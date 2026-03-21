package dataaccess;

import model.*;
import exception.*;

public interface UserDAO {
    UserData getUser(String username) throws SQLDataAccessException;
    void createUser(String username, String password, String email) throws SQLDataAccessException;
    void clearUsers() throws SQLDataAccessException;
    boolean matchPasswords(String dbPassword, String password);
}
