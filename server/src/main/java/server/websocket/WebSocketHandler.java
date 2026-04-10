package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.BadRequestException;
import exception.SQLDataAccessException;
import io.javalin.websocket.*;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.PlayerJoinCommand;
import websocket.commands.PlayerLeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.List;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public WebSocketHandler(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;

    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        try {
            UserGameCommand command = new Gson().fromJson(ctx.message(), UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> {
                    PlayerJoinCommand playerJoinCommand = new Gson().fromJson(ctx.message(), PlayerJoinCommand.class);
                    connect(playerJoinCommand.getAuthToken(), playerJoinCommand.getGameID(), playerJoinCommand.getColor(), ctx.session);
                }
                case LEAVE -> {
                    PlayerLeaveCommand playerLeaveCommand = new Gson().fromJson(ctx.message(), PlayerLeaveCommand.class);
                    exit(playerLeaveCommand.getAuthToken(), playerLeaveCommand.getGameID(), playerLeaveCommand.getColor(), ctx.session);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(moveCommand.getAuthToken(), moveCommand.getGameID(), moveCommand.getChessMove(), moveCommand.getColor(), ctx.session);
                }
                case RESIGN -> resign(command.getAuthToken(), command.getGameID(), ctx.session);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String authToken, Integer id, String color, Session session) throws IOException {
        try {
            connections.add(id, session);
            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new BadRequestException("Error: invalid authToken\n");
            }
            String visitorName = authData.username();
            if (color == null) {
                GameData data = null;
                data = gameDAO.getGame(id);
                String white = data.whiteUsername();
                String black = data.blackUsername();
                if (visitorName.equals(white)) {
                    color = "white";
                } else if (visitorName.equals(black)) {
                    color = "black";
                } else {
                    color = "observer";
                }
            }
            var message = String.format("%s joined the game as %s\n\n", visitorName, color.toLowerCase());
            var notification = new NotificationMessage(NotificationMessage.ServerMessageType.NOTIFICATION, message);
            connections.broadcast(id, session, notification);
            var loadGame = new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, gameDAO.getGame(id).game(), null);
            session.getRemote().sendString(new Gson().toJson(loadGame));
        }  catch (BadRequestException | SQLDataAccessException e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: database error\n\n");
            String json = new Gson().toJson(error);
            session.getRemote().sendString(json);
        }
    }





    private void exit(String authToken, Integer id, String color, Session session) throws IOException {
        String visitorName = null;
        try {
            visitorName = authDAO.getAuth(authToken).username();

            if (color == null) {
                GameData data = null;
                data = gameDAO.getGame(id);
                String white = data.whiteUsername();
                String black = data.blackUsername();
                if (visitorName.equals(white)) {
                    color = "white";
                } else if (visitorName.equals(black)) {
                    color = "black";
                } else {
                    color = "observer";
                }
            }


            var message = String.format("%s (%s) left the game\n\n", visitorName, color.toLowerCase());
            var notification = new NotificationMessage(NotificationMessage.ServerMessageType.NOTIFICATION, message);


            GameData gameData = gameDAO.getGame(id);
            if (color.equalsIgnoreCase("white")){
                gameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), gameData.game());
            } else if (color.equalsIgnoreCase("black")) {
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), gameData.game());
            }

            var statement = "UPDATE games SET game=? WHERE gameID=?";
            gameDAO.updateGameData(id, gameData);
            connections.broadcast(id, session, notification);
            connections.remove(id, session);
        } catch (SQLDataAccessException|BadRequestException e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: database error\n\n");
            String json = new Gson().toJson(error);
            session.getRemote().sendString(json);
        }

    }

    private void makeMove(String authToken, Integer gameID, ChessMove move, String color, Session session) throws IOException {

        try {
            GameData gameData = gameDAO.getGame(gameID);
            ChessGame game = gameData.game();
            ChessGame.TeamColor turn = game.getTeamTurn();

            AuthData authData = authDAO.getAuth(authToken);
            if (authData == null) {
                throw new BadRequestException("Error: invalid authToken\n");
            }

            String visitorName = authData.username();

            if (color == null) {
                GameData data = null;
                data = gameDAO.getGame(gameID);
                String white = data.whiteUsername();
                String black = data.blackUsername();
                if (visitorName.equals(white)) {
                    color = "white";
                } else if (visitorName.equals(black)) {
                    color = "black";
                } else {
                    color = "observer";
                }
            }
            if (color.equalsIgnoreCase("observer")){
                throw new BadRequestException("Error: only players in the game can make moves.\n");
            }

            if (color.equalsIgnoreCase("white") && !(game.getTeamTurn() == ChessGame.TeamColor.WHITE)){
                throw new InvalidMoveException("Error: invalid move, it is not your turn.\n");
            } else if (color.equalsIgnoreCase("black") && !(game.getTeamTurn() == ChessGame.TeamColor.BLACK)){
                throw new InvalidMoveException("Error: invalid move, it is not your turn.\n");
            }

            String message = String.format("%s moved %s\n", visitorName, getMoveDescription(move, game));
            game.makeMove(move);
            gameDAO.updateGame(gameID, game);
            connections.broadcast(gameID, new LoadGameMessage(ServerMessage.ServerMessageType.LOAD_GAME, game, null));
            connections.broadcast(gameID, session, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));



            String opponentName = null;
            if (turn == ChessGame.TeamColor.WHITE) {
                opponentName = gameData.whiteUsername();
            } else if (turn == ChessGame.TeamColor.BLACK) {
                opponentName = gameData.blackUsername();
            }
            if (opponentName == null){
                opponentName = "<other team>";
            }



            turn = game.getTeamTurn();
            String newMessage = null;
            if (game.isInCheckmate(turn)) {
                newMessage = String.format("\n%s is now in checkmate! %s wins, game is over.\n", opponentName, visitorName);
                connections.broadcast(gameID, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, newMessage));
            } else if (game.isInCheck(turn)){
                newMessage = String.format("\n%s is now in check!\n", opponentName);
                connections.broadcast(gameID, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, newMessage));
            }


        } catch (InvalidMoveException e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: invalid move\n");
            String json = new Gson().toJson(error);
            session.getRemote().sendString(json);
        } catch (BadRequestException | SQLDataAccessException e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: database error\n");
            String json = new Gson().toJson(error);
            session.getRemote().sendString(json);
        }
    }

    private String getMoveDescription(ChessMove move, ChessGame game) throws SQLDataAccessException, BadRequestException {
        List<String> letters = List.of("A", "B", "C", "D", "E", "F", "G", "H");
        int startRow = move.getStartPosition().getRow();
        int startCol = move.getStartPosition().getColumn();
        int endRow = move.getEndPosition().getRow();
        int endCol = move.getEndPosition().getColumn();
        ChessPiece.PieceType type = game.getBoard().getPiece(move.getStartPosition()).getPieceType();

        StringBuilder moveString = new StringBuilder(String.format("%s %s%d to %s%d", type, letters.get(startCol - 1), startRow, letters.get(endCol - 1), endRow));

        if (move.getPromotionPiece() != null){
            String promote = String.format(", promoted %s to %s", type, move.getPromotionPiece());
            moveString.append(promote);
        }

        return moveString.append("\n\n").toString();
    }

    private void resign(String authToken, Integer gameID, Session session) throws IOException {

        try {
            String visitorName = null;
            visitorName = authDAO.getAuth(authToken).username();
            var message = String.format("%s has resigned, game is over.\n\n", visitorName);
            connections.broadcast(gameID, new NotificationMessage(ServerMessage.ServerMessageType.NOTIFICATION, message));
        } catch (SQLDataAccessException e) {
            ErrorMessage error = new ErrorMessage(ServerMessage.ServerMessageType.ERROR, "Error: database error\n");
            String json = new Gson().toJson(error);
            session.getRemote().sendString(json);
        }

    }
}
