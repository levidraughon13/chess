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

        //checks if pawn at end of board
        if (this.piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (this.myPosition.getRow() == 8) { return moves; }
        } else {
            if (this.myPosition.getRow() == 1) { return moves;}
        }

        List<ChessPosition> squares = getMovePositions();

        // cases for moving forward one space and moving forward two spacs if in starting position
        if (this.board.getPiece(squares.getFirst()) == null) {
            List<Boolean> result = this.checkValidSquare(squares.getFirst());
            if (result.getFirst()){
                moves.addAll(getPromotionMoves(squares.getFirst()));
                if (this.piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    if (this.currentRow == 2){
                        if (this.board.getPiece(squares.get(3)) == null) {
                            moves.add(new ChessMove(this.myPosition, squares.get(3), null));
                        }
                    }
                } else {
                    if (this.currentRow == 7) {
                        if (this.board.getPiece(squares.get(3)) == null) {
                            moves.add(new ChessMove(this.myPosition, squares.get(3), null));
                        }
                    }
                }
            }
        }

        //cases for capture pieces
        if (this.currentCol != 8) {
            if (this.board.getPiece(squares.get(1)) != null) {
                if (this.board.getPiece(squares.get(1)).getTeamColor() != this.piece.getTeamColor()) {
                    moves.addAll(getPromotionMoves(squares.get(1)));
                }
            }
        }
        if (this.currentCol != 1) {
            if (this.board.getPiece(squares.get(2)) != null) {
                if (this.board.getPiece(squares.get(2)).getTeamColor() != this.piece.getTeamColor()) {
                    moves.addAll(getPromotionMoves(squares.get(2)));
                }
            }
        }

        return moves;
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
            ChessPosition sq1 = new ChessPosition(this.currentRow+1, this.currentCol);
            ChessPosition sq2 = new ChessPosition(this.currentRow+1, this.currentCol+1);
            ChessPosition sq3 = new ChessPosition(this.currentRow+1, this.currentCol-1);
            ChessPosition sq4 = new ChessPosition(this.currentRow+2, this.currentCol);
            return List.of(sq1, sq2, sq3, sq4);
        }
        else if (this.piece.getTeamColor() == ChessGame.TeamColor.BLACK){
            ChessPosition sq1 = new ChessPosition(this.currentRow-1, this.currentCol);
            ChessPosition sq2 = new ChessPosition(this.currentRow-1, this.currentCol+1);
            ChessPosition sq3 = new ChessPosition(this.currentRow-1, this.currentCol-1);
            ChessPosition sq4 = new ChessPosition(this.currentRow-2, this.currentCol);
            return List.of(sq1, sq2, sq3, sq4);
        }
        return List.of();
    }
}
