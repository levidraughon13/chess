package websocket.commands;

public class PlayerLeaveCommand extends UserGameCommand {
    private final String color;

    public PlayerLeaveCommand(CommandType commandType, String authToken, Integer gameID, String color) {
        super(commandType, authToken, gameID);

        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
