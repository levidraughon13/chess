package service;

import model.*;
import request.*;
import result.*;
import dataaccess.*;

public class UserService {
    public static RegisterResult register(RegisterRequest registerRequest) {
        UserData user = UserDAO.getUser(registerRequest.username());
        throw new RuntimeException("Not implemented");
    }
    public LoginResult login(LoginRequest loginRequest) {
        throw new RuntimeException("Not implemented");
    }
    public void logout(LogoutRequest logoutRequest) {
        throw new RuntimeException("Not implemented");
    }
}
