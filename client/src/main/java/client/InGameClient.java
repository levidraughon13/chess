package client;

import ui.EscapeSequences;
import ui.EscapeSequences.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class InGameClient {
    private final ServerFacade server;
    private final String authToken;


    public InGameClient(ServerFacade server, String authToken) {
        this.server = server;
        this.authToken = authToken;
    }

    public String run() {
        System.out.println(" ");
        System.out.print(printInitialBoard());
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("exit")) {

            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        return result;
    }


    private String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "exit" -> "exit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    public String printInitialBoard() {
        String board = EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + " " + EscapeSequences.EMPTY + EscapeSequences.SET_TEXT_BOLD;
        List<String> letters = List.of("A", "B", "C", "D" , "E", "F", "G", "H");
        List<String> backPieces = List.of("R", "N", "B", "Q", "K", "B", "N", "R");

        for (int i = 0; i < letters.size(); i++){
            board += EscapeSequences.EMPTY + letters.get(i) + EscapeSequences.EMPTY;
        }

        board += (EscapeSequences.EMPTY + " " + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR + "\n") +
                (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + "8" + EscapeSequences.EMPTY + EscapeSequences.SET_TEXT_COLOR_BLUE);
        for (int i = 0; i < backPieces.size(); i++){
            if ((i % 2) == 0){
                board += EscapeSequences.SET_BG_COLOR_WHITE;
            }
            else {
                board += EscapeSequences.SET_BG_COLOR_BLACK;
            }
            board += EscapeSequences.EMPTY + backPieces.get(i) + EscapeSequences.EMPTY;
        }
        board += (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.EMPTY + "8" + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR) +
                (EscapeSequences.EMPTY + "   " + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR + "\n");

        board += (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + "7" + EscapeSequences.EMPTY + EscapeSequences.SET_TEXT_COLOR_BLUE);
        for (int i = 0; i < 8; i++){
            if ((i % 2) == 0){
                board += EscapeSequences.SET_BG_COLOR_BLACK;
            }
            else {
                board += EscapeSequences.SET_BG_COLOR_WHITE;
            }
            board += EscapeSequences.EMPTY + "P" + EscapeSequences.EMPTY;
        }
        board += (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.EMPTY + "7" + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR + "\n");
        for (int i = 6; i > 2; i--){
            board += (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + i + EscapeSequences.EMPTY);
            for (int j = 0; j < 8; j++){
                if ((i%2) == 0){
                    if ((j%2) == 0) {
                        board += EscapeSequences.SET_BG_COLOR_WHITE;
                    } else {
                        board += EscapeSequences.SET_BG_COLOR_BLACK;
                    }
                } else {
                    if ((j%2) == 0) {
                        board += EscapeSequences.SET_BG_COLOR_BLACK;
                    } else {
                        board += EscapeSequences.SET_BG_COLOR_WHITE;
                    }
                }
                board += EscapeSequences.EMPTY + " " + EscapeSequences.EMPTY;
            }
            board += ( EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + i + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR + "\n");
        }

        board += (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + "2" + EscapeSequences.EMPTY + EscapeSequences.SET_TEXT_COLOR_GREEN);
        for (int i = 0; i < 8; i++){
            if ((i % 2) == 0){
                board += EscapeSequences.SET_BG_COLOR_WHITE;
            }
            else {
                board += EscapeSequences.SET_BG_COLOR_BLACK;
            }
            board += EscapeSequences.EMPTY + "P" + EscapeSequences.EMPTY;
        }
        board += (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.EMPTY + "2" + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR + "\n");


        board += (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + "1" + EscapeSequences.EMPTY + EscapeSequences.SET_TEXT_COLOR_GREEN);
        for (int i = 0; i < backPieces.size(); i++){
            if ((i % 2) == 0){
                board += EscapeSequences.SET_BG_COLOR_BLACK;
            }
            else {
                board += EscapeSequences.SET_BG_COLOR_WHITE;
            }
            board += EscapeSequences.EMPTY + backPieces.get(i) + EscapeSequences.EMPTY;
        }
        board += (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.RESET_TEXT_COLOR + EscapeSequences.EMPTY + "1" + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR) +
                (EscapeSequences.EMPTY + " " + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR + "\n");

        board += (EscapeSequences.SET_BG_COLOR_LIGHT_GREY + EscapeSequences.EMPTY + " " + EscapeSequences.EMPTY);
        for (int i = 0; i < letters.size(); i++){
            board += EscapeSequences.EMPTY + letters.get(i) + EscapeSequences.EMPTY;
        }
        board += (EscapeSequences.EMPTY + " " + EscapeSequences.EMPTY + EscapeSequences.RESET_BG_COLOR + "\n" + EscapeSequences.RESET_TEXT_BOLD_FAINT);

        return board;
    }

    private String help() {
        return """
                Possible Commands:
                  exit - exit game
                                 
                """;
    }
}
