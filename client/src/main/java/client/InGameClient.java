package client;

import chess.*;
import client.websocket.ServerMessageHandler;
import client.websocket.WebSocketCommunicator;
import exception.BadRequestException;
import exception.DataAccessException;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.*;

public class InGameClient implements ServerMessageHandler {
    private final String team;
    private final ChessGame.TeamColor teamColor;
    private final WebSocketCommunicator ws;
    private final String authToken;
    private final Integer gameID;
    private boolean gameOver = false;
    private ChessGame game = new ChessGame();


    public InGameClient(ServerFacade server, String color, String authToken, Integer gameID) throws DataAccessException {
        this.team = color; //remember that if the color == "observer", different commands should be shown below
        this.ws = new WebSocketCommunicator(server.serverUrl, this);
        this.authToken = authToken;
        this.gameID = gameID;
        if (Objects.equals(team, "BLACK")){
            this.teamColor = ChessGame.TeamColor.BLACK;
        } else {
            this.teamColor = ChessGame.TeamColor.WHITE;
        }
    }

    public void run() throws IOException {
        ws.playerJoin(authToken, gameID, team);
        System.out.println(" ");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave")) {

            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print("\n" + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }

        ws.playerLeave(authToken, gameID, team);
    }


    private String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            if (gameOver && !cmd.equalsIgnoreCase("leave")){
                return "\nGame is over, you can only enter 'leave' to exit the game\n";
            }
            if (!team.equalsIgnoreCase("observer")) {
                if (cmd.equalsIgnoreCase("move")) {
                    return move(params);
                } else if (cmd.equalsIgnoreCase("resign")) {
                    resign();
                    return "";
                }
            }
            return switch (cmd) {
                case "leave" -> "leave";
                case "redraw" -> redraw(game, List.of());
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String highlight(String[] params) throws BadRequestException {
        ChessPosition selected = getPosition(params);

        Collection<ChessMove> moves = game.validMoves(selected);
        if (moves.isEmpty()){
            return "No valid moves\n";
        }
        List<ChessPosition> positions = new ArrayList<>(List.of());
        positions.add(moves.iterator().next().getStartPosition());
        for (ChessMove move : moves) {
            positions.add(move.getEndPosition());
        }
        return redraw(game, positions);
    }

    private String move(String[] params) throws BadRequestException, IOException {
        String color = team;
        if (gameOver){
            color = "over";
        }
        ChessMove move = getMove(params);
        ws.makeMove(authToken, gameID, move, color);
        return "";
    }

    private void resign() throws IOException {
        gameOver = true;
        ws.resign(authToken, gameID, team);
    }

    private String help() {
        if (gameOver) {
            return """
                    Game is over. Possible Commands:
                      leave - leave the game
                      help - see possible commands
                    """;
        }
        if (!team.equalsIgnoreCase("observer")){
            return """
                Possible Commands (Some are exclusive to players):
                  redraw - reload the current chess board
                  move <space 1> <space 2> - move a piece on space 1 to space 2 (eg. move e7 e6)
                                           - you may also include a promotion piece if promoting
                                             a pawn, but doing so incorrectly will cause the game
                                             to prompt you for a different move
                  resign - forfeit the game
                  highlight <space> - highlight legal moves for a given piece (eg. highlight a6)
                  leave - leave the game
                  help - see possible commands
                """;
        }
        return """
                Possible Commands:
                  redraw - reload the current chess board
                  highlight <space> - highlight legal moves for a given piece (eg. highlight a6)
                  leave - leave the game
                  help - see possible commands
                """;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()){
            case ERROR, NOTIFICATION -> {
                System.out.print(message.getMessage());
                if (message.getMessage().contains("game is over")){
                    gameOver = true;
                }
            }
            case LOAD_GAME -> {
                game = message.getGame();
                System.out.print(redraw(game, List.of()));
            }
        }
    }

