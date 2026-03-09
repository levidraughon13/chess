package dataaccess;

import model.GameData;

import java.util.HashMap;

public class SQLGameDAO implements GameDAO{
    @Override
    public int createGame(String gameName) {
        return 0;
    }

    @Override
    public HashMap<Integer, GameData> listGames() {
        return null;
    }

    @Override
    public void joinGame(Integer gameID, String username, String team) throws BadRequestException {

    }

    @Override
    public GameData getGame(Integer gameID) throws BadRequestException {
        return null;
    }

    @Override
    public void clearGames() {

    }
}
