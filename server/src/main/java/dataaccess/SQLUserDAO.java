package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO{
    @Override
    public UserData getUser(String username) {
        return null;
    }

    @Override
    public void createUser(String username, String password, String email) {

    }

    @Override
    public void clearUsers() {

    }
}
