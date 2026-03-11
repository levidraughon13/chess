package dataaccess;

import model.*;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException;
    void createUser(String username, String password, String email) throws DataAccessException;
    void clearUsers() throws DataAccessException;
    boolean matchPasswords(String dbPassword, String password);
}
