package client.websocket;

import com.google.gson.Gson;


import exception.DataAccessException;
import jakarta.websocket.*;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

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

    public void observerJoin(String authToken, Integer gameID){}

    public void observerLeave(String authToken, Integer gameID){}

    public void playerJoin(String authToken, Integer gameID){}

    public void playerLeave(String authToken, Integer gameID){}

    public void makeMove(String authToken, Integer gameID){}

    public void resign(String authToken, Integer gameID){}

    public void check(String authToken){}

    public void checkmate(String authToken){}
}
