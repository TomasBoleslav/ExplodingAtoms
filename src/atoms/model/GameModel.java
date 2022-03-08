package atoms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Random;
import java.util.stream.IntStream;

// Mathematical proofs:
// 1. If there is endless loop of explosions, the player who caused it will take over all enemy electrons
// Proof:
//   By contradiction. Let's assume player causes an endless loop and an enemy square will be left untouched.
//   Let's take the boundary inside which the explosion of the loop happen and outside not. The number of squares
//   neighboring the boundary is finite. Every time explosion happens inside the boundary, an electron will be sent
//   outside. Thus squares neighboring the boundary have an endless supply of electrons, so they must explode too at
//   one point. Hence, we have the contradiction with the fact that atoms outside the boundary do not explode.
// Consequence: We can stop the game when all squares belong to 1 player
//
// 2. Two neighboring atoms cannot explode at the same time
// Proof:
//   Explosions happen in waves and are initiated with 1 explosion, when we put 1 electron to the board.
//   Let's say we put this electron to white square. Then in the next wave, the explosions can be initiated only from
//   black squares. In the following wave, the explosions can be initiated only from the white squares, etc.
//   Thus, each wave of explosions is initiated only from white squares or only from black squares.
//   2 neighbors cannot explode in the same wave, because they have different square colors.

/**
 * Model of the Exploding Atoms game.
 */
public final class GameModel {
    public static final int PLAYERS_COUNT = 2;
    public static final int BOARD_SIZE = 8;

    private static final int MINIMAX_DEPTH = 3;
    private static final Random random = new Random(0);
    private int moveCounter = 0; // TODO: remove
    private int winnerId;
    private BoardState currentBoardState;
    private int currentPlayerId;

    /**
     * Creates a new game model.
     */
    public GameModel() {
        currentBoardState = new BoardState(BOARD_SIZE, PLAYERS_COUNT);
        currentPlayerId = 0;
        winnerId = Board.NO_PLAYER_ID;
    }

    /**
     * Gets the ID of the current player.
     * @return The ID of the current player.
     */
    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    /**
     * Checks whether the game is over.
     * @return An indicator whether the game is over.
     */
    public boolean isGameOver() {
        return winnerId != Board.NO_PLAYER_ID;
    }

    /**
     * Gets the ID of the winner.
     * @return The ID of the winner.
     */
    public int getWinnerId() {
        return winnerId;
    }

    /**
     * Performs move by the current player.
     * @param position The target position where to place an electron.
     * @return The move that was performed.
     */
    public DetailedMove performMove(SquarePosition position) {
        if (isGameOver()) {
            return null;
        }
        DetailedMove move = MoveGenerator.generateDetailedMove(currentBoardState, currentPlayerId, position);
        BoardState nextState = MoveGenerator.generateMove(currentBoardState, currentPlayerId, position);
        if (move == null || nextState == null) {
            return null;
        }
        switchToNextState(nextState);
        return move;
    }

    /**
     * Performs move as an AI player.
     * @return The move that was performed.
     */
    public DetailedMove performAIMove() {
        if (isGameOver()) {
            return null;
        }
        BoardState nextState = chooseAIMove();
        DetailedMove move = MoveGenerator.generateDetailedMove(currentBoardState, currentPlayerId, nextState.getTarget());
        if (move == null || nextState == null) {
            return null;
        }
        switchToNextState(nextState);
        return move;
    }

    public void getStatistics() {
        // TODO: return player statistics - how many electrons they have, who is winner
        // TODO: also return copy of the current board
    }

    /**
     * Gets copy of the current board.
     * @return The copy of the current board.
     */
    public Board getCurrentBoardCopy() {
        return currentBoardState.getBoard().copy();
    }

    /**
     * Switches the current state to another one.
     * @param nextBoardState The state to switch to.
     */
    private void switchToNextState(BoardState nextBoardState) {
        currentBoardState = nextBoardState;
        if (nextBoardState.isTerminal()) {
            winnerId = currentPlayerId;
        } else {
            currentPlayerId = getNextPlayerId(currentPlayerId);
        }
        moveCounter++;
    }

