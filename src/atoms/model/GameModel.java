package atoms.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

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

public final class GameModel {
    public static final int PLAYERS_COUNT = 2;

    public GameModel(int boardSize) {
        boardState = new BoardState(boardSize, PLAYERS_COUNT);
        this.playersCount = playersCount;
        currentPlayerId = 0;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public DetailedMove performMove(int playerId, SquarePosition position) throws Exception {
        DetailedMove move = MoveGenerator.createDetailedMove(boardState, playerId, position);
        boardState = MoveGenerator.createBoardState(boardState, playerId, position);
        return move;
    }

    public DetailedMove performAIMove(int playerId) {
        // TODO: compute Position, return detailed move

        return null;
    }

    public void getState() {
        // TODO: return game state - Running, Over, Winner
        // OR pass onGameOver action to constructor
    }

    private final int playersCount;
    private BoardState boardState;
    private int currentPlayerId;

    private void switchToNextPlayer() {
        currentPlayerId = getNextPlayerId(currentPlayerId);
    }

    private int getNextPlayerId(int playerId) {
        return (playerId + 1) % playersCount;
    }

    private int minimax(BoardState state, int depth, int alpha, int beta, int playerId, boolean maximizing) {
        int sign = maximizing ? 1 : -1;
        if (depth == 0 || boardStateIsTerminal(state)) {
            return sign * evaluateBoardState(state);
        }
        List<BoardState> states = MoveGenerator.generateBoardStates(state, playerId);
        int nextPlayerId = getNextPlayerId(playerId);
        if (maximizing) {
            int maxValue = Integer.MIN_VALUE;
            for (BoardState nextState : states) {
                int value = minimax(nextState, depth - 1, alpha, beta, nextPlayerId, false);
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
                int value = minimax(nextState, depth - 1, alpha, beta, nextPlayerId, true);
                minValue = Math.min(minValue, value);
                if (minValue <= alpha) {
                    break;
                }
                beta = Math.min(beta, minValue);
            }
            return minValue;
        }
    }

    private static boolean boardStateIsTerminal(BoardState state) {
        return false;
    }

    private static int evaluateBoardState(BoardState state) {
        return 0;
    }

    // Moves ordering during minimax:
    // - go through all board changes, count differences (how many electrons were added + how many electrons of enemy player
    //   were removed - actually not, because electrons from enemy player are added to player)
    // When you reach the last depth, go through the whole board and count how many electrons belongs to whom

    // Can I really determine explosions from board changes alone? No, I cannot - there can be multiple explosions in 1 phase
    // I need to store additional information - positions of explosions?

    // Move must be easy to generate, but also convertible to DetailedMove
    // Make algorithms to generate Move and convert it to DetailedMove, and we will see
    // In Move I do not need to have explosion origins and targets, I just need to have changes

    // Move: SquarePosition origin, List<MovePhase> phases
    // MovePhase: List<Explosion>
    // Explosion: SquarePosition origin, List<SquarePosition> targets
    // StolenSquares: List<(int playerId, List<SquarePosition>)> - add stolen targets to the list

    // We have to track number of enemy atoms to stop the game when endless loop happens

    // How to reverse Move:
    // Iterate through phases in reverse
    // Add 3 cells to explosion origin, remove 1 cell from targets
    // Remove 1 cell from move origin
    // Iterate through stolen squares, restore their original owner

    // Explosion Position -> remove all electrons from explosion position
    // Explosion TargetPositions (can be determined from explosion position) -> increase their electrons by one
    // If I just save new squares, then I will not be able to increase electrons more than by one in one wave
    // How to reverse move?
    // I cannot determine if square gained after explosion was owned by another player

    // We have to track number of

    // Minimax function will return pair (int evaluation, Move move) -> move that was evaluated this way
}
