package client;

import java.util.Arrays;
import java.util.Scanner;
import ui.EscapeSequences;

public class PreLogClient {
    private final ServerFacade server;

    public PreLogClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run() {
        System.out.println(" Welcome to the chess! Log in to start.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {

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
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Throwable ex) {
            return ex.getMessage();
        }
    }

    private String login(String[] params) {
        return null;
    }

    private String register(String[] params) {
        return null;
    }

    private String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                """;
    }
}
