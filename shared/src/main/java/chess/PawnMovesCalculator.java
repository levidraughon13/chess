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

        if (this.piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (this.myPosition.getRow() == 8) { return moves; }
        } else {
            if (this.myPosition.getRow() == 1) { return moves;}
        }

        List<ChessPosition> squares = getMovePositions();



        List<Boolean> result = this.checkValidSquare(squares.getFirst());
        if (this.board.getPiece(squares.getFirst()) == null) {
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

    public Boolean checkPromotable(ChessPosition square) {
        if (this.piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return square.getRow() == 8;
        } else {
            return square.getRow() == 1;
        }
    }

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
