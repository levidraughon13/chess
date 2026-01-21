package chess;

import java.util.Collection;

public class BishopMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        ChessPiece piece = board.getPiece(myPosition);
        if (myPosition.getRow() == 8 && myPosition.getColumn() == 8) {

        }
        return null;
    }
}
