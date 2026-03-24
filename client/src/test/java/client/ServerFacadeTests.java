import client.InGameClient;
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
    static void stopServer() throws DataAccessException {
        facade.clear();
        server.stop();
    }

    @BeforeEach
    public void clear() throws DataAccessException {
        facade.clear();
    }


    @Test
    void registerTest() throws Exception {
        var authData = facade.register(new UserData("username", "password", "p1@email.com"));
        assertTrue(authData.authToken().length() > 10);
    }

    @Test
    void registerFailTest() throws Exception {
        var authData = facade.register(new UserData("username", "password", "p1@email.com"));
        facade.logout(authData.authToken());
        assertThrows(Exception.class, () -> facade.register(new UserData("username", "p2", "e2")));
    }

    @Test
    void loginTest() throws Exception {
        var newUser = facade.register(new UserData("username", "password", "email"));
        facade.logout(newUser.authToken());
        var result = facade.login("username", "password");
        assertTrue(result.authToken().length() > 10);
    }

    @Test
    void loginFailTest() {
        assertThrows(Exception.class, () -> facade.login("username", "password"));
    }

    @Test
    void logoutTest() throws Exception {
        var newUser = facade.register(new UserData("username", "password", "email"));
        facade.logout(newUser.authToken());
        assertThrows(Exception.class, () -> facade.createGame(newUser.authToken(),"game"));
    }

    @Test
    void logoutFailTest() throws Exception {
        var newUser = facade.register(new UserData("username", "password", "email"));
        facade.logout(newUser.authToken());
        assertThrows(Exception.class, () -> facade.logout(newUser.authToken()));
    }

    @Test
    void createGameTest() throws Exception {
        var newUser = facade.register(new UserData("username", "password", "email"));
        var result = facade.createGame(newUser.authToken(),"game");
        assertEquals(1, result.gameID());
    }

    @Test
    void createGameFailTest() {
        assertThrows(Exception.class, () -> facade.createGame("1234567","game"));
    }

    @Test
    void joinGameTest() throws Exception {
        var newUser = facade.register(new UserData("username", "password", "email"));
        var game = facade.createGame(newUser.authToken(),"game");
        facade.joinGame(newUser.authToken(), game.gameID(), "WHITE");
        facade.logout(newUser.authToken());
        var newUser2 = facade.register(new UserData("username2", "password2", "email2"));
        assertThrows(Exception.class, () -> facade.joinGame(newUser2.authToken(), game.gameID(), "WHITE"));
    }

    @Test
    void joinGameFailTest() throws Exception {
        var newUser = facade.register(new UserData("username", "password", "email"));
        var game = facade.createGame(newUser.authToken(),"game");
        facade.joinGame(newUser.authToken(), game.gameID(), "WHITE");
        facade.logout(newUser.authToken());
        var newUser2 = facade.register(new UserData("username2", "password2", "email2"));
        assertThrows(Exception.class, () -> facade.joinGame(newUser2.authToken(), game.gameID()+1, "BLACK"));
    }

    @Test
    void listGamesTest() throws Exception {
        var newUser = facade.register(new UserData("username", "password", "email"));
        var game = facade.createGame(newUser.authToken(),"game");
        facade.joinGame(newUser.authToken(), game.gameID(), "WHITE");
        facade.createGame(newUser.authToken(),"game2");
        facade.logout(newUser.authToken());
        var newUser2 = facade.register(new UserData("username2", "password2", "email2"));
        facade.createGame(newUser2.authToken(),"game3");
        facade.createGame(newUser2.authToken(),"game4");
        var result = facade.listGames(newUser2.authToken());
        assertEquals(4, result.games().size());
    }

    @Test
    void listGamesFailTest() throws Exception {
        var newUser = facade.register(new UserData("username", "password", "email"));
        var game = facade.createGame(newUser.authToken(),"game");
        facade.joinGame(newUser.authToken(), game.gameID(), "WHITE");
        facade.createGame(newUser.authToken(),"game2");
        facade.logout(newUser.authToken());
        var newUser2 = facade.register(new UserData("username2", "password2", "email2"));
        facade.createGame(newUser2.authToken(),"game3");
        facade.createGame(newUser2.authToken(),"game4");
        facade.logout(newUser2.authToken());
        assertThrows(Exception.class, () -> facade.listGames(newUser2.authToken()));
    }

    @Test
    void print() {
        InGameClient testClient = new InGameClient(facade, "1");
        System.out.print(testClient.printInitialBoard());
    }
}
