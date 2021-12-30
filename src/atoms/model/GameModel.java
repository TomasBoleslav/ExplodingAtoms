package atoms.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// Mathematical proofs:
// 1. If there is endless loop of explosions, the whole board must belong to the player who caused it.
// Proof:
//   If there is endless loop of explosions, there must be a square A that is repeated (number of squares is finite).
//   The neighbors of A will thus have an endless supply of electrons -> they too belong to the cycle.
//   By induction all squares belong to the endless cycle
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

    public GameModel(int boardSize, int playersCount) {
        board = new Board(boardSize);
    }

    private int currentPlayerId;

    // TODO: remember ID of current player,
    public int getCurrentPlayerId() {
        return currentPlayerId;
    }

    public DetailedMove performMove(int playerId, SquarePosition position) throws Exception {
        Square square = board.getSquare(position);
        if (square.playerId() != board.NO_PLAYER_ID && square.playerId() != playerId) {
            throw new Exception("Cannot add an electron to a square that belongs to another player");
        }


        Queue<List<SquarePosition>> explosionOrigins;
        Square square = board.getSquare(position);
        int newElectronsCount = square.electronsCount() + 1;
        Square newSquare;
        int maxElectronsCount = getMaxElectronsCount(position);
        if (newElectronsCount < maxElectronsCount) { // No, depends on the position!
            newSquare = new Square(playerId, newElectronsCount);
        } else {
            newSquare = new Square(playerId, 1);
        }
        Square newSquare = new Square(playerId, newElectronsCount);
        return null;
    }

    public DetailedMove performAIMove(int playerId) {
        // TODO: return verbose move
        return null;
    }

    public void getState() {
        // TODO: return game state - Running, Over, Winner
        // OR pass onGameOver action to constructor
    }

    private final Board board;

    private int getMaxElectronsCount(SquarePosition position) {
        int maxElectrons = 4;
        int lastIndex = board.getSize() - 1;
        if (position.row() == 0 || position.row() == lastIndex) {
            maxElectrons--;
        }
        if (position.column() == 0 || position.column() == lastIndex) {
            maxElectrons--;
        }
    }

    private List<SquarePosition> getExplosionTargets(SquarePosition position) {
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

    private Move2 createMove2(Board board, SquarePosition origin, int playerId) {

    }

    private ExplosionWave createMove(Board board, SquarePosition origin, int playerId) {
        // We have to track number of owned squares !!! - when it reaches 64, the player wins

        // Proof with endless loop does not work - it would work if 1 electron stayed after explosion
        //
        List<List<SquarePosition>> waves = new ArrayList<>();
        List<SquarePosition> currentWave = new ArrayList<>();
        currentWave.add(origin);
        while (currentWave.size() > 0) {
            List<SquarePosition> nextWave = new ArrayList<>();
            ExplosionWave explosionWave = new ExplosionWave();
            for (SquarePosition position : currentWave) {

            }

            currentWave = nextWave;
        }

        Queue<SquarePosition> wavePositions = new LinkedList<>();
        positions.add(origin);
        while
        while (!positions.isEmpty()) {
            SquarePosition currentPosition = positions.remove();

        }

        Move move = new Move(playerId, position);
        Square originSquare = board.getSquare(position);
        originSquare.incElectronsCount();
        List<SquarePosition> explosionTargets = getExplosionTargets(position);
        int maxElectrons = explosionTargets.size() - 1;
        if (originSquare.getElectronsCount() > maxElectrons) {
            List<ExplosionWave> explosionWaves = new ArrayList<>();
        }
    }

    private static DetailedMove convertMoveToDetailedMove() {
        // can this be done? not simply
        // but I need this when performing AI move - using SimpleMove internally, but then converting to Move for View
        // it would be better to have phases - groups of changes that were made at the same time
        // they can be easily added in minimax
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
