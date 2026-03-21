package dataaccess;

import model.GameData;
import exception.*;

import java.util.HashMap;

public interface GameDAO {

    int createGame(String gameName) throws SQLDataAccessException;

    HashMap<Integer, GameData> listGames() throws SQLDataAccessException;

    void joinGame(Integer gameID, String username, String team) throws BadRequestException, SQLDataAccessException;

    GameData getGame(Integer gameID) throws SQLDataAccessException, BadRequestException;

    void clearGames() throws SQLDataAccessException;
}
