package dataaccess;

import model.*;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException, SQLDataAccessException;
    void createUser(String username, String password, String email) throws DataAccessException, SQLDataAccessException;
    void clearUsers() throws SQLDataAccessException;
    boolean matchPasswords(String dbPassword, String password);
}
