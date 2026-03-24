package client;

import exception.BadRequestException;
import exception.DataAccessException;
import result.*;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PostLogClient {
    private final ServerFacade server;
    private final String authToken;
    private final String username;
    private final List<GameInfo> games = new ArrayList<>();
    private int nextIndex = 1;

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
                System.out.print(result + "\n"  + help());
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg  + help());
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

    private String createGame(String[] params) throws DataAccessException {
        NewGameResult game = server.createGame(authToken, params[0]);
        games.add(new GameInfo(nextIndex, null, null, params[0]));
        nextIndex ++;
        return String.format("\nNew game '%s' created \n", params[0]);
    }

    private String observeGame(String[] params) throws DataAccessException {
        int id = getId(params, 1, "Error, expected: observe <gameID>\n");

        if (id <= 0 || id >= nextIndex) {
            throw new BadRequestException("Error: use numbers from the list of games to observe a game\n");
        }
        System.out.print("\nObserving game " + params[0] + "\n");
        new InGameClient("observer").run();

        return "\nLeft game\n";
    }

    private String joinGame(String[] params) throws DataAccessException {
        int id = getId(params, 2, "Error, expected: join <gameID> [WHITE|BLACK]\n");

        if (!params[1].equalsIgnoreCase("WHITE") && !params[1].equalsIgnoreCase("BLACK")) {
            throw new BadRequestException("Error: use WHITE or BLACK for the team color");
        }

        if (id <= 0 || id >= nextIndex) {
            throw new BadRequestException("Error: use numbers from the list of games to observe a game\n");
        }

        GameInfo game = games.get(Integer.parseInt(params[0])-1);
        if (params[1].equalsIgnoreCase("WHITE")){
            server.joinGame(authToken, Integer.parseInt(params[0]), params[1].toUpperCase());
            games.set(Integer.parseInt(params[0])-1, new GameInfo(game.gameID(), username, game.blackUsername(), game.gameName()));
        } else if (params[1].equalsIgnoreCase("BLACK")){
            server.joinGame(authToken, Integer.parseInt(params[0]), params[1].toUpperCase());
            games.set(Integer.parseInt(params[0])-1, new GameInfo(game.gameID(), game.whiteUsername(), username, game.gameName()));
        } else {
            throw new BadRequestException("Error: invalid team color given");
        }
        System.out.printf("\nSuccessfully joined game %d as %s \n", Integer.parseInt(params[0]), params[1].toLowerCase());
        new InGameClient(params[1].toUpperCase()).run();
        return "\nGame exited\n";
    }

    private static int getId(String[] params, int x, String message) throws BadRequestException {
        if (params.length != x) {
            throw new BadRequestException(message);
        }
        String s = params[0];
        if (!(s != null && s.matches("\\d+"))) {
            throw new BadRequestException("Error: use numbers from the list of games\n");
        }

        int id = 0;
        try {
            id = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Error: use numbers from the list of games\n");
        }
        return id;
    }

    private String listGames() throws DataAccessException {
        StringBuilder result = new StringBuilder(String.format(EscapeSequences.SET_BG_COLOR_MAGENTA+
                "| %-5s | %-20s | %-20s | %-20s |" + EscapeSequences.RESET_BG_COLOR + "\n",
                "Index", "White Team", "Black Team", "Name"));
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
                  observe <gameID> - observe an existing game
                  logout - sign out
                  quit - sign out and exit program
                  help - see possible commands
                
                """;
    }

}