    private String redraw(ChessGame game, List<ChessPosition> positions){
        StringBuilder boardString = new StringBuilder(EscapeSequences.SET_TEXT_BOLD);
        ChessBoard board = game.getBoard();

        String team2;
        if (Objects.equals(team, "BLACK")){

            team2 = EscapeSequences.SET_TEXT_COLOR_GREEN;
        } else {

            team2 = EscapeSequences.SET_TEXT_COLOR_BLUE;
        }

        List<String> letters = List.of(" A ", " B ", " C ", " D " , " E ", " F ", " G ", " H ");
        List<String> numbers = List.of(" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ");
        List<Integer> rows = List.of(8, 7, 6, 5, 4, 3, 2, 1);
        List<Integer> cols = List.of(1, 2, 3, 4, 5, 6, 7, 8);


        if (Objects.equals(team, "BLACK")) {
            letters = letters.reversed();
            numbers = numbers.reversed();
            rows = rows.reversed();
            cols = cols.reversed();
        }

        letterRow(boardString, letters);



        // ABOVE DOES THE TOP LETTER ROW


        for (int i = 0; i < 8; i++){
            boardString.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(numbers.get(7-i)).append(team2);
            for (int j = 0; j < 8; j++){
                ChessPosition currentSquare = new ChessPosition(rows.get(i), cols.get(j));
                if (!positions.isEmpty()){
                    setSquareColor(((j+i) %2 ), boardString, positions.getFirst(), currentSquare, positions);
                } else {
                    setSquareColor(((j+i) %2 ), boardString);
                }

                ChessPiece piece = board.getPiece(currentSquare);

                if (piece == null) {
                    boardString.append("   ");
                } else{
                    switch(piece.getTeamColor()){
                        case WHITE -> boardString.append(EscapeSequences.SET_TEXT_COLOR_GREEN);
                        case BLACK -> boardString.append(EscapeSequences.SET_TEXT_COLOR_BLUE);
                    }

                    switch(piece.getPieceType()){
                        case ROOK -> boardString.append(" R ");
                        case BISHOP -> boardString.append(" B ");
                        case QUEEN -> boardString.append(" Q ");
                        case KNIGHT -> boardString.append(" N ");
                        case KING -> boardString.append(" K ");
                        case PAWN -> boardString.append(" P ");
                    }
                }
            }
            boardString.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.RESET_TEXT_COLOR)
                    .append(numbers.get(7-i)).append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_BG_COLOR).append("\n");
        }


        // BELOW DOES BOTTOM LETTER ROW

        letterRow(boardString, letters);

        boardString.append(EscapeSequences.RESET_BG_COLOR + "\n" + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        return boardString.toString();
    }

    private void letterRow(StringBuilder board, List<String> letters){
        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "   ");
        for (String letter : letters) {
            board.append(letter);
        }
        board.append("   " + EscapeSequences.RESET_BG_COLOR + "\n");
    }

    private void setSquareColor(int i, StringBuilder board){
        if ((i % 2) == 0){
            board.append(EscapeSequences.SET_BG_COLOR_WHITE);
        }
        else {
            board.append(EscapeSequences.SET_BG_COLOR_BLACK);
        }
    }

    private void setSquareColor(int i, StringBuilder board, ChessPosition piecePosition, ChessPosition currentSquare, List<ChessPosition> positions){
        setSquareColor(i, board);

        if (positions.contains(currentSquare)) {
            board.append(EscapeSequences.SET_BG_COLOR_YELLOW);
        }
        if (currentSquare.equals(piecePosition)){
            board.append(EscapeSequences.SET_BG_COLOR_RED);
        }
    }

    private ChessMove getMove(String[] params) throws BadRequestException {
        if (!(params.length == 2) && !(params.length == 3)) {
            throw new BadRequestException("Error: format input as example 'move e4 e7 *<promotion piece>*'\n");
        }
        String p1 = params[0].toLowerCase();
        String p2 = params[1].toLowerCase();

        char p1c = p1.charAt(0);
        char p1r = p1.charAt(1);
        char p2c = p2.charAt(0);
        char p2r = p2.charAt(1);

        int col1 = p1c - 'a' + 1;
        int row1 = p1r - '0';

        int col2 = p2c - 'a' + 1;
        int row2 = p2r - '0';

        if (params.length == 3){
            ChessPiece promote = getPromote(params);

            return new ChessMove(new ChessPosition(row1, col1), new ChessPosition(row2, col2), promote.getPieceType());
        }
        return new ChessMove(new ChessPosition(row1, col1), new ChessPosition(row2, col2), null);
    }

    private ChessPiece getPromote(String[] params) throws BadRequestException {
        ChessPiece promote = null;
        if (params.length == 3) {
            switch (params[2].toLowerCase()) {
                case ("pawn") -> promote = new ChessPiece(teamColor, ChessPiece.PieceType.PAWN);
                case ("rook") -> promote = new ChessPiece(teamColor, ChessPiece.PieceType.ROOK);
                case ("knight") -> promote = new ChessPiece(teamColor, ChessPiece.PieceType.KNIGHT);
                case ("bishop") -> promote = new ChessPiece(teamColor, ChessPiece.PieceType.BISHOP);
                case ("queen") -> promote = new ChessPiece(teamColor, ChessPiece.PieceType.QUEEN);
                case ("king") -> promote = new ChessPiece(teamColor, ChessPiece.PieceType.KING);
                default -> throw new BadRequestException("\nError: invalid promotion piece given\n");
            }
        }
        return promote;
    }

    private ChessPosition getPosition(String[] params) throws BadRequestException {
        if (params.length != 1) {
            throw new BadRequestException("Error: format input as example 'highlight e7'\n");
        }
        String p1 = params[0].toLowerCase();


        char p1c = p1.charAt(0);
        char p1r = p1.charAt(1);

        int col1 = p1c - 'a' + 1;
        int row1 = p1r - '0';

        return new ChessPosition(row1, col1);
    }
}
