package websocket.messages;
import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;

    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }
}
