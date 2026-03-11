package server;

import com.google.gson.Gson;
import dataaccess.*;

import io.javalin.*;
import io.javalin.http.Context;
import request.*;
import result.*;
import service.*;

public class Server {

    private final Javalin javalin;
    private final UserService userService;
    private final AuthService authService;
    private final GameService gameService;

    public Server(){
        this(new SQLUserDAO(), new SQLAuthDAO(), new SQLGameDAO());
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
            badRequest(ctx, e.getMessage());
        } catch (DataAccessException e) {
            other(ctx, e.getMessage());
        } catch (SQLDataAccessException e) {
            sqlException(ctx, e.getMessage());
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
            badRequest(ctx, e.getMessage());
        } catch (UnauthorizedException e) {
            unauthorized(ctx, e.getMessage());
        } catch (SQLDataAccessException e) {
            sqlException(ctx, e.getMessage());
        }
    }

    private void logout(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            userService.logout(new LogoutRequest(authToken));
            ctx.status(200);
            ctx.result();
        } catch (UnauthorizedException e) {
            unauthorized(ctx, e.getMessage());
        } catch (SQLDataAccessException e) {
            sqlException(ctx, e.getMessage());
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
            unauthorized(ctx, e.getMessage());
        } catch (SQLDataAccessException e) {
            sqlException(ctx, e.getMessage());
        }
    }

    private void createGame(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            NewGameResult result = gameService.createGame(new Gson().fromJson(ctx.body(), NewGameRequest.class), authToken);
            ctx.status(200);
            ctx.result(new Gson().toJson(result));
        } catch (UnauthorizedException e) {
            unauthorized(ctx, e.getMessage());
        } catch (BadRequestException e) {
            badRequest(ctx, e.getMessage());
        } catch (SQLDataAccessException e) {
            sqlException(ctx, e.getMessage());
        }
    }

    private void joinGame(Context ctx){
        String authToken = ctx.header("authorization");
        try {
            gameService.joinGame(new Gson().fromJson(ctx.body(), JoinRequest.class), authToken);
            ctx.status(200);
            ctx.result();
        } catch (BadRequestException e) {
            badRequest(ctx, e.getMessage());
        } catch (UnauthorizedException e) {
            unauthorized(ctx, e.getMessage());
        } catch (DataAccessException e) {
            other(ctx, e.getMessage());
        } catch (SQLDataAccessException e) {
            sqlException(ctx, e.getMessage());
        }
    }

    private void clear(Context ctx){
        try {
            userService.clear();
            authService.clear();
            gameService.clear();
            ctx.status(200);
            ctx.result();
        } catch (SQLDataAccessException e) {
            sqlException(ctx, e.getMessage());
        }
    }

    public void stop() {
        javalin.stop();
    }

    private void badRequest(Context ctx, String e) {
        Response error = new Response(e);
        ctx.contentType("application/json");
        ctx.status(400);
        ctx.result(new Gson().toJson(error));
    }

    private void unauthorized(Context ctx, String e){
        Response error = new Response(e);
        ctx.contentType("application/json");
        ctx.status(401);
        ctx.result(new Gson().toJson(error));
    }

    private void sqlException(Context ctx, String e) {
        Response error = new Response(e);
        ctx.contentType("application/json");
        ctx.status(500);
        ctx.result(new Gson().toJson(error));
    }

    private void other(Context ctx, String e){
        Response error = new Response(e);
        ctx.contentType("application/json");
        ctx.status(403);
        ctx.result(new Gson().toJson(error));
    }
}
