package service;

import org.junit.jupiter.api.*;
import dataaccess.*;
import request.*;
import result.*;
import exception.*;

import java.util.Objects;

import static dataaccess.MemoryAuthDAO.generateToken;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ServiceTests {


    private static MemoryUserDAO userDAO;
    private static MemoryAuthDAO authDAO;
    private static MemoryGameDAO gameDAO;
    private static UserService users;
    private static AuthService auths;
    private static GameService games;

    @BeforeAll
    public static void init() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        users = new UserService(userDAO, authDAO);
        auths = new AuthService(authDAO);
        games = new GameService(gameDAO, authDAO);
    }

    @BeforeEach
    public void clearThings(){
        userDAO.clearUsers();
        authDAO.clearAuths();
        gameDAO.clearGames();
    }

    @Test
    public void registerSuccessTest() throws DataAccessException, SQLDataAccessException {
        RegisterResult result = users.register(new RegisterRequest("levi", "1234", "email"));
        assert (result.authToken() != null && Objects.equals(result.username(), "levi"));
    }

    @Test
    public void registerFailTest() throws DataAccessException, SQLDataAccessException {
        users.register(new RegisterRequest("levi", "1234", "email"));
        assertThrows(DataAccessException.class, () -> users.register(new RegisterRequest("levi", "5678", "email")));
    }

    @Test
    public void loginSuccessTest() throws DataAccessException, SQLDataAccessException {
        users.register(new RegisterRequest("levi", "1234", "email"));
        LoginResult result = users.login(new LoginRequest("levi", "1234"));
        assert (Objects.equals(result.username(), "levi"));
        assert (result.authToken() != null);
    }

    @Test
    public void loginFailTest() {
        assertThrows(UnauthorizedException.class, () -> users.login(new LoginRequest("levi", "1234")));
    }

    @Test
    public void logoutSuccessTest() throws DataAccessException, SQLDataAccessException{
        RegisterResult result = users.register(new RegisterRequest("levi", "1234", "email"));
        users.logout(new LogoutRequest(result.authToken()));
        assert (authDAO.getAuth(result.authToken()) == null);
    }

    @Test
    public void logoutFailTest() {
        assertThrows(UnauthorizedException.class, () -> users.logout(new LogoutRequest(generateToken())));
    }

    @Test
    public void createGameSuccessTest() throws DataAccessException, SQLDataAccessException{
        RegisterResult register =  users.register(new RegisterRequest("levi", "1234", "email"));
        NewGameResult result = games.createGame(new NewGameRequest("test game"), register.authToken());

        assert (gameDAO.getGame(result.gameID()) != null);
    }

    @Test
    public void createGameFailTest() {
        assertThrows(UnauthorizedException.class, () -> games.createGame(new NewGameRequest("name"), generateToken()));
    }

    @Test
    public void joinGameSuccessTest() throws DataAccessException, SQLDataAccessException{
        RegisterResult register =  users.register(new RegisterRequest("levi", "1234", "email"));
        NewGameResult game = games.createGame(new NewGameRequest("test game"), register.authToken());
        games.joinGame(new JoinRequest("WHITE", game.gameID()), register.authToken());

        assert (Objects.equals(gameDAO.getGame(game.gameID()).whiteUsername(), "levi"));
    }

    @Test
    public void joinGameFailTest() throws DataAccessException, SQLDataAccessException{
        RegisterResult register =  users.register(new RegisterRequest("levi", "1234", "email"));
        games.createGame(new NewGameRequest("test game"), register.authToken());

        assertThrows(BadRequestException.class, () -> games.joinGame(new JoinRequest("WHITE", 5), register.authToken()));
    }

    @Test
    public void listGamesSuccessTest() throws DataAccessException, SQLDataAccessException{
        RegisterResult register = users.register(new RegisterRequest("levi", "1234", "email"));
        games.createGame(new NewGameRequest("test game"), register.authToken());
        users.logout(new LogoutRequest(register.authToken()));
        register = users.register(new RegisterRequest("lvi", "1234", "email"));
        games.createGame(new NewGameRequest("test game"), register.authToken());
        users.logout(new LogoutRequest(register.authToken()));
        register = users.register(new RegisterRequest("lei", "1234", "email"));
        games.createGame(new NewGameRequest("test game"), register.authToken());
        users.logout(new LogoutRequest(register.authToken()));
        register = users.register(new RegisterRequest("evi", "1234", "email"));
        games.createGame(new NewGameRequest("test game"), register.authToken());

        assert (games.listGames(register.authToken()) != null);
    }

    @Test
    public void listGamesFailTest() {
        assertThrows(UnauthorizedException.class, () -> users.logout(new LogoutRequest(generateToken())));
    }

    @Test
    public void clearSuccessTest() throws DataAccessException, SQLDataAccessException{
        RegisterResult register = users.register(new RegisterRequest("levi", "1234", "email"));
        games.createGame(new NewGameRequest("test game"), register.authToken());
        users.logout(new LogoutRequest(register.authToken()));
        register = users.register(new RegisterRequest("lvi", "1234", "email"));
        games.createGame(new NewGameRequest("test game"), register.authToken());
        users.logout(new LogoutRequest(register.authToken()));
        register = users.register(new RegisterRequest("lei", "1234", "email"));
        games.createGame(new NewGameRequest("test game"), register.authToken());
        users.logout(new LogoutRequest(register.authToken()));
        register = users.register(new RegisterRequest("evi", "1234", "email"));
        games.createGame(new NewGameRequest("test game"), register.authToken());

        users.clear();
        games.clear();
        auths.clear();

        Assertions.assertThrows(BadRequestException.class, () -> gameDAO.getGame(1));
        Assertions.assertNull(userDAO.getUser("levi"), "users not cleared");
        Assertions.assertNull(authDAO.getAuth(register.authToken()), "auths not cleared");
    }

    @Test
    public void clearNoneTest() throws DataAccessException, SQLDataAccessException{
        users.clear();
        auths.clear();
        games.clear();
    }

}
