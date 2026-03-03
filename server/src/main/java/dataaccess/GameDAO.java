package dataaccess;

import model.GameData;

import java.util.HashMap;

public interface GameDAO {

    public default int createGame(String gameName) {
        throw new RuntimeException("Not implemented");
    }

    public default HashMap<Integer, GameData> listGames() {
        throw new RuntimeException("Not implemented");
    }

    public default void joinGame(Integer gameID, String username, String team) throws BadRequestException {
        throw new RuntimeException("Not implemented");
    }

    public default GameData getGame(Integer gameID){
        throw new RuntimeException("Not implemented");
    }

    public default void clearGames() {
        throw new RuntimeException("Not implemented");
    }
}
