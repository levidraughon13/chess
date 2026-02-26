package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

public class Database {
    public UserData[] users = new UserData[50];
    public GameData[] games = new GameData[50];
    public AuthData[] auths = new AuthData[50];
}
