package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;

public class AuthService extends Service {
    private final AuthDAO authDataAccess;

    public AuthService(AuthDAO authDataAccess){
        this.authDataAccess = authDataAccess;
    }

    public void clear(){
        try {
            authDataAccess.clearAuths();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
