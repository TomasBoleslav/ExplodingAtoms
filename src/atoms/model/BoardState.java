package atoms.model;

// forbid move that starts 2 or more squares away from allied or enemy squares
// - it may cause speed up in the beginning

// Don't have difficulties - just have 1 difficulty computing to the maximum possible depth

// Consider not separating move into explosions
// - but animation with multiple explosions would be faster if they happen in waves
// - yeah, separate move into explosions for faster animations (what if e.g. 10 explosions happen? move would take a lot of seconds)
// How should animations look like:
// - explosion origin is red
// - explosion targets are yellow
// - squares can have up to 7 circles

public class BoardState {
    public BoardState(Board board, SquarePosition origin, int[] electronCounts) {
        this.board = board;
        this.origin = origin;
        this.electronCounts = electronCounts;
    }

    public BoardState(int boardSize, int playersCount) {
        board = new Board(boardSize);
        origin = null;
        electronCounts = new int[playersCount];
    }

    public boolean allPlayersMoved() {
        int totalElectronsCount = 0;
        for (int playerElectrons : electronCounts) {
            totalElectronsCount += playerElectrons;
        }
        return totalElectronsCount >= electronCounts.length;
    }

    public boolean isTerminal() {
        int totalElectronsCount = 0;
        int playersAlive = 0;
        for (int playerElectrons : electronCounts) {
            if (playerElectrons > 0) {
                if (playersAlive > 0) {
                    return false;
                }
                playersAlive++;
                totalElectronsCount += playerElectrons;
            }
        }
        return totalElectronsCount >= electronCounts.length;
    }

    public BoardState copy() {
        return new BoardState(
                board.copy(),
                origin,
                electronCounts.clone()
        );
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public SquarePosition getTarget() {
        return origin;
    }

    public int getElectronsCount(int playerId) {
        return electronCounts[playerId];
    }

    private final Board board;
    private final SquarePosition origin;
    private final int[] electronCounts;
    private int value;
}
