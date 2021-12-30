package atoms.model;

import java.util.ArrayList;
import java.util.List;

// 2 possibilities for BoardChange:
// Minimalistic: Square squareToChange, int originalPlayerId, int electronsChangeAmount
// - Square must be mutable
// - reference to board is not required - we hold reference to square
// - square positions are not needed - we have direct reference to square, maybe original position may be required to
//   compute DetailedMove
//
// We must be able to compute DetailedMove from OriginalMove
// - no, we don't
// - we can just create DetailedMove again from the position
// - make 2 functions to create DetailedMove and Move


// Example heuristics:
// 1. Positions that will cause explosion (square is full)
// 2. Positions that are surrounded with the highest number of enemy squares, but none of them hase higher number of
//    electrons - actually, enemy squares with higher number of atoms should be avoided
// 3. Moves that take over the highest number of squares
// 4. Moves that will take over the highest number of electrons


// I need to decrease the number of enemy squares instead the number of allied squares! once enemy squares count hit zero,
// it will be game over
// - when generating moves, go through the whole board and count enemy squares
// - when generating move, check for stolenSquaresCount == enemySquaresCount

/*
1 2 1
2 4 2
1 2 1

1 3 1
3 0 3
1 3 1

3 0 3
0 4 0
3 0 3

1 3 1
3 0 3
1 3 1

-----
2 1
1 1

0 2
2 1

2 0
0 3

0 2
2 1
-----
1 1
1 1

2 1
1 0

0 2
2 0

2 0
0 2

0 2
2 0
-----





*/


final class Move2 {
    public Move2(int playerId, SquarePosition origin) {
        this.playerId = playerId;
        this.origin = origin;
        changes = new ArrayList<>();
        stolenSquaresCount = 0;
    }

    public void addChange(Square square, int originalPlayerId, int electronsCountChange) {
        changes.add(new SquareChange(square, originalPlayerId, electronsCountChange));
        if (originalPlayerId != Board.NO_PLAYER_ID && originalPlayerId != playerId) {
            stolenSquaresCount++;
        }
    }

    public void apply(Board board) {
        for (SquareChange change : changes) {
            change.square.changeElectronsCount(change.electronsCountChange);
        }
    }

    public void undo(Board board) {
        for (int i = changes.size() - 1; i >= 0; i--) {
            SquareChange change = changes.get(i);
            change.square.changeElectronsCount(-change.electronsCountChange);
        }
    }

    public int getStolenSquaresCount() {
        return stolenSquaresCount;
    }

    public DetailedMove toDetailedMove(Board initialBoard) {
        return null;
    }

    public record SquareChange (Square square, int originalPlayerId, int electronsCountChange) {}


    private final int playerId;
    private final SquarePosition origin;
    private final List<SquareChange> changes;
    private int stolenSquaresCount;
}