package chess;

import java.util.Collection;

public class RookMovesCalculator extends PieceMovesCalculator{

    public RookMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        return this.orthogonalMoves();
    }
}