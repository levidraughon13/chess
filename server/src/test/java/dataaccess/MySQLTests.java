package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
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
    private static final SQLUserDAO userDAO = new SQLUserDAO();
    private static final SQLAuthDAO authDAO = new SQLAuthDAO();
    private static final SQLGameDAO gameDAO = new SQLGameDAO();
    static Connection conn;
    String statement;

    @BeforeAll
    public static void setUp() throws DataAccessException {
        conn = DatabaseManager.getConnection();
    }

    @BeforeEach
    public void clearAll() throws SQLDataAccessException {
        userDAO.clearUsers();
        authDAO.clearAuths();
        gameDAO.clearGames();

    }

    @Test
    public void createUserSuccessTest() throws SQLDataAccessException, SQLException {
        userDAO.createUser("Levi", "1234", "email");
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
        userDAO.createUser("Levi", "1234", "email");
        Assertions.assertThrows(SQLDataAccessException.class, () -> userDAO.createUser("Levi", "134", "email"));
    }


    @Test
    public void getUserSuccessTest() throws SQLDataAccessException {
        userDAO.createUser("Levi", "1234", "email");
        statement = "SELECT username, password FROM users WHERE username=?";
        UserData user = userDAO.getUser("Levi");
        UserData expected = new UserData("Levi", "1234", "email");
        Assertions.assertEquals(expected.username(), user.username());
        assert(BCrypt.checkpw(expected.password(), user.password()));
    }

    @Test
    public void getUserFailTest() throws SQLDataAccessException {
        Assertions.assertNull(userDAO.getUser("Levi"));
    }


    @Test
    public void matchPasswordSuccessTest(){}

    @Test
    public void matchPasswordFailTest(){}


    @Test
    public void createAuthSuccessTest() throws SQLException, SQLDataAccessException {
        String authToken = authDAO.createAuth("Levi");
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
        Assertions.assertThrows(NullPointerException.class, () -> authDAO.createAuth(null));
    }


    @Test
    public void getAuthSuccessTest() throws SQLDataAccessException {
        String authToken = authDAO.createAuth("Levi");
        AuthData expected = new AuthData(authToken, "Levi");
        Assertions.assertEquals(expected, authDAO.getAuth(authToken));
    }

    @Test
    public void getAuthFailTest() throws SQLDataAccessException {
        Assertions.assertNull(authDAO.getAuth("Levi"));
    }


    @Test
    public void deleteAuthSuccessTest() throws SQLDataAccessException, SQLException {
        authDAO.createAuth("Levi");
        authDAO.createAuth("levi");
        authDAO.createAuth("Lev");

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
        authDAO.createAuth("Levi");
        authDAO.createAuth("levi");
        authDAO.createAuth("Lev");
        authDAO.deleteAuth("1234");

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
        gameDAO.createGame("new game");
    }

    @Test
    public void createGameFailTest() {
        Assertions.assertThrows(SQLDataAccessException.class, () -> gameDAO.createGame(null));
    }


    @Test
    public void getGameSuccessTest() throws SQLDataAccessException, BadRequestException {
        int id = gameDAO.createGame("new game");
        GameData game = gameDAO.getGame(id);
        Assertions.assertNotNull(game.game());
    }

    @Test
    public void getGameFailTest() {
        Assertions.assertThrows(BadRequestException.class, () -> gameDAO.getGame(1));
    }


    @Test
    public void listGamesSuccessTest() throws SQLDataAccessException {
        gameDAO.createGame("new game");
        gameDAO.createGame("new game");
        gameDAO.createGame("new game");
        HashMap<Integer, GameData> expected = new HashMap<>();
        expected.put(1, new GameData(1, null, null, "new game", new ChessGame()));
        expected.put(2, new GameData(2, null, null, "new game", new ChessGame()));
        expected.put(3, new GameData(3, null, null, "new game", new ChessGame()));

        Assertions.assertEquals(expected, gameDAO.listGames());
    }

    @Test
    public void listGamesFailTest() throws SQLDataAccessException {
        gameDAO.createGame("new game");
        gameDAO.createGame("new game");
        gameDAO.createGame("new game");
        gameDAO.clearGames();
        Assertions.assertEquals(new HashMap<>(), gameDAO.listGames());
    }


    @Test
    public void joinGameSuccessTest() throws SQLDataAccessException, BadRequestException {
        gameDAO.createGame("new game");
        gameDAO.joinGame(1, "Levi", "WHITE");
        GameData game = gameDAO.getGame(1);
        Assertions.assertEquals("Levi", game.whiteUsername());
    }

    @Test
    public void joinGameFailTest() throws SQLDataAccessException, BadRequestException {
        gameDAO.createGame("new game");
        gameDAO.joinGame(1, "Levi", "WHITE");
        Assertions.assertThrows(BadRequestException.class, () -> gameDAO.joinGame(1, "Mike", "WHIE"));
    }

}
