package atoms.model;

/**
 * The state of the game board.
 */
public class BoardState {

    /**
     * Creates a board state caused by a move.
     * @param board The board.
     * @param origin The target position that was the cause of this state.
     * @param electronCounts The number of electrons of each player.
     */
    public BoardState(Board board, SquarePosition origin, int[] electronCounts) {
        this.board = board;
        this.origin = origin;
        this.electronCounts = electronCounts;
    }

    /**
     * Creates an initial board state.
     * @param boardSize The size of the board.
     * @param playersCount The number of players.
     */

    public BoardState(int boardSize, int playersCount) {
        board = new Board(boardSize);
        origin = null;
        electronCounts = new int[playersCount];
    }

    /**
     * Checks if all players have made a move.
     * @return An indicator whether all players have made a move.
     */
    public boolean allPlayersMoved() {
        int totalElectronsCount = 0;
        for (int playerElectrons : electronCounts) {
            totalElectronsCount += playerElectrons;
        }
        return totalElectronsCount >= electronCounts.length;
    }

    /**
     * Checks whether the state is terminal (a player won).
     * @return An indicator whether the state is terminal (a player won).
     */
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

    /**
     * Gets the target square of the move that caused this state.
     * @return The target square of the move that caused this state.
     */
    public SquarePosition getTarget() {
        return origin;
    }

    /**
     * Gets the number of electrons of a player.
     * @param playerId The ID of the player.
     * @return The number of electrons of the player.
     */
    public int getElectronsCount(int playerId) {
        return electronCounts[playerId];
    }

    /**
     * Gets the number of electrons of each player.
     * @return The number of electrons of each player.
     */
    public int[] getAllElectronCounts() {
        return electronCounts;
    }

    /**
     * Gets the board.
     * @return The board.
     */
    public Board getBoard() {
        return board;
    }

    private final Board board;
    private final SquarePosition origin;
    private final int[] electronCounts;
}
