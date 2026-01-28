package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PawnMovesCalculator extends PieceMovesCalculator {

    public PawnMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        Collection<ChessMove> moves = new ArrayList<>();
        List<ChessPosition> squares = getMovePositions();
        List<Boolean> result0 = checkValidSquare(squares.getFirst());
        List<Boolean> result1 = checkValidSquare(squares.get(1));
        List<Boolean> result2 = checkValidSquare(squares.get(2));
        List<Boolean> result3 = checkValidSquare(squares.get(3));
        if (result0.getFirst() && result0.get(1)){
            moves.addAll(getPromotionMoves(squares.getFirst()));
        }
        if (result1.getFirst() && result1.get(1)){
            moves.addAll(getPromotionMoves(squares.get(1)));
        }
        if (result2.getFirst() && !(result2.get(1))){
            moves.addAll(getPromotionMoves(squares.get(2)));
            if (inStartPosition()){
                if (result3.getFirst() && !(result3.get(1))){
                    moves.add(new ChessMove(myPosition, squares.get(3), null));
                }
            }
        }
        return moves;
    }

    public Boolean inStartPosition(){
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return currentRow == 2;
        }
        return currentRow == 7;
    }

    /**
     * @return a boolean to know whether the pawn can promote or not
     * given the space it's moving to
     */
    public Boolean checkPromotable(ChessPosition square) {
        if (this.piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return square.getRow() == 8;
        } else {
            return square.getRow() == 1;
        }
    }

    /**
     * @return a collection of moves to a given space with all types of promotion pieces
     */
    public Collection<ChessMove> getPromotionMoves (ChessPosition square) {
        Collection<ChessMove> moves = new ArrayList<>();
        if (this.checkPromotable(square)) {
            moves.add(new ChessMove(this.myPosition, square, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(this.myPosition, square, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(this.myPosition, square, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(this.myPosition, square, ChessPiece.PieceType.ROOK));
        } else {
            moves.add(new ChessMove(this.myPosition, square, null));
        }
        return moves;
    }

    /**
     * @return possible spaces this pawn could move to depending
     * on its color
     */
    private List<ChessPosition> getMovePositions() {
        if (this.piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            ChessPosition sq1 = new ChessPosition(this.currentRow+1, this.currentCol+1);
            ChessPosition sq2 = new ChessPosition(this.currentRow+1, this.currentCol-1);
            ChessPosition sq3 = new ChessPosition(this.currentRow+1, this.currentCol);
            ChessPosition sq4 = new ChessPosition(this.currentRow+2, this.currentCol);
            return List.of(sq1, sq2, sq3, sq4);
        }
        else if (this.piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            ChessPosition sq1 = new ChessPosition(this.currentRow-1, this.currentCol+1);
            ChessPosition sq2 = new ChessPosition(this.currentRow-1, this.currentCol-1);
            ChessPosition sq3 = new ChessPosition(this.currentRow-1, this.currentCol);
            ChessPosition sq4 = new ChessPosition(this.currentRow-2, this.currentCol);
            return List.of(sq1, sq2, sq3, sq4);
        }
        return List.of();
    }
}
