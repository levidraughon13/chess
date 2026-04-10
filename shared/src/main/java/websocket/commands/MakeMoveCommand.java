package websocket.commands;


import chess.ChessMove;

public class MakeMoveCommand extends UserGameCommand {

    private final ChessMove move;
    private final String color;

    public MakeMoveCommand(CommandType commandType, String authToken, Integer gameID, ChessMove move, String color) {
        super(commandType, authToken, gameID);
        this.move = move;
        this.color = color;
    }

    public ChessMove getChessMove() { return move; }

    public String getColor() {
        return color;
    }
}
