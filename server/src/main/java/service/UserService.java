package service;

import model.*;
import request.*;
import result.*;
import dataaccess.*;
import exception.*;

public class UserService extends Service{
    private final UserDAO userDataAccess;
    private final AuthDAO authDataAccess;


    public UserService(UserDAO userDataAccess, AuthDAO authDataAccess){
        this.userDataAccess = userDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws SQLDataAccessException, DataAccessException {
        if (registerRequest.username() == null | registerRequest.password() == null | registerRequest.email() == null){
            throw new BadRequestException("Error: bad request");
        }
        UserData user = userDataAccess.getUser(registerRequest.username());
        if (user != null){
            throw new DataAccessException("Error: already taken");
        }
        userDataAccess.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
        user = userDataAccess.getUser(registerRequest.username());
        String authToken = authDataAccess.createAuth(user.username());
        return new RegisterResult(user.username(), authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws SQLDataAccessException, BadRequestException, UnauthorizedException {
        if (loginRequest.username() == null | loginRequest.password() == null){
            throw new BadRequestException("Error: bad request");
        }
        UserData user = userDataAccess.getUser(loginRequest.username());
        if (user == null){
            throw new UnauthorizedException("Error: unauthorized, user does not exist");
        } else if (!userDataAccess.matchPasswords(user.password(), loginRequest.password())){
            throw new UnauthorizedException("Error: unauthorized, incorrect password");
        }
        String authToken = authDataAccess.createAuth(user.username());
        return new LoginResult(user.username(), authToken);
    }

    public void logout(LogoutRequest logoutRequest) throws UnauthorizedException, SQLDataAccessException {
        String authToken = logoutRequest.authToken();
        validateAuthToken(authDataAccess, authToken);
        authDataAccess.deleteAuth(authToken);
    }

    public void clear() throws SQLDataAccessException {
        userDataAccess.clearUsers();
    }

}
