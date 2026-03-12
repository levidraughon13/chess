package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLUserDAO implements UserDAO {

    public SQLUserDAO(){
        try {
            String[] createStatements = {
                    """
            CREATE TABLE IF NOT EXISTS users (
              `username` varchar(255) NOT NULL,
              `password` varchar(255) NOT NULL,
              `email` varchar(255) NOT NULL,
              PRIMARY KEY (`username`)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
            };
            MySqlDAO.configureDatabase(createStatements);
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) throws SQLDataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(rs.getString(1),rs.getString(2),rs.getString(3));
                    }
                }
            }
        } catch (Exception e) {
            throw new SQLDataAccessException(String.format("Error, unable to read data: %s", e.getMessage()));
        }
        return null;
    }

    @Override
    public void createUser(String username, String password, String email) throws SQLDataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        userExecuteUpdate(statement, username, hashedPassword, email);
    }

    @Override
    public void clearUsers() throws SQLDataAccessException {
        var statement = "TRUNCATE users";
        userExecuteUpdate(statement);
    }

    @Override
    public boolean matchPasswords(String dbPassword, String password) {
        return BCrypt.checkpw(password, dbPassword);
    }

    private void userExecuteUpdate(String statement, Object... params) throws SQLDataAccessException {
        MySqlDAO.executeUpdate(statement, params);
    }
}