    /**
     * Gets ID of the player who is on the move after the given player.
     * @param playerId The ID of a player.
     * @return The ID of the player who is on the move after the given player.
     */
    private int getNextPlayerId(int playerId) {
        return (playerId + 1) % PLAYERS_COUNT;
    }

    /**
     * Checks whether the given player is a maximizing player in the minimax algorithm.
     * @param playerId The ID of a player.
     * @return The indicator whether the player is a maximizing player in the minimax algorithm.
     */
    private boolean isMaximizingPlayer(int playerId) {
        return playerId % 2 == 0;
    }

    /**
     * Chooses a move for AI player using the minimax algorithm.
     * @return
     */
    private BoardState chooseAIMove() {
        List<BoardState> states = MoveGenerator.generateAllMoves(currentBoardState, currentPlayerId);
        List<Integer> evaluations = new ArrayList<>();
        int nextPlayerId = getNextPlayerId(currentPlayerId);
        for (BoardState state : states) {
            int value = minimax(state, MINIMAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, nextPlayerId);
            evaluations.add(value);
        }
        int bestValue;
        if (isMaximizingPlayer(currentPlayerId)) {
            bestValue = Collections.max(evaluations);
        } else {
            bestValue = Collections.min(evaluations);
        }
        int[] bestStateIndices = IntStream.range(0, states.size()).filter(i -> evaluations.get(i) == bestValue).toArray();
        int bestStateIndex = bestStateIndices[random.nextInt(bestStateIndices.length)];
        return states.get(bestStateIndex);
    }

    /**
     * Evaluates a game state using the minimax algorithm.
     * @param state The state to evaluate.
     * @param depth The depth of the recursion.
     * @param alpha The alpha from the alpha-beta pruning algorithm.
     * @param beta The beta from the alpha-beta pruning algorithm.
     * @param playerId The ID of the player on the move.
     * @return The evaluation of the state.
     */
    private int minimax(BoardState state, int depth, int alpha, int beta, int playerId) {
        if (depth == 0 || state.isTerminal()) {
            return evaluateBoardState(state);
        }
        List<BoardState> states = MoveGenerator.generateAllMoves(state, playerId);
        int nextPlayerId = getNextPlayerId(playerId);
        if (isMaximizingPlayer(playerId)) {
            int maxValue = Integer.MIN_VALUE;
            for (BoardState nextState : states) {
                int value = minimax(nextState, depth - 1, alpha, beta, nextPlayerId);
                maxValue = Math.max(maxValue, value);
                if (maxValue >= beta) {
                    break;
                }
                alpha = Math.max(alpha, maxValue);
            }
            return maxValue;
        } else {
            int minValue = Integer.MAX_VALUE;
            for (BoardState nextState : states) {
                int value = minimax(nextState, depth - 1, alpha, beta, nextPlayerId);
                minValue = Math.min(minValue, value);
                if (minValue <= alpha) {
                    break;
                }
                beta = Math.min(beta, minValue);
            }
            return minValue;
        }
    }

    /**
     * Evaluates the given state.
     * @param state The state to evaluate.
     * @return The evaluation of the state.
     */
    private static int evaluateBoardState(BoardState state) {
        int electronsCount1 = state.getElectronsCount(0);
        int electronsCount2 = state.getElectronsCount(1);
        if (electronsCount1 == 0) {
            // First player loses
            return Integer.MIN_VALUE;
        } else if (electronsCount2 == 0) {
            // Second player loses
            return Integer.MAX_VALUE;
        }
        return electronsCount1 - electronsCount2;
    }

    // Moves ordering during minimax:
    // - go through all board changes, count differences (how many electrons were added + how many electrons of enemy player
    //   were removed - actually not, because electrons from enemy player are added to player)
    // When you reach the last depth, go through the whole board and count how many electrons belongs to whom
}
