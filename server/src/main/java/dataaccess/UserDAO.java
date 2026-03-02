package dataaccess;

import model.*;

public interface UserDAO {
    public default UserData getUser(String username) {
        throw new RuntimeException("Not implemented");
    }
    public default void createUser(String username, String password, String email) {
        throw new RuntimeException("Not implemented");
    }
    public default void clearUsers() {
        throw new RuntimeException("Not implemented");
    }
}
