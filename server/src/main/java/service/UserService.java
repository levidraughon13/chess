package service;

import model.*;
import request.*;
import result.*;
import dataaccess.*;

import java.util.UUID;

public class UserService {
    private final UserDAO userAccess;
    private final AuthDAO authDataAccess;

    public UserService(UserDAO UserDataAccess, AuthDAO AuthDataAccess){
        userAccess = UserDataAccess;
        authDataAccess = AuthDataAccess;
    }


    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest.username() == null | registerRequest.password() == null | registerRequest.email() == null){
            throw new BadRequestException("Error: bad request");
        }
        UserData user = userAccess.getUser(registerRequest.username());
        if (user != null){
            throw new DataAccessException("Error: already taken");
        }
        userAccess.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
        user = userAccess.getUser(registerRequest.username());
        String authToken = generateToken();
        authDataAccess.createAuth(generateToken(), registerRequest.username());
        return new RegisterResult(user.username(), authToken);
    }
    public LoginResult login(LoginRequest loginRequest) {
        throw new RuntimeException("Not implemented");
    }
    public void logout(LogoutRequest logoutRequest) {
        throw new RuntimeException("Not implemented");
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
