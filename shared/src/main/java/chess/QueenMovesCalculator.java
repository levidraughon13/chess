package chess;

import java.util.Collection;

public class QueenMovesCalculator extends PieceMovesCalculator{
    public QueenMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        Collection<ChessMove> diagonal = this.diagonalMoves();
        Collection<ChessMove> orthogonal = this.orthogonalMoves();
        orthogonal.addAll(diagonal);
        return orthogonal;
    }
}
