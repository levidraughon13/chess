import chess.*;
import dataaccess.*;
import server.Server;
import service.AuthService;
import service.GameService;
import service.UserService;

public class ServerMain {
    public static void main(String[] args) {
        UserDAO UserDataAccess = new MemoryUserDAO();
        GameDAO GameDataAccess = new MemoryGameDAO();
        AuthDAO AuthDataAccess = new MemoryAuthDAO();
        var port = 8080;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        Server server = new Server(UserDataAccess, AuthDataAccess, GameDataAccess);
        server.run(port);
        System.out.println("♕ 240 Chess Server");
    }
}
