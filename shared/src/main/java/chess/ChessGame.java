package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        setTeamTurn(TeamColor.WHITE);
        this.teamTurn = getTeamTurn();
        setBoard(board);
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> moves = board.getPiece(startPosition).pieceMoves(this.board,startPosition);
        try{
            ChessBoard copy = board.clone();
            if (isInCheck(getTeamTurn())) {
                for (ChessMove move : moves) {
                    board.addPiece(move.getEndPosition(), copy.getPiece(startPosition));
                    board.addPiece(startPosition, null);
                    if (isInCheck(getTeamTurn())) {
                        moves.remove(move);
                    }
                    this.board = copy;
                }
            }
        } catch (CloneNotSupportedException e){
            throw new RuntimeException("Cloning Error", e);
        }

        return moves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        if (!(valid.contains(move))){
            throw new InvalidMoveException();
        }
        if (isInCheckmate(getTeamTurn())){ return; }

        board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
        board.addPiece(move.getStartPosition(), null);
        if (teamTurn == TeamColor.WHITE){ teamTurn = TeamColor.BLACK; }
        else if (teamTurn == TeamColor.BLACK){ teamTurn = TeamColor.WHITE; }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingSpot = getKingSquare(teamColor);
        Collection<ChessMove> threats = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                ChessPiece piece = this.board.board[i][j];
                if (piece != null && piece.getTeamColor() != teamColor){
                    threats.addAll(piece.pieceMoves(board, new ChessPosition(i+1, j+1)));
                }
            }
        }
        for (ChessMove move : threats){
            if (move.getEndPosition().equals(kingSpot)){
                return true;
            }
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!(isInCheck(teamColor))){
            return false;
        }
        Collection<ChessPosition> positions = teamPositions(teamColor);
        Collection<ChessMove> moves = new ArrayList<>();
        for (ChessPosition position : positions){
            moves.addAll(validMoves(position));
        }
        return moves.isEmpty();
    }

    public ChessPosition getKingSquare(TeamColor teamColor){
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                ChessPiece piece = this.board.board[i][j];
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING){
                    return new ChessPosition(i+1, j+1);
                }
            }
        }
        return null;
    }

    public Collection<ChessPosition> teamPositions(TeamColor teamColor){
        Collection<ChessPosition> positions = new ArrayList<>();
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                ChessPiece piece = this.board.board[i][j];
                if (piece != null && piece.getTeamColor() == teamColor){
                    positions.add(new ChessPosition(i+1,j+1));
                }
            }
        }
        return positions;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor) || isInCheckmate(teamColor)){
            return false;
        }
        Collection<ChessPosition> positions = teamPositions(teamColor);
        Collection<ChessMove> moves = new ArrayList<>();
        for (ChessPosition position : positions){
            moves.addAll(validMoves(position));
        }
        return moves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        board.resetBoard();
        this.board  = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return this.board;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
