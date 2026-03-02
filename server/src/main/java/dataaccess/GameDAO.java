package dataaccess;

public interface GameDAO {
    public default void clearGames() {
        throw new RuntimeException("Not implemented");
    }
}
