import client.ServerFacade;
import exception.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;

import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        facade.clear();
    }


    @Test
    void register() throws Exception {
        var authData = facade.register(new UserData("username", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void login() throws Exception {
        var newUser = facade.register(new UserData("username", "password", "email"));
        facade.logout(newUser.authToken());
        var result = facade.login("username", "password");
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    void logout() throws Exception {

    }

    @Test
    void createGame() throws Exception {

    }

    @Test
    void joinGame() throws Exception {

    }

    @Test
    void listGames() throws Exception {

    }
}
