package service;

import dataaccess.AuthDAO;
import dataaccess.SQLDataAccessException;

public class AuthService extends Service {
    private final AuthDAO authDataAccess;

    public AuthService(AuthDAO authDataAccess){
        this.authDataAccess = authDataAccess;
    }

    public void clear(){
        try {
            authDataAccess.clearAuths();
        } catch (SQLDataAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
