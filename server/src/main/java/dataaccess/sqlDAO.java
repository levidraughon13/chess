package dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

public class sqlDAO {
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
}
