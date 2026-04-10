package websocket.commands;

public class ResignCommand extends UserGameCommand {
    private final String color;

    public ResignCommand(CommandType commandType, String authToken, Integer gameID, String color) {
        super(commandType, authToken, gameID);
        this.color = color;
    }

    public String getColor() {
        return color;
    }
}
