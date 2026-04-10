package server.websocket;

import chess.ChessGame;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    public final ConcurrentHashMap<Integer, Set<Session>> connections = new ConcurrentHashMap<>();

    public void add(Integer id, Session session) {
        connections.computeIfAbsent(id, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void remove(Integer id, Session session) {
        connections.get(id).remove(session);
    }

    public void broadcast(Integer id, Session excludeSession, ServerMessage notification) throws IOException {
        String json = notification.toString();
        for (Session c : connections.get(id)) {
            if (c.isOpen()) {
                c.getRemote().sendString(json);
            }
        }
    }


}