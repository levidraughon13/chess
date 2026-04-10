package websocket.messages;
import chess.ChessGame;
import com.google.gson.Gson;

public class LoadGameMessage extends ServerMessage {
    private final ChessGame game;
    private final String message;

    public LoadGameMessage(ServerMessageType type, ChessGame game, String message) {
        super(type);
        this.game = game;
        this.message = message;
    }

    @Override
    public ChessGame getGame() {
        return game;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
