package dataaccess;

import model.AuthData;


public interface AuthDAO {
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken);
    void clearAuths();
    String createAuth(String username);
}
