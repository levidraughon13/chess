package client;

import java.util.Arrays;
import java.util.Scanner;

public class PostLogClient {
    private final ServerFacade server;
    private final String authToken;
    private final String username;

    public PostLogClient(ServerFacade server, String authToken, String username) {
        this.server = server;
        this.authToken = authToken;
        this.username = username;
    }

    public String run() {
        System.out.println(" Login successful!");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit") && !result.equals("logout")) {

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

    private String help() {
        return """
                - newGame <gameName>
                - joinGame <teamColor> <gameID>
                - listGames
                - logout
                - quit
                """;
    }

    private String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "newGame" -> createGame(params);
                case "joinGame" -> joinGame(params);
                case "listGames" -> listGames(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String createGame(String[] params) {
        return null;
    }

    private String joinGame(String[] params) {
        return null;
    }

    private String listGames(String[] params) {
        return null;
    }

    private String logout(String[] params) {
        return null;
    }

}
