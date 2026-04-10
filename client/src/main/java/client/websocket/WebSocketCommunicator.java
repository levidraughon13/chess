package client.websocket;

import chess.ChessMove;
import com.google.gson.Gson;


import exception.DataAccessException;
import jakarta.websocket.*;
import websocket.commands.PlayerLeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.PlayerJoinCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@ClientEndpoint
public class WebSocketCommunicator {

    Session session;
    ServerMessageHandler messageHandler;

    public WebSocketCommunicator(String url, ServerMessageHandler handler) throws DataAccessException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.messageHandler = handler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            //set message handler
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    ServerMessage notification = new Gson().fromJson(message, ServerMessage.class);

                    switch (notification.getServerMessageType()){
                        case ERROR -> {
                            var realMessage = new Gson().fromJson(message, ErrorMessage.class);
                            messageHandler.notify(realMessage);
                        }
                        case LOAD_GAME -> {
                            var realMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            messageHandler.notify(realMessage);
                        }
                        case NOTIFICATION -> {
                            var realMessage = new Gson().fromJson(message, NotificationMessage.class);
                            messageHandler.notify(realMessage);
                        }
                    }

                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }


    public void observerJoin(String authToken, Integer gameID) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void observerLeave(String authToken, Integer gameID) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void playerJoin(String authToken, Integer gameID, String color) throws IOException {
        var command = new PlayerJoinCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID, color);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void playerLeave(String authToken, Integer gameID, String color) throws IOException {
        var command = new PlayerLeaveCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, color);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void makeMove(String authToken, Integer gameID, ChessMove move) throws IOException {
        var command = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

    public void resign(String authToken, Integer gameID) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        this.session.getBasicRemote().sendText(new Gson().toJson(command));
    }

}
