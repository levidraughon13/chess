package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> authData = new HashMap<>();

    public AuthData getAuth(String authToken) {
         return authData.get(authToken);
    }
    public String createAuth(String username) {
        String authToken = generateToken();
        while (getAuth(authToken) != null) { authToken = generateToken(); }
        authData.put(authToken, new AuthData(authToken, username));
        return authToken;
    }
    public void deleteAuth(String authToken) throws UnauthorizedException {
        if (authData.get(authToken) == null) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        authData.remove(authToken);
    }
    public void clearAuths() {
        authData.clear();
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
