package client;

import com.google.gson.Gson;
import model.*;
import exception.*;
import request.*;
import result.*;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        this.serverUrl = url;
    }

    public RegisterResult register(UserData user) throws DataAccessException {
        var request = buildRequest("POST", "/user", new RegisterRequest(user.username(), user.password(), user.email()), null);
        var response = sendRequest(request);
        var result = handleResponse(response, AuthData.class );
        return new RegisterResult(result.username(), result.authToken());
    }

    public LoginResult login(String username, String password) throws DataAccessException {
        var request = buildRequest("POST", "/session", new LoginRequest(username, password), null);
        var response = sendRequest(request);
        var result = handleResponse(response, AuthData.class);
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
        return handleResponse(response, GameList.class);
    }

    public NewGameResult createGame(String authToken, String name) throws DataAccessException {
        var request = buildRequest("POST", "/game", new NewGameRequest(name), authToken);
        var response = sendRequest(request);
        var result = handleResponse(response, GameData.class);
        return new NewGameResult(result.gameID());
    }

    public void joinGame(String authToken, int gameID, String color) throws DataAccessException {
        var request = buildRequest("PUT", "/game", new JoinRequest(color, gameID), authToken);
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
                Response error = new Gson().fromJson(body, Response.class);
                throw new DataAccessException(error.message() + "\n");
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
