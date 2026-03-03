package dataaccess;

import model.AuthData;


public interface AuthDAO {
    AuthData getAuth(String authToken);
    void deleteAuth(String authToken) throws UnauthorizedException;
    void clearAuths();
    String createAuth(String username);
}
