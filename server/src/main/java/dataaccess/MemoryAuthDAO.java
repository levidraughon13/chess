package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO{
    private final HashMap<String, AuthData> authData = new HashMap<>();

    public AuthData getAuth(String authToken) {
        return authData.get(authToken);
    }
    public void createAuth(String authToken, String username) {
        authData.put(authToken, new AuthData(authToken, username));
    }
    public void clearAuths() {
        authData.clear();
    }
}
