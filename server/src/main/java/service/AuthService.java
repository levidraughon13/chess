package service;

import dataaccess.AuthDAO;

public class AuthService extends Service {
    private final AuthDAO authDataAccess;

    public AuthService(AuthDAO authDataAccess){
        this.authDataAccess = authDataAccess;
    }

//    public String createAuthData(String username){
//        String authToken = generateToken();
//        authDataAccess.createAuth(authToken, username);
//        return authToken;
//    }

    public void clear(){
        authDataAccess.clearAuths();
    }

}
