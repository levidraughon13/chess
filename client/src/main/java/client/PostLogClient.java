package client;

import exception.DataAccessException;
import result.*;
import ui.EscapeSequences;

import java.util.Arrays;
import java.util.List;
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
        System.out.println(" Login successful, welcome " + username + "!");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit") && !result.equals("logout")) {

            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result + "\n");
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
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "list" -> listGames();
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String observeGame(String[] params) {
        return "Observing game" + params[0];
    }

    private String createGame(String[] params) throws DataAccessException {
        NewGameResult game = server.createGame(authToken, params[0]);
        return String.format("\nNew game '%s' created with game ID %d \n", params[0], game.gameID());
    }

    private String joinGame(String[] params) throws DataAccessException {
        server.joinGame(authToken, Integer.parseInt(params[0]), params[1].toUpperCase());
        System.out.printf("\nSuccessfully joined game %d as %s \n", Integer.parseInt(params[0]), params[1].toLowerCase());
        new InGameClient(params[1].toUpperCase()).run();
        return "\nGame exited\n";
    }

    private String listGames() throws DataAccessException {
        GameList gameList = server.listGames(authToken);
        List<GameInfo> games = gameList.games();
        StringBuilder result = new StringBuilder(String.format(EscapeSequences.SET_BG_COLOR_MAGENTA+"| %-5s | %-20s | %-20s | %-20s |" + EscapeSequences.RESET_BG_COLOR + "\n", "Index", "White Team", "Black Team", "Name"));
        for (int i = 0; i < games.size(); i++){
            GameInfo game = games.get(i);
            result.append(String.format("| %-5s | %-20s | %-20s | %-20s |\n", i + 1, game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return result.toString() + "\n";
    }

    private String logout() throws DataAccessException {
        server.logout(authToken);
        return "logout";
    }

    private String help() {
        return """
                \nPossible Commands:
                  create <gameName> - create a new game
                  join <gameID> [WHITE or BLACK] - join an existing game
                  list - list all games
                  logout - sign out
                  quit - sign out and exit program
                  help - see possible commands
                
                """;
    }

}
