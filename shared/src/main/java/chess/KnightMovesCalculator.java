package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator extends PieceMovesCalculator {
    public KnightMovesCalculator(ChessBoard board, ChessPosition myPosition) {
        super(board, myPosition);
    }

    @Override
    public Collection<ChessMove> pieceMoves() {
        List<ChessPosition> squares = getMovePositions();
        Collection<ChessMove> moves = new ArrayList<>();
        for (ChessPosition position: squares) {
            List<Boolean> result = this.checkValidSquare(position);
            if (result.getFirst()){
                moves.add(new ChessMove(this.myPosition, position, null));
            }
        }
        return moves;
    }

    private List<ChessPosition> getMovePositions() {
        ChessPosition sq1 = new ChessPosition(this.currentRow+2, this.currentCol-1);
        ChessPosition sq2 = new ChessPosition(this.currentRow+1, this.currentCol-2);
        ChessPosition sq3 = new ChessPosition(this.currentRow+2, this.currentCol+1);
        ChessPosition sq4 = new ChessPosition(this.currentRow+1, this.currentCol+2);
        ChessPosition sq5 = new ChessPosition(this.currentRow-2, this.currentCol-1);
        ChessPosition sq6 = new ChessPosition(this.currentRow-1, this.currentCol-2);
        ChessPosition sq7 = new ChessPosition(this.currentRow-2, this.currentCol+1);
        ChessPosition sq8 = new ChessPosition(this.currentRow-1, this.currentCol+2);

        return List.of(sq1, sq2, sq3, sq4, sq5, sq6, sq7, sq8);
    }
}
