package atoms.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

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
    private static final int MINIMAX_DEPTH = 2;

    public GameModel(int boardSize) {
        currentBoardState = new BoardState(boardSize, PLAYERS_COUNT);
        currentPlayerId = 0;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public DetailedMove performMove(SquarePosition position) {
        DetailedMove move = MoveGenerator.createDetailedMove(currentBoardState, currentPlayerId, position);
        currentBoardState = MoveGenerator.createBoardState(currentBoardState, currentPlayerId, position);
        switchToNextPlayer();
        return move;
    }

    public DetailedMove performAIMove() {
        BoardState state = chooseNextBoardStateForAI();
        DetailedMove move = MoveGenerator.createDetailedMove(currentBoardState, currentPlayerId, state.getTarget());
        currentBoardState = state;
        switchToNextPlayer();
        return move;
    }

    public void getStatistics() {
        // TODO: return player statistics - how many electrons they have, who is winner
    }

    private BoardState currentBoardState;
    private int currentPlayerId;

    private void switchToNextPlayer() {
        currentPlayerId = getNextPlayerId(currentPlayerId);
    }

    private int getNextPlayerId(int playerId) {
        return (playerId + 1) % PLAYERS_COUNT;
    }

    private boolean isMaximizingPlayer(int playerId) {
        return playerId % 2 == 0;
    }

    private BoardState chooseNextBoardStateForAI() {
        List<BoardState> states = MoveGenerator.generateBoardStates(currentBoardState, currentPlayerId);
        List<Integer> evaluations = new ArrayList<>();
        int nextPlayerId = getNextPlayerId(currentPlayerId);
        for (BoardState state : states) {
            int value = minimax(state, MINIMAX_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, nextPlayerId);
            evaluations.add(value);
        }
        int searchedValue;
        if (isMaximizingPlayer(currentPlayerId)) {
            searchedValue = Collections.max(evaluations);
        } else {
            searchedValue = Collections.min(evaluations);
        }
        int searchedIndex = evaluations.indexOf(searchedValue);
        return states.get(searchedIndex);
    }

    private int minimax(BoardState state, int depth, int alpha, int beta, int playerId) {
        if (depth == 0 || state.isTerminal()) {
            return evaluateBoardState(state);
        }
        List<BoardState> states = MoveGenerator.generateBoardStates(state, playerId);
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
