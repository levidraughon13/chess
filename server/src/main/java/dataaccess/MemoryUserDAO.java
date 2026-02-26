package dataaccess;

import model.AuthData;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    public UserData getUser(String username) {

        throw new RuntimeException("Not implemented");
    }
    public AuthData createUser(String username, String password, String email) {
        throw new RuntimeException("Not implemented");
    }
}
