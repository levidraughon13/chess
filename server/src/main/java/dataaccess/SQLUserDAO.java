package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

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
            sqlDAO.configureDatabase(createStatements);
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
        executeUpdate(statement, username, hashedPassword, email);
    }

    @Override
    public void clearUsers() throws SQLDataAccessException {
        var statement = "TRUNCATE users";
        executeUpdate(statement);
    }

    @Override
    public boolean matchPasswords(String dbPassword, String password) {
        return BCrypt.checkpw(password, dbPassword);
    }

    private void executeUpdate(String statement, Object... params) throws SQLDataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    switch (param) {
                        case String p -> ps.setString(i + 1, p);
                        case Integer p -> ps.setInt(i + 1, p);
                        case null -> ps.setNull(i + 1, NULL);
                        default -> throw new IllegalStateException("Unexpected value: " + param);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    rs.getInt(1);
                }

            }
        } catch (SQLException | DataAccessException e) {
            throw new SQLDataAccessException(String.format("Error: unable to update database: %s, %s", statement, e.getMessage()));
        }
    }
}
