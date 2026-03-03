package service;

import dataaccess.AuthDAO;

public class AuthService extends Service {
    private final AuthDAO authDataAccess;

    public AuthService(AuthDAO authDataAccess){
        this.authDataAccess = authDataAccess;
    }

    public void clear(){
        authDataAccess.clearAuths();
    }

}
