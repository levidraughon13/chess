package client;

import java.util.Scanner;

public class PostLogClient {

    public void run() {
        System.out.println(" Login successful!");
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

    private String help() {
        return """
                - register <username> <password> <email>
                - login <username> <password>
                - quit
                """;
    }

    private String eval(String line) {
    }
}
