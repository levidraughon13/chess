package client;

import exception.DataAccessException;
import result.*;

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
                Possible Commands:
                  create <gameName> - create a new game
                  join <gameID> [WHITE or BLACK] - join an existing game
                  list - list all games
                  logout - sign out
                  quit - sign out and exit program
                  help - see possible commands
                
                """;
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
        return null;
    }

    private String createGame(String[] params) throws DataAccessException {
        NewGameResult game = server.createGame(authToken, params[0]);
        return String.format("New game %s created with game ID %d \n", params[0], game.gameID());
    }

    private String joinGame(String[] params) throws DataAccessException {
        server.joinGame(authToken, Integer.parseInt(params[0]), params[1].toUpperCase());
        System.out.printf("Successfully joined game %d as %s \n", Integer.parseInt(params[0]), params[1].toLowerCase());
        new InGameClient(server, authToken, params[1].toUpperCase()).run();
        return "Game exited";
    }

    private String listGames() throws DataAccessException {
        GameList gameList = server.listGames(authToken);
        List<GameInfo> games = gameList.games();
        StringBuilder result = new StringBuilder("| Index | White Team | Black Team | Name |\n");
        for (int i = 0; i < games.size(); i++){
            GameInfo game = games.get(i);
            result.append(String.format("| %d | %s | %s | %s |\n", i + 1, game.whiteUsername(), game.blackUsername(), game.gameName()));
        }
        return result.toString();
    }

    private String logout() throws DataAccessException {
        server.logout(authToken);
        return "logout";
    }

}
