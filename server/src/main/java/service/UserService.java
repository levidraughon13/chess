package service;

import model.*;
import request.*;
import result.*;
import dataaccess.*;

import java.util.Objects;
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
        authDataAccess.createAuth(authToken, user.username());
        return new RegisterResult(user.username(), authToken);
    }
    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException {
        if (loginRequest.username() == null | loginRequest.password() == null){
            throw new BadRequestException("Error: bad request");
        }
        UserData user = userAccess.getUser(loginRequest.username());
        if (user == null){
            throw new UnauthorizedException("Error: unauthorized, user does not exist");
        } else if (!Objects.equals(user.password(), loginRequest.password())){
            throw new UnauthorizedException("Error: unauthorized, incorrect password");
        }
        String authToken = generateToken();
        authDataAccess.createAuth(authToken, user.username());
        return new LoginResult(user.username(), authToken);
    }

    public void logout(LogoutRequest logoutRequest) throws UnauthorizedException {
        String authToken = logoutRequest.authToken();
        boolean b = !(validateAuthToken(authToken));
        if (b) {
            throw new UnauthorizedException("Error: unauthorized, not logged in");
        }
        authDataAccess.deleteAuth(authToken);
    }

    public void clear() {
        userAccess.clearUsers();
    }

    public Boolean validateAuthToken(String authToken){
        AuthData data = authDataAccess.getAuth(authToken);
        return data != null;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
