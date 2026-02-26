package dataaccess;

import model.*;

public interface UserDAO {
    public default UserData getUser(String username) {

        throw new RuntimeException("Not implemented");
    }
    public default AuthData createUser(String username, String password, String email) {
        throw new RuntimeException("Not implemented");
    }

}
