package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.SQLDataAccessException;
import dataaccess.UnauthorizedException;
import model.AuthData;

public class Service {

    public AuthData validateAuthToken(AuthDAO authDataAccess, String authToken) throws UnauthorizedException, SQLDataAccessException {
        AuthData data = authDataAccess.getAuth(authToken);
        boolean b = (data == null);
        if (b) {
            throw new UnauthorizedException("Error: unauthorized");
        }
        return data;
    }
}
