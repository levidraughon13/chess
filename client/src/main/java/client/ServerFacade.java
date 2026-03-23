package client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.*;
import exception.*;
import request.*;
import result.*;

import java.lang.reflect.Type;
import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public RegisterResult register(UserData user) throws DataAccessException {
        var request = buildRequest("POST", "/user", new RegisterRequest(user.username(), user.password(), user.email()), null);
        var response = sendRequest(request);
        AuthData result = handleResponse(response, AuthData.class );
        return new RegisterResult(result.username(), result.authToken());
    }

    /*
    javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
        javalin.delete("/db", this::clear);
    */
    public LoginResult login(String username, String password) throws DataAccessException {
        var request = buildRequest("POST", "/session", new LoginRequest(username, password), null);
        var response = sendRequest(request);
        AuthData result = handleResponse(response, AuthData.class);
        return new LoginResult(result.username(), result.authToken());
    }
    public void logout(String authToken) throws DataAccessException {
        var request = buildRequest("DELETE", "/session", null, authToken);
        var response = sendRequest(request);
        handleResponse(response, null);
    }
    public GameList listGames(String authToken) throws DataAccessException {
        var request = buildRequest("GET", "/game", null, authToken);
        var response = sendRequest(request);
        List<GameInfo>  result = handleResponse(response, List.class);
        return new GameList(result);
    }
    public NewGameResult createGame(String name) throws DataAccessException {
        var request = buildRequest("POST", "/game", new NewGameRequest(name), null);
        var response = sendRequest(request);
        GameData result = handleResponse(response, GameData.class);
        return new NewGameResult(result.gameID());
    }
    public void joinGame(String color, int gameID) throws DataAccessException {
        var request = buildRequest("PUT", "/game", new JoinRequest(color, gameID), null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }
    public void clear() throws DataAccessException {
        var request = buildRequest("DELETE", "/db", null, null);
        var response = sendRequest(request);
        handleResponse(response, null);
    }


    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));
        if (authToken != null) {
            request.header("Authorization", authToken);
        }
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws BadRequestException {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws DataAccessException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new DataAccessException((String) new Gson().fromJson(response.body(), responseClass));
            }
            throw new DataAccessException("other failure: " + status);
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
