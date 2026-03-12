package dataaccess;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class MySqlDAO {
    static public void configureDatabase(String[] createStatements) throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Error: Unable to configure database: %s", ex.getMessage()));
        }
    }

    static public void executeUpdate(String statement, Object... params) throws SQLDataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (Objects.requireNonNull(param) instanceof String p) {
                        ps.setString(i + 1, p);
                    } else {
                        throw new IllegalStateException("Unexpected value: " + param);
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
