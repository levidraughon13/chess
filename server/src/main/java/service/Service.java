package service;

import dataaccess.AuthDAO;
import dataaccess.UnauthorizedException;
import model.AuthData;

public class Service {

    public AuthData validateAuthToken(AuthDAO authDataAccess, String authToken) throws UnauthorizedException {
        AuthData data = authDataAccess.getAuth(authToken);
        boolean b = (data == null);
        if (b) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return data;
    }
}
