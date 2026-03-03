package server;

import com.google.gson.Gson;
import dataaccess.*;

import io.javalin.*;
import io.javalin.http.Context;
import model.*;
import request.*;
import result.*;
import service.*;
import service.UserService.*;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public Server(){
        this(new MemoryUserDAO(), new MemoryAuthDAO(), new MemoryGameDAO());
    }

    public Server(UserDAO user, AuthDAO auth, GameDAO game) {
        this.userService = new UserService(user, auth);
        this.authService = new AuthService(auth);
        this.gameService = new GameService(game, auth);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::addUser);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGames);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
        javalin.delete("/db", this::clear);

        // Register your endpoints and exception handlers here.


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    private void addUser(Context ctx){
        RegisterResult result;
        ctx.contentType("application/json");
        try {
            result = userService.register(new Gson().fromJson(ctx.body(), RegisterRequest.class));
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (BadRequestException e){
            Response error = new Response(e.getMessage());
            ctx.contentType("application/json");
            ctx.status(400);
            ctx.result(new Gson().toJson(error));
        } catch (DataAccessException e) {
            Response error = new Response(e.getMessage());
            ctx.contentType("application/json");
            ctx.status(403);
            ctx.result(new Gson().toJson(error));
        }
    }

    private void login(Context ctx){
        LoginResult result;
        ctx.contentType("application/json");
        try {
            result = userService.login(new Gson().fromJson(ctx.body(), LoginRequest.class));
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (BadRequestException e){
            Response error = new Response(e.getMessage());
            ctx.contentType("application/json");
            ctx.status(400);
            ctx.result(new Gson().toJson(error));
        } catch (UnauthorizedException e) {
            Response error = new Response(e.getMessage());
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(new Gson().toJson(error));
        }
    }

    private void logout(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            userService.logout(new LogoutRequest(authToken));
            ctx.status(200);
            ctx.result();
        } catch (UnauthorizedException e) {
            Response error = new Response(e.getMessage());
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(new Gson().toJson(error));
        }
    }

    private void listGames(Context ctx){
        String authToken = ctx.header("authorization");
        ctx.contentType("application/json");
        try {
            GameList games = gameService.listGames(authToken);
            ctx.status(200);
            ctx.result(new Gson().toJson(games));
        } catch (UnauthorizedException e) {
            Response error = new Response(e.getMessage());
            ctx.status(401);
            ctx.result(new Gson().toJson(error));
        }
    }

    private void createGame(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            NewGameResult result = gameService.createGame(new Gson().fromJson(ctx.body(), NewGameRequest.class), authToken);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (UnauthorizedException e) {
            Response error = new Response(e.getMessage());
            ctx.status(401);
            ctx.result(new Gson().toJson(error));
        } catch (BadRequestException e) {
            Response error = new Response(e.getMessage());
            ctx.contentType("application/json");
            ctx.status(400);
            ctx.result(new Gson().toJson(error));
        }


    }

    private void joinGame(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            gameService.joinGame(new Gson().fromJson(ctx.body(), JoinRequest.class), authToken);
            ctx.status(200);
            ctx.result();
        } catch (BadRequestException e) {
            Response error = new Response(e.getMessage());
            ctx.contentType("application/json");
            ctx.status(400);
            ctx.result(new Gson().toJson(error));
        } catch (UnauthorizedException e) {
            Response error = new Response(e.getMessage());
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(new Gson().toJson(error));
        } catch (DataAccessException e) {
            Response error = new Response(e.getMessage());
            ctx.contentType("application/json");
            ctx.status(403);
            ctx.result(new Gson().toJson(error));
        }
    }

    private void clear(Context ctx){
        userService.clear();
        authService.clear();
        gameService.clear();
        ctx.status(200);
        ctx.result();
    }

    public void stop() {
        javalin.stop();
    }

}
