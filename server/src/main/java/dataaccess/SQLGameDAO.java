package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import exception.*;
import java.util.HashMap;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class SQLGameDAO implements GameDAO{

    public SQLGameDAO(){
        try {
            String[] createStatements = {
                    """
            CREATE TABLE IF NOT EXISTS  games (
              `gameID` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(255),
              `blackUsername` varchar(255),
              `gameName` varchar(255) NOT NULL,
              `game` TEXT DEFAULT NULL,
              PRIMARY KEY (`gameID`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
            };
            MySqlDAO.configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int createGame(String gameName) throws SQLDataAccessException {
        String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?)";
        return executeUpdate(statement, null, null, gameName, new ChessGame());
    }

    @Override
    public HashMap<Integer, GameData> listGames() throws SQLDataAccessException {
        HashMap<Integer, GameData> allGames = new HashMap<>();
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT * FROM games";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        allGames.put(rs.getInt(1), getGame(rs.getInt(1)));
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLDataAccessException(String.format("Unable to read data: %s", e.getMessage()));
        }
        return allGames;
    }


    @Override
    public void joinGame(Integer gameID, String username, String team) throws BadRequestException, SQLDataAccessException {
        String statement;
        if (Objects.equals(team, "WHITE")){
            statement = "UPDATE games SET whiteUsername=? WHERE gameID=?";
        } else if (Objects.equals(team, "BLACK")){
            statement = "UPDATE games SET blackUsername=? WHERE gameID=?";
        } else {
            throw new BadRequestException("Error: bad request, invalid team color");
        }
        executeUpdate(statement, username, gameID);
    }

    @Override
    public GameData getGame(Integer gameID) throws SQLDataAccessException, BadRequestException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM games WHERE gameID=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        ChessGame game = new Gson().fromJson(rs.getString(5), ChessGame.class);
                        return new GameData(rs.getInt(1),rs.getString(2),rs.getString(3), rs.getString(4), game);
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLDataAccessException(String.format("Error: Unable to read data: %s", e.getMessage()));
        }
        throw new BadRequestException("Error: bad request");
    }

    @Override
    public void clearGames() throws SQLDataAccessException {
        var statement = "TRUNCATE games";
        executeUpdate(statement);
    }

    @Override
    public void updateGame(Integer gameID, ChessGame game) throws SQLDataAccessException {
        var statement = "UPDATE games SET game=? WHERE gameID=?";
        executeUpdate(statement, game, gameID);
    }


    private int executeUpdate(String statement, Object... params) throws SQLDataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case ChessGame p -> ps.setString(i + 1, new Gson().toJson(p));
                        case null -> ps.setNull(i + 1, NULL);
                        default -> throw new IllegalStateException("Unexpected value: " + param);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new SQLDataAccessException(String.format("Error: unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
