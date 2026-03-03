package dataaccess;

import model.AuthData;
import model.UserData;


public interface AuthDAO {
    public default AuthData getAuth(String authToken) {
        throw new RuntimeException("Not implemented");
    }
    public default void createAuth(String authToken, String username) {
        throw new RuntimeException("Not implemented");
    }
    public default void deleteAuth(String authToken){
        throw new RuntimeException("Not implemented");
    }
    public default void clearAuths() {
        throw new RuntimeException("Not implemented");
    }

    String createAuth(String username);
}
