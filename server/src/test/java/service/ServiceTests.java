package service;

import org.junit.jupiter.api.*;
import dataaccess.*;
import request.*;
import result.*;

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
    public void registerSuccessTest() throws DataAccessException {
        RegisterResult result = users.register(new RegisterRequest("levi", "1234", "email"));
        assert (result.authToken() != null && Objects.equals(result.username(), "levi"));
    }

    @Test
    public void registerFailTest() throws DataAccessException {
        users.register(new RegisterRequest("levi", "1234", "email"));
        assertThrows(DataAccessException.class, () -> {
            users.register(new RegisterRequest("levi", "5678", "email"));;
        });
    }

    @Test
    public void loginSuccessTest() throws DataAccessException, BadRequestException {
        users.register(new RegisterRequest("levi", "1234", "email"));
        LoginResult result = users.login(new LoginRequest("levi", "1234"));
        assert (Objects.equals(result.username(), "levi"));
        assert (result.authToken() != null);
    }

    @Test
    public void loginFailTest() throws DataAccessException {
        assertThrows(UnauthorizedException.class, () -> {
            users.login(new LoginRequest("levi", "1234"));
        });
    }

    @Test
    public void logoutSuccessTest() throws DataAccessException{
        RegisterResult result = users.register(new RegisterRequest("levi", "1234", "email"));
        users.logout(new LogoutRequest(result.authToken()));
        assert (authDAO.getAuth(result.authToken()) == null);
    }

    @Test
    public void logoutFailTest() throws DataAccessException{
        assertThrows(UnauthorizedException.class, () -> {
            users.logout(new LogoutRequest(generateToken()));
        });
    }

}
