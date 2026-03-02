package service;

import dataaccess.GameDAO;

public class GameService {
    private final GameDAO gameDataAccess;

    public GameService(GameDAO gameDataAccess) {
        this.gameDataAccess = gameDataAccess;
    }

    public void clear(){
        gameDataAccess.clearGames();
    }
}
