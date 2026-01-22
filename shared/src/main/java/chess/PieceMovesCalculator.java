package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
        List<ChessPosition> validSquares = new ArrayList<>();
        while (rowShift < 7 && colShift < 7){
            if (q1){
                ChessPosition next = new ChessPosition(currentRow-rowShift, currentCol-colShift);
                if (next.getRow() < 1 || next.getColumn() < 1) {
                    q1 = false;
                } else if (board.getPiece(next) == null) {
                    validSquares.add(next);
                } else if (board.getPiece(next).getTeamColor() == piece.getTeamColor()) {
                    q1 = false;
                } else if (board.getPiece(next).getTeamColor() != piece.getTeamColor()) {
                    validSquares.add(next);
                    q1 = false;
                }
            }
            if (q2){
                ChessPosition next = new ChessPosition(currentRow-rowShift, currentCol+colShift);
                if (next.getRow() < 1 || next.getColumn() > 8) {
                    q2 = false;
                } else if (board.getPiece(next) == null) {
                    validSquares.add(next);
                } else if (board.getPiece(next).getTeamColor() == piece.getTeamColor()) {
                    q2 = false;
                } else if (board.getPiece(next).getTeamColor() != piece.getTeamColor()) {
                    validSquares.add(next);
                    q2 = false;
                }
            }
            if (q3){
                ChessPosition next = new ChessPosition(currentRow+rowShift, currentCol-colShift);
                if (next.getRow() > 8 || next.getColumn() < 1) {
                    q3 = false;
                } else if (board.getPiece(next) == null) {
                    validSquares.add(next);
                } else if (board.getPiece(next).getTeamColor() == piece.getTeamColor()) {
                    q3 = false;
                } else if (board.getPiece(next).getTeamColor() != piece.getTeamColor()) {
                    validSquares.add(next);
                    q3 = false;
                }
            }
            if (q4){
                ChessPosition next = new ChessPosition(currentRow+rowShift, currentCol+colShift);
                if (next.getRow() > 8 || next.getColumn() > 8) {
                    q4 = false;
                } else if (board.getPiece(next) == null) {
                    validSquares.add(next);
                } else if (board.getPiece(next).getTeamColor() == piece.getTeamColor()) {
                    q4 = false;
                } else if (board.getPiece(next).getTeamColor() != piece.getTeamColor()) {
                    validSquares.add(next);
                    q4 = false;
                }
            }
            rowShift ++;
            colShift ++;
        }
        Collection<ChessMove> moves = new ArrayList<>();
        for (ChessPosition position : validSquares){
            moves.add(new ChessMove(myPosition, position, null));
        }
        return moves;
    }
}
