package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PieceMovesCalculator {

    protected final ChessBoard board;
    protected final ChessPosition myPosition;
    protected final ChessPiece piece;
    protected final int currentRow;
    protected final int currentCol;

    public PieceMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        this.board = board;
        this.myPosition = myPosition;
        this.piece = board.getPiece(myPosition);
        this.currentRow = myPosition.getRow();
        this.currentCol = myPosition.getColumn();
    }

    public Collection<ChessMove> pieceMoves() {
        throw new RuntimeException("Not implemented");
    }

    /**
     * @return two booleans in a list. 1st boolean indicated whether a move can be made to 'next'
     *         The second boolean indicates whether the path to further squares is blocked
     */
    public List<Boolean> checkValidSquare (ChessPosition next) {
        if ((next.getRow() < 1 || next.getRow() > 8) || (next.getColumn() < 1 || next.getColumn() > 8)) {
            return List.of(false, true);
        } else if (this.board.getPiece(next) == null) {
            return List.of(true, false);
        } else if (this.board.getPiece(next).getTeamColor() == this.piece.getTeamColor()) {
            return List.of(false, true);
        } else if (this.board.getPiece(next).getTeamColor() != this.piece.getTeamColor()) {
            return List.of(true, true);
        }
        return List.of(true, false);
    }

    public Collection<ChessMove> diagonalMoves(){
        boolean q1 = true;
        boolean q2 = true;
        boolean q3 = true;
        boolean q4 = true;
        int shift = 1;
        Collection<ChessMove> moves = new ArrayList<>();
        while (shift < 7 && (q1 || q2 || q3 || q4)){
            if (q1){
                ChessPosition next = new ChessPosition(this.currentRow-shift, this.currentCol-shift);
                List<Boolean> result = checkValidSquare(next);
                if (result.getFirst()) { moves.add(new ChessMove(this.myPosition, next, null)); }
                if (result.get(1)) { q1 = false; }
            }
            if (q2){
                ChessPosition next = new ChessPosition(this.currentRow-shift, this.currentCol+shift);
                List<Boolean> result = checkValidSquare(next);
                if (result.getFirst()) { moves.add(new ChessMove(this.myPosition, next, null)); }
                if (result.get(1)) { q2 = false; }
            }
            if (q3){
                ChessPosition next = new ChessPosition(this.currentRow+shift, this.currentCol-shift);
                List<Boolean> result = checkValidSquare(next);
                if (result.getFirst()) { moves.add(new ChessMove(this.myPosition, next, null)); }
                if (result.get(1)) { q3 = false; }
            }
            if (q4){
                ChessPosition next = new ChessPosition(this.currentRow+shift, this.currentCol+shift);
                List<Boolean> result = checkValidSquare(next);
                if (result.getFirst()) { moves.add(new ChessMove(this.myPosition, next, null)); }
                if (result.get(1)) { q4 = false; }
            }
            shift ++;
        }
        return moves;
    }

    public Collection<ChessMove> orthogonalMoves(){
        boolean up = true;
        boolean down = true;
        boolean left = true;
        boolean right = true;
        int shift = 1;
        Collection<ChessMove> moves = new ArrayList<>();
        while (shift < 7 && (up || down || left || right)){
            if (up){
                ChessPosition next = new ChessPosition(this.currentRow+shift, this.currentCol);
                List<Boolean> result = checkValidSquare(next);
                if (result.getFirst()) { moves.add(new ChessMove(this.myPosition, next, null)); }
                if (result.get(1)) { up = false; }
            }
            if (down){
                ChessPosition next = new ChessPosition(this.currentRow-shift, this.currentCol);
                List<Boolean> result = checkValidSquare(next);
                if (result.getFirst()) { moves.add(new ChessMove(this.myPosition, next, null)); }
                if (result.get(1)) { down = false; }
            }
            if (left){
                ChessPosition next = new ChessPosition(this.currentRow, this.currentCol-shift);
                List<Boolean> result = checkValidSquare(next);
                if (result.getFirst()) { moves.add(new ChessMove(this.myPosition, next, null)); }
                if (result.get(1)) { left = false; }
            }
            if (right){
                ChessPosition next = new ChessPosition(this.currentRow, this.currentCol+shift);
                List<Boolean> result = checkValidSquare(next);
                if (result.getFirst()) { moves.add(new ChessMove(this.myPosition, next, null)); }
                if (result.get(1)) { right = false; }
            }
            shift ++;
        }

        return moves;
    }
}
