package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    private final String color;

    public LeaveCommand(CommandType commandType, String authToken, Integer gameID, String color) {
        super(commandType, authToken, gameID);

        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
