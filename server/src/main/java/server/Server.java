package server;

import io.javalin.*;
import io.javalin.http.Context;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;
import service.UserService.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::addUser);
        // Register your endpoints and exception handlers here.


    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    private void addUser(Context ctx){
        RegisterResult result = UserService.register(ctx.bodyAsClass(RegisterRequest.class));
        ctx.contentType("application/json");
        ctx.json(result);
    }

    public void stop() {
        javalin.stop();
    }
}
