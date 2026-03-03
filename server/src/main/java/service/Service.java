package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UnauthorizedException;
import dataaccess.UserDAO;
import model.AuthData;

import java.util.UUID;

public class Service {

    public AuthData validateAuthToken(AuthDAO authDataAccess, String authToken) throws UnauthorizedException {
        AuthData data = authDataAccess.getAuth(authToken);
        boolean b = (data == null);
        if (b) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return data;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
