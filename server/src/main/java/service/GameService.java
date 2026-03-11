package service;

import dataaccess.*;
import model.AuthData;
import model.GameData;
import request.JoinRequest;
import request.NewGameRequest;
import result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class GameService extends Service{
    private final GameDAO gameDataAccess;
    private final AuthDAO authDataAccess;


    public GameService(GameDAO gameDataAccess, AuthDAO authDataAccess){
        this.gameDataAccess = gameDataAccess;
        this.authDataAccess = authDataAccess;
    }

    public NewGameResult createGame(NewGameRequest gameRequest, String authToken) throws
            UnauthorizedException,
            BadRequestException,
            SQLDataAccessException {
        validateAuthToken(authDataAccess, authToken);
        if (gameRequest.gameName() == null){
            throw new BadRequestException("Error: bad request");
        }
        int gameID = gameDataAccess.createGame(gameRequest.gameName());
        return new NewGameResult(gameID);
    }

    public GameList listGames(String authToken) throws UnauthorizedException, SQLDataAccessException {
        validateAuthToken(authDataAccess, authToken);
        HashMap<Integer, GameData> games;
        games = gameDataAccess.listGames();
        List<GameInfo> gameInfo = new ArrayList<>();
        for (GameData data : games.values()){
            gameInfo.add(new GameInfo(data.gameID(), data.whiteUsername(), data.blackUsername(), data.gameName()));
        }
        return new GameList(gameInfo);
    }

    public void joinGame(JoinRequest joinRequest, String authToken) throws DataAccessException, SQLDataAccessException{
        AuthData authData = validateAuthToken(authDataAccess, authToken);
        if (joinRequest.gameID() == null) {
            throw new BadRequestException("Error: invalid gameID");
        }
        GameData game = gameDataAccess.getGame(joinRequest.gameID());
        if (Objects.equals(joinRequest.playerColor(), "WHITE")){
            if (game.whiteUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
        } else {
            if (game.blackUsername() != null) {
                throw new DataAccessException("Error: already taken");
            }
        }
        gameDataAccess.joinGame(joinRequest.gameID(), authData.username(), joinRequest.playerColor());
    }

    public void clear() throws SQLDataAccessException {
        gameDataAccess.clearGames();
    }
}
