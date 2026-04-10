package websocket.commands;

public class PlayerJoinCommand extends UserGameCommand {
    private final String color;

    public PlayerJoinCommand(CommandType commandType, String authToken, Integer gameID, String color) {
        super(commandType, authToken, gameID);

        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
