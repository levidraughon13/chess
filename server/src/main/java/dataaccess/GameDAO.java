package dataaccess;

import model.GameData;

import java.util.HashMap;

public interface GameDAO {

    int createGame(String gameName) throws DataAccessException;

    HashMap<Integer, GameData> listGames();

    void joinGame(Integer gameID, String username, String team) throws BadRequestException;

    GameData getGame(Integer gameID) throws DataAccessException;

    void clearGames();
}
