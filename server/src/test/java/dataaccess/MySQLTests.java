package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class MySQLTests {
    private static final SQLUserDAO USER_DAO = new SQLUserDAO();
    private static final SQLAuthDAO AUTH_DAO = new SQLAuthDAO();
    private static final SQLGameDAO GAME_DAO = new SQLGameDAO();
    static Connection conn;
    String statement;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        conn = DatabaseManager.getConnection();
    }

    @BeforeEach
    public void clearAll() throws SQLDataAccessException {
        USER_DAO.clearUsers();
        AUTH_DAO.clearAuths();
        GAME_DAO.clearGames();

    }

    @Test
    public void createUserSuccessTest() throws SQLDataAccessException, SQLException {
        USER_DAO.createUser("Levi", "1234", "email");
        statement = "SELECT username, password FROM users WHERE username=?";
        PreparedStatement p = conn.prepareStatement(statement);
        p.setString(1, "Levi");

        ResultSet r = p.executeQuery();
        r.next();

        assert ("Levi".equals(r.getString("username")));
        assert(BCrypt.checkpw("1234", r.getString("password")));

    }

    @Test
    public void createUserFailTest() throws SQLDataAccessException {
        USER_DAO.createUser("Levi", "1234", "email");
        Assertions.assertThrows(SQLDataAccessException.class, () -> USER_DAO.createUser("Levi", "134", "email"));
    }


    @Test
    public void getUserSuccessTest() throws SQLDataAccessException {
        USER_DAO.createUser("Levi", "1234", "email");
        statement = "SELECT username, password FROM users WHERE username=?";
        UserData user = USER_DAO.getUser("Levi");
        UserData expected = new UserData("Levi", "1234", "email");
        Assertions.assertEquals(expected.username(), user.username());
        assert(BCrypt.checkpw(expected.password(), user.password()));
    }

    @Test
    public void getUserFailTest() throws SQLDataAccessException {
        Assertions.assertNull(USER_DAO.getUser("Levi"));
    }


    @Test
    public void matchPasswordSuccessTest(){}

    @Test
    public void matchPasswordFailTest(){}


    @Test
    public void createAuthSuccessTest() throws SQLException, SQLDataAccessException {
        String authToken = AUTH_DAO.createAuth("Levi");
        statement = "SELECT authToken, username FROM auths WHERE username=?";
        PreparedStatement p = conn.prepareStatement(statement);
        p.setString(1, "Levi");

        ResultSet r = p.executeQuery();
        r.next();

        assert ("Levi".equals(r.getString("username")));
        assert (authToken.equals(r.getString("authToken")));
    }

    @Test
    public void createAuthFailTest(){
        Assertions.assertThrows(NullPointerException.class, () -> AUTH_DAO.createAuth(null));
    }


    @Test
    public void getAuthSuccessTest() throws SQLDataAccessException {
        String authToken = AUTH_DAO.createAuth("Levi");
        AuthData expected = new AuthData(authToken, "Levi");
        Assertions.assertEquals(expected, AUTH_DAO.getAuth(authToken));
    }

    @Test
    public void getAuthFailTest() throws SQLDataAccessException {
        Assertions.assertNull(AUTH_DAO.getAuth("Levi"));
    }


    @Test
    public void deleteAuthSuccessTest() throws SQLDataAccessException, SQLException {
        AUTH_DAO.createAuth("Levi");
        AUTH_DAO.createAuth("levi");
        AUTH_DAO.createAuth("Lev");

        statement = "SELECT username FROM auths";
        PreparedStatement p = conn.prepareStatement(statement);

        ResultSet r = p.executeQuery();
        r.next();
        do {
            Assertions.assertNotNull (r.getString("username"));
        } while (r.next());

    }

    @Test
    public void deleteAuthFailTest() throws SQLDataAccessException, SQLException {
        AUTH_DAO.createAuth("Levi");
        AUTH_DAO.createAuth("levi");
        AUTH_DAO.createAuth("Lev");
        AUTH_DAO.deleteAuth("1234");

        statement = "SELECT username FROM auths";
        PreparedStatement p = conn.prepareStatement(statement);

        ResultSet r = p.executeQuery();
        r.next();
        Assertions.assertNotNull(r.getString("username"));
        r.next();
        Assertions.assertNotNull(r.getString("username"));
        r.next();
        Assertions.assertNotNull(r.getString("username"));
    }


    @Test
    public void createGameSuccessTest() throws SQLDataAccessException {
        GAME_DAO.createGame("new game");
    }

    @Test
    public void createGameFailTest() {
        Assertions.assertThrows(SQLDataAccessException.class, () -> GAME_DAO.createGame(null));
    }


    @Test
    public void getGameSuccessTest() throws SQLDataAccessException, BadRequestException {
        int id = GAME_DAO.createGame("new game");
        GameData game = GAME_DAO.getGame(id);
        Assertions.assertNotNull(game.game());
    }

    @Test
    public void getGameFailTest() {
        Assertions.assertThrows(BadRequestException.class, () -> GAME_DAO.getGame(1));
    }


    @Test
    public void listGamesSuccessTest() throws SQLDataAccessException {
        GAME_DAO.createGame("new game");
        GAME_DAO.createGame("new game");
        GAME_DAO.createGame("new game");
        HashMap<Integer, GameData> expected = new HashMap<>();
        expected.put(1, new GameData(1, null, null, "new game", new ChessGame()));
        expected.put(2, new GameData(2, null, null, "new game", new ChessGame()));
        expected.put(3, new GameData(3, null, null, "new game", new ChessGame()));

        Assertions.assertEquals(expected, GAME_DAO.listGames());
    }

    @Test
    public void listGamesFailTest() throws SQLDataAccessException {
        GAME_DAO.createGame("new game");
        GAME_DAO.createGame("new game");
        GAME_DAO.createGame("new game");
        GAME_DAO.clearGames();
        Assertions.assertEquals(new HashMap<>(), GAME_DAO.listGames());
    }


    @Test
    public void joinGameSuccessTest() throws SQLDataAccessException, BadRequestException {
        GAME_DAO.createGame("new game");
        GAME_DAO.joinGame(1, "Levi", "WHITE");
        GameData game = GAME_DAO.getGame(1);
        Assertions.assertEquals("Levi", game.whiteUsername());
    }

    @Test
    public void joinGameFailTest() throws SQLDataAccessException, BadRequestException {
        GAME_DAO.createGame("new game");
        GAME_DAO.joinGame(1, "Levi", "WHITE");
        Assertions.assertThrows(BadRequestException.class, () -> GAME_DAO.joinGame(1, "Mike", "WHIE"));
    }

}
