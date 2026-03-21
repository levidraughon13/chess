package dataaccess;

import model.AuthData;
import exception.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;


public class SQLAuthDAO implements AuthDAO{

    public SQLAuthDAO(){
        try {
            String[] createStatements = {
                    """
            CREATE TABLE IF NOT EXISTS  auths (
              `authToken` varchar(255) NOT NULL,
              `username` varchar(255) NOT NULL,
              PRIMARY KEY (`authToken`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
            };
            MySqlDAO.configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws SQLDataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auths WHERE authToken=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new AuthData(rs.getString(1),rs.getString(2));
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLDataAccessException(String.format("Error, unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void deleteAuth(String authToken) throws SQLDataAccessException {
        var statement = "DELETE FROM auths WHERE authToken=?";
        authExecuteUpdate(statement, authToken);
    }

    @Override
    public void clearAuths() throws SQLDataAccessException {
        var statement = "TRUNCATE auths";
        authExecuteUpdate(statement);
    }

    @Override
    public String createAuth(String username) throws SQLDataAccessException {
        String authToken;
        try {
            do {
                authToken = generateToken();
            } while (getAuth(authToken) != null);
            String statement = "INSERT INTO auths (authToken, username) VALUES (?, ?)";
            authExecuteUpdate(statement, authToken, username);
        } catch (SQLDataAccessException e) {
            throw new SQLDataAccessException(e.getMessage());
        }
        return authToken;
    }

    private static String generateToken() {
        return UUID.randomUUID().toString();
    }

    private void authExecuteUpdate(String statement, Object... params) throws SQLDataAccessException {
        MySqlDAO.executeUpdate(statement, params);
    }
}
