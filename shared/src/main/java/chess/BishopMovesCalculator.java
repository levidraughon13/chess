package chess;

import java.util.Collection;


public class BishopMovesCalculator extends PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return this.diagonalMoves(board, myPosition);
    }
}
