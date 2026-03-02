package server;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryUserDAO;

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

    public Server(){
        this(new UserService(new MemoryUserDAO(), new MemoryAuthDAO()));
    }

    public Server(UserService userService) {
        this.userService = userService;
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
        RegisterResult result = null;
        ctx.contentType("application/json");
        try {
            result = userService.register(new Gson().fromJson(ctx.body(), RegisterRequest.class));
            ctx.result(new Gson().toJson(result));
        } catch (BadRequestException e){
            Response error = new Response(e.getMessage());
            ctx.status(400);
            ctx.result(new Gson().toJson(error));
        } catch (DataAccessException e) {
            Response error = new Response(e.getMessage());
            ctx.status(403);
            ctx.result(new Gson().toJson(error));
        }
    }

    private void login(Context ctx){

    }

    private void logout(Context ctx){
        ctx.header("authorization:");
    }

    private void listGames(Context ctx){

    }

    private void createGame(Context ctx){

    }

    private void joinGame(Context ctx){

    }

    private void clear(Context ctx){

    }

    public void stop() {
        javalin.stop();
    }
}
