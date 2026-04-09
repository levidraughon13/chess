package client.websocket;

import com.google.gson.Gson;


import exception.DataAccessException;
import jakarta.websocket.*;
import websocket.messages.Error;
import websocket.messages.LoadGameMessage;
import websocket.messages.Notification;
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
                            var realMessage = new Gson().fromJson(message, Error.class);
                            messageHandler.notify(realMessage);
                        }
                        case LOAD_GAME -> {
                            var realMessage = new Gson().fromJson(message, LoadGameMessage.class);
                            messageHandler.notify(realMessage);
                        }
                        case NOTIFICATION -> {
                            var realMessage = new Gson().fromJson(message, Notification.class);
                            messageHandler.notify(realMessage);
                        }
                    }

                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void observerJoin(){}

    public void observerLeave(){}

    public void playerJoin(){}

    public void playerLeave(){}

    public void makeMove(){}

    public void resign(){}

    public void check(){}

    public void checkmate(){}
}
