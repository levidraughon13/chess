package dataaccess;

import chess.ChessGame;
import model.GameData;
import exception.*;

import java.util.HashMap;
import java.util.Objects;

public class MemoryGameDAO implements GameDAO{
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private int idCount;

    public MemoryGameDAO(){
        idCount = 0;
    }

    public int createGame(String gameName) {
        int newID = idCount + 1;
        idCount++;
        games.put(newID, new GameData(newID, null, null, gameName, new ChessGame()));
        return newID;
    }

    public HashMap<Integer, GameData> listGames() {
        return games;
    }

    public void joinGame(Integer gameID, String username, String team) throws BadRequestException {
        GameData game = getGame(gameID);
        if (Objects.equals(team, "WHITE")){
            games.replace(gameID, new GameData(gameID, username, game.blackUsername(), game.gameName(), game.game()));
        } else if (Objects.equals(team, "BLACK")){
            games.replace(gameID, new GameData(gameID, game.whiteUsername(), username, game.gameName(), game.game()));
        } else {
            throw new BadRequestException("Error: bad request, invalid team color");
        }
    }

    public GameData getGame(Integer gameID) throws BadRequestException {
        if (games.get(gameID) == null){
            throw new BadRequestException("Error: bad request");
        }
        return games.get(gameID);
    }

    public void clearGames(){
        games.clear();
    }

    @Override
    public void updateGame(Integer gameID, ChessGame game) throws BadRequestException {
        GameData gameData = getGame(gameID);
        games.replace(gameID, new GameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game));
    }
}
