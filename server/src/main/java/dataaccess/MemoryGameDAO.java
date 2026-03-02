package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.HashMap;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int idCount;

    public MemoryGameDAO(){
        idCount = 0;
    }

    public HashMap<Integer, GameData> listGames(Integer id) {
        return games;
    }
    public void createUser(String whiteUser, String blackUser, String gameName, ChessGame game) {
        int newID = idCount + 1;
        idCount++;
        games.put(newID, new GameData(newID, whiteUser, blackUser, gameName, game));
    }
    public void clearUsers(){
        games.clear();
    }
}
