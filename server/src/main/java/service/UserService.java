package service;

import model.*;
import request.*;
import result.*;
import dataaccess.*;

public class UserService {
    private final UserDAO users;
    public UserService(){
        this.users = new UserDAO();
    }

    public RegisterResult register(RegisterRequest registerRequest) {
        UserData user = users.getUser(registerRequest.username());
        AuthData authData = users.createUser(registerRequest.username(), registerRequest.password(), registerRequest.email());
        throw new RuntimeException("Not implemented");
    }
    public LoginResult login(LoginRequest loginRequest) {
        throw new RuntimeException("Not implemented");
    }
    public void logout(LogoutRequest logoutRequest) {
        throw new RuntimeException("Not implemented");
    }
}
