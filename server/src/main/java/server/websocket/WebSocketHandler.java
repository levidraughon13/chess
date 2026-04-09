package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import exception.BadRequestException;
import exception.SQLDataAccessException;
import io.javalin.websocket.*;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

import java.io.IOException;


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
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx.session);
                case LEAVE -> exit(command.getAuthToken(), command.getGameID(), ctx.session);
                case MAKE_MOVE -> {
                    MakeMoveCommand moveCommand = new Gson().fromJson(ctx.message(), MakeMoveCommand.class);
                    makeMove(moveCommand.getGameID(), moveCommand.getChessMove(), ctx.session);
                }
                case RESIGN -> resign(command.getAuthToken(), ctx.session);
            }
        } catch (IOException | SQLDataAccessException ex) {
            ex.printStackTrace();
        } catch (BadRequestException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String authToken, Integer id, Session session) throws IOException, SQLDataAccessException {
        connections.add(id, session);
        String visitorName = authDAO.getAuth(authToken).username();
        var message = String.format("%s joined the game", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(id, session, notification);
    }


    private void exit(String authToken, Integer id, Session session) throws IOException, SQLDataAccessException {
        String visitorName = authDAO.getAuth(authToken).username();
        var message = String.format("%s left the game", visitorName);
        var notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
        connections.broadcast(id, session, notification);
        connections.remove(id, session);
    }

    private void makeMove(Integer gameID, ChessMove move, Session session) throws SQLDataAccessException, BadRequestException {
        GameData gameData = gameDAO.getGame(gameID);
        ChessGame game = gameData.game();
        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
        gameDAO.updateGame(gameID, game);
    }

    private void resign(String authToken, Session session) throws SQLDataAccessException {
        String visitorName = authDAO.getAuth(authToken).username();
        var message = String.format("%s left the game", visitorName);
    }
}
