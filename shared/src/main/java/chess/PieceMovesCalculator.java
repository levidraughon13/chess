package chess;

import java.util.Collection;

public class PieceMovesCalculator {

    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        throw new RuntimeException("Not implemented");
    }

    public Collection<ChessMove> diagonalMoves(ChessBoard board, ChessPosition myPosition){
        ChessPiece piece = board.getPiece(myPosition);
        int currentRow = myPosition.getRow();
        int currentCol = myPosition.getColumn();
        boolean q1 = true;
        boolean q2 = true;
        boolean q3 = true;
        boolean q4 = true;
        int rowShift = 1;
        int colShift = 1;
        while (rowShift < 8 && colShift < 8){

            rowShift ++;
            colShift ++;
        }
        throw new RuntimeException("Not implemented");
    }
}
