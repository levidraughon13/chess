package client;

import ui.EscapeSequences;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class InGameClient {
    private final String team;


    public InGameClient(String color) {
        this.team = color;
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
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }


    private String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "leave" -> "leave";
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

    private String help() {
        return """
                Possible Commands:
                  leave - exit game
                  help - see possible commands
                """;
    }
}
