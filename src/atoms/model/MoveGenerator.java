package atoms.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// NOTE: stolen squares cannot be used to check if move is winning
// - it counts empty squares
// - what if I count only occupied squares?

public class MoveGenerator {
    public static List<Move> generateMoves(Board board, int playerId) {
        int otherSquaresCount = countOtherSquares(board, playerId);
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                Square square = board.getSquare(i, j);
                if (square.getPlayerId() != playerId) {
                    otherSquaresCount++;
                }
            }
        }
    }

    private static Move2 createMove(Board board, SquarePosition origin, int playerId, int otherSquaresCount) {
        Move2 move = new Move2(playerId, origin);
        Queue<SquarePosition> targets = new LinkedList<>();
        targets.add(origin);
        while (!targets.isEmpty() && !moveStoleAllSquares(move, otherSquaresCount)) {
            SquarePosition target = targets.remove();
            Square square = board.getSquare(target);
            List<SquarePosition> explosionTargets = getExplosionTargets(board, target);
            int minElectronsCountForExplosion = explosionTargets.size();
            int oldElectronsCount = square.getElectronsCount();
            int newElectronsCount = oldElectronsCount + 1;
            if (newElectronsCount >= minElectronsCountForExplosion) {
                newElectronsCount -= minElectronsCountForExplosion;
                targets.addAll(explosionTargets);
            }
            square.set(playerId, newElectronsCount);
            move.addChange(square, playerId, newElectronsCount - oldElectronsCount);
        }
        return move;
    }

    private static boolean moveStoleAllSquares(Move2 move, int otherSquaresCount) {
        return move.getStolenSquaresCount() == otherSquaresCount && otherSquaresCount > 0;
    }

    private static int countOtherSquares(Board board, int playerId) {
        int otherSquaresCount = 0;
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                Square square = board.getSquare(i, j);
                if (square.getPlayerId() != playerId) {
                    otherSquaresCount++;
                }
            }
        }
        return otherSquaresCount;
    }

    public static DetailedMove createDetailedMove(Board board, SquarePosition position, int playerId) {
        return null;
    }

    private static List<SquarePosition> getExplosionTargets(Board board, SquarePosition position) {
        List<SquarePosition> explosionTargets = new ArrayList<>();
        int prevRow = position.row() - 1;
        int nextRow = position.row() + 1;
        int prevColumn = position.column() - 1;
        int nextColumn = position.column() + 1;
        if (prevRow >= 0) {
            explosionTargets.add(new SquarePosition(prevRow, position.column()));
        }
        if (nextRow < board.getSize()) {
            explosionTargets.add(new SquarePosition(nextRow, position.column()));
        }
        if (prevColumn >= 0) {
            explosionTargets.add(new SquarePosition(position.row(), prevColumn));
        }
        if (nextColumn < board.getSize()) {
            explosionTargets.add(new SquarePosition(position.row(), nextColumn));
        }
        return explosionTargets;
    }
}
