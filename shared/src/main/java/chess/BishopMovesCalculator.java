package chess;

import java.util.Collection;


public class BishopMovesCalculator extends PieceMovesCalculator {


    public BishopMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        return this.diagonalMoves();
    }
}
