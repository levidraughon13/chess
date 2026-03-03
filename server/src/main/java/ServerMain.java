import chess.*;
import dataaccess.*;
import server.Server;
import service.AuthService;
import service.GameService;
import service.UserService;

public class ServerMain {
    public static void main(String[] args) {
        UserDAO userDataAccess = new MemoryUserDAO();
        GameDAO gameDataAccess = new MemoryGameDAO();
        AuthDAO authDataAccess = new MemoryAuthDAO();
        var port = 8080;
        if (args.length >= 1) {
            port = Integer.parseInt(args[0]);
        }
        Server server = new Server(userDataAccess, authDataAccess, gameDataAccess);
        server.run(port);
        System.out.println("♕ 240 Chess Server");
    }
}
