package client;

import chess.*;
import client.websocket.ServerMessageHandler;
import client.websocket.WebSocketCommunicator;
import exception.BadRequestException;
import exception.DataAccessException;
import ui.EscapeSequences;
import websocket.messages.ServerMessage;

import java.util.*;

public class InGameClient implements ServerMessageHandler {
    private final ServerFacade server;
    private final String team;
    private final ChessGame.TeamColor teamColor;
    private final WebSocketCommunicator ws;
    private final String authToken;
    private final String username;
    private boolean gameOver = false;
    private ChessGame game = new ChessGame();


    public InGameClient(ServerFacade server, String color, String authToken, String username) throws DataAccessException {
        this.team = color; //remember that if the color == "observer", different commands should be shown below
        this.server = server;
        this.ws = new WebSocketCommunicator(server.serverUrl, this);
        this.authToken = authToken;
        this.username = username;
        if (Objects.equals(team, "BLACK")){
            this.teamColor = ChessGame.TeamColor.BLACK;
        } else {
            this.teamColor = ChessGame.TeamColor.WHITE;
        }
    }

    public void run() {
        System.out.println(" ");
        System.out.print(printInitialBoard());
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
            System.out.print(help());
        }
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
                    return "move()";
                } else if (cmd.equalsIgnoreCase("resign")) {
                    return "resign()";
                }
            }
            return switch (cmd) {
                case "leave" -> "leave";
                case "redraw" -> redraw(game);
                case "highlight" -> highlight(params);
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String printInitialBoard() {
        StringBuilder board = new StringBuilder(EscapeSequences.SET_TEXT_BOLD);
        String team1;
        String team2;
        if (Objects.equals(team, "BLACK")){
            team1 = EscapeSequences.SET_TEXT_COLOR_BLUE;
            team2 = EscapeSequences.SET_TEXT_COLOR_GREEN;
        } else {
            team1 = EscapeSequences.SET_TEXT_COLOR_GREEN;
            team2 = EscapeSequences.SET_TEXT_COLOR_BLUE;
        }

        List<String> letters = List.of(" A ", " B ", " C ", " D " , " E ", " F ", " G ", " H ");
        List<String> backPieces = List.of(" R ", " N ", " B ", " Q ", " K ", " B ", " N ", " R ");
        List<String> numbers = List.of(" 1 ", " 2 ", " 3 ", " 4 ", " 5 ", " 6 ", " 7 ", " 8 ");


        if (Objects.equals(team, "BLACK")) {
            letters = letters.reversed();
            backPieces = backPieces.reversed();
            numbers = numbers.reversed();
        }

        letterRow(board, letters);

        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(numbers.getLast()).append(team2);

        backRow(board, backPieces, 0);

        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.RESET_TEXT_COLOR)
                .append(numbers.getLast()).append(EscapeSequences.RESET_BG_COLOR).append("\n");

        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(numbers.get(numbers.size() - 2)).append(team2);
        for (int i = 0; i < 8; i++){
            setSquareColor(i+1, board);
            board.append(" P ");
        }

        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.RESET_TEXT_COLOR)
                .append(numbers.get(numbers.size() - 2)).append(EscapeSequences.RESET_BG_COLOR).append("\n");

        middleRows(board, numbers);

        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(numbers.get(1)).append(team1);
        for (int i = 0; i < 8; i++){
            setSquareColor(i, board);
            board.append(" P ");
        }

        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.RESET_TEXT_COLOR)
                .append(numbers.get(1)).append(EscapeSequences.RESET_BG_COLOR).append("\n");

        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(numbers.getFirst()).append(team1);

        backRow(board, backPieces, 1);

        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.RESET_TEXT_COLOR)
                .append(numbers.getFirst()).append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_BG_COLOR).append("\n");

        letterRow(board, letters);

        board.append(EscapeSequences.RESET_BG_COLOR + "\n" + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        return board.toString();
    }

    private void letterRow(StringBuilder board, List<String> letters){
        board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY + "   ");
        for (String letter : letters) {
            board.append(letter);
        }
        board.append("   " + EscapeSequences.RESET_BG_COLOR + "\n");
    }

    private void backRow(StringBuilder board, List<String> backPieces, int colorSign){
        for (String backPiece : backPieces) {
            setSquareColor(colorSign, board);
            board.append(backPiece);
            colorSign++;
        }
    }

    private void middleRows(StringBuilder board, List<String> numbers) {
        for (int i = 5; i > 1; i--){
            board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(numbers.get(i));
            for (int j = 0; j < 8; j++){
                if ((i%2) == 0){
                    setSquareColor(j+1, board);
                } else {
                    setSquareColor(j, board);
                }
                board.append("   ");
            }
            board.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY)
                    .append(numbers.get(i))
                    .append(EscapeSequences.RESET_BG_COLOR).append("\n");
        }
    }

    private void setSquareColor(int i, StringBuilder board){
        if ((i % 2) == 0){
            board.append(EscapeSequences.SET_BG_COLOR_WHITE);
        }
        else {
            board.append(EscapeSequences.SET_BG_COLOR_BLACK);
        }
    }

    private String redraw(ChessGame game){
        StringBuilder boardString = new StringBuilder(EscapeSequences.SET_TEXT_BOLD);
        ChessBoard board = game.getBoard();
        String team1;
        String team2;
        if (Objects.equals(team, "BLACK")){
            team1 = EscapeSequences.SET_TEXT_COLOR_BLUE;
            team2 = EscapeSequences.SET_TEXT_COLOR_GREEN;
        } else {
            team1 = EscapeSequences.SET_TEXT_COLOR_GREEN;
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
                setSquareColor(((j+i) %2 ), boardString);
                ChessPiece piece = board.getPiece(new ChessPosition(rows.get(i), cols.get(j)));

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
                    .append(numbers.get(7-1)).append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_BG_COLOR).append("\n");
        }


        // BELOW DOES BOTTOM LETTER ROW

        letterRow(boardString, letters);

        boardString.append(EscapeSequences.RESET_BG_COLOR + "\n" + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        return boardString.toString();
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

        ChessPiece promote = getPromote(params);

        return new ChessMove(new ChessPosition(row1, col1), new ChessPosition(row2, col2), promote.getPieceType());
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
                default -> {
                    throw new BadRequestException("\nError: invalid promotion piece given\n");
                }
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


        ChessPiece promote = getPromote(params);

        return new ChessPosition(row1, col1);
    }

    private String highlight(String[] params) throws BadRequestException {
        ChessPosition selected = getPosition(params);

        Collection<ChessMove> moves = game.validMoves(selected);
        List<ChessPosition> positions = new ArrayList<>(List.of());
        positions.add(moves.iterator().next().getStartPosition());
        for (ChessMove move : moves) {
            positions.add(move.getEndPosition());
        }
        return redraw(game, positions);
    }

    private String move(String[] params) throws BadRequestException {
        if (team.equalsIgnoreCase("white") && !(game.getTeamTurn() == ChessGame.TeamColor.WHITE)){
            return "\nIt is not your turn\n";
        } else if (team.equalsIgnoreCase("black") && !(game.getTeamTurn() == ChessGame.TeamColor.BLACK)){
            return "\nIt is not your turn\n";
        }
        ChessMove move = getMove(params);
        return null;
    }

    private void resign(){
        gameOver = true;
    }

    private String help() {
        if (!team.equalsIgnoreCase("observer")){
            return """
                Possible Commands:
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
            }
            case LOAD_GAME -> {
                game = message.getGame();
                System.out.print(redraw(game));
            }
        }
    }

    private String redraw(ChessGame game, List<ChessPosition> positions){
        StringBuilder boardString = new StringBuilder(EscapeSequences.SET_TEXT_BOLD);
        ChessBoard board = game.getBoard();
        String team1;
        String team2;
        if (Objects.equals(team, "BLACK")){
            team1 = EscapeSequences.SET_TEXT_COLOR_BLUE;
            team2 = EscapeSequences.SET_TEXT_COLOR_GREEN;
        } else {
            team1 = EscapeSequences.SET_TEXT_COLOR_GREEN;
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
                setSquareColor(((j+i) %2 ), boardString, positions.getFirst(), currentSquare, positions);
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
                    .append(numbers.get(7-1)).append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_BG_COLOR).append("\n");
        }


        // BELOW DOES BOTTOM LETTER ROW

        letterRow(boardString, letters);

        boardString.append(EscapeSequences.RESET_BG_COLOR + "\n" + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        return boardString.toString();
    }

    private void setSquareColor(int i, StringBuilder board, ChessPosition piecePosition, ChessPosition currentSquare, List<ChessPosition> positions){
        if ((i % 2) == 0){
            board.append(EscapeSequences.SET_BG_COLOR_WHITE);
        }
        else {
            board.append(EscapeSequences.SET_BG_COLOR_BLACK);
        }

        if (positions.contains(currentSquare)) {
            board.append(EscapeSequences.SET_BG_COLOR_YELLOW);
        } else if (positions.contains(piecePosition)){
            board.append(EscapeSequences.SET_BG_COLOR_RED);
        }
    }
}
