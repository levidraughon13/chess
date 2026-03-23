package client;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

import exception.BadRequestException;
import exception.DataAccessException;
import model.*;
import result.LoginResult;
import result.RegisterResult;
import ui.EscapeSequences;

public class PreLogClient {
    private final ServerFacade server;

    public PreLogClient(String serverUrl) {
        this.server = new ServerFacade(serverUrl);
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
            return ex.getMessage() + "\n" + help();
        }
    }

    private String login(String[] params) throws DataAccessException {
        if (params.length == 2) {
                LoginResult auth = server.login(params[0], params[1]);
                String result = new PostLogClient(server, auth.authToken(), auth.username()).run();
                if (Objects.equals(result, "quit")) {
                    server.logout(auth.authToken());
                    return "quit";
                }
                return "Logout Successful";
            }
        throw new BadRequestException("Error: expected <username> <password>");
    }

    private String register(String[] params) throws DataAccessException {
        if (params.length == 3) {
            RegisterResult auth = server.register(new UserData(params[0], params[1], params[2]));
            String result = new PostLogClient(server, auth.authToken(), auth.username()).run();
            if (Objects.equals(result, "quit")) {
                server.logout(auth.authToken());
                return "quit";
            }
            return "\nLogout Successful\n";
        }
        throw new BadRequestException("Error: expected <username> <password> <email>");

    }

    private String help() {
        return """
                Possible Commands:
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                """;
    }
}
