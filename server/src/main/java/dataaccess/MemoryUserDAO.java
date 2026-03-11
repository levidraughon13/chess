package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Objects;

public class MemoryUserDAO implements UserDAO {
    private final HashMap<String, UserData> users = new HashMap<>();

    public UserData getUser(String username) {
        return users.get(username);
    }
    public void createUser(String username, String password, String email) {
        users.put(username, new UserData(username, password, email));
    }
    public void clearUsers(){
        users.clear();
    }

    @Override
    public boolean matchPasswords(String dbPassword, String password) {
        return Objects.equals(dbPassword, password);
    }

}
