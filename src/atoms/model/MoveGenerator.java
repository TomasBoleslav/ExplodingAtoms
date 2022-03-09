package atoms.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Generator for moves.
 */
public class MoveGenerator {

    /**
     * Generates all moves for a player.
     * @param state The initial state.
     * @param playerId The ID of the player making the move.
     * @return The list of all possible moves.
     */
    public static List<BoardState> generateAllMoves(BoardState state, int playerId) {
        List<BoardState> states = new ArrayList<>();
        boolean allPlayersMoved = state.allPlayersMoved();
        List<SquarePosition> targets = generateAllMoveTargets(state.getBoard(), playerId);
        for (SquarePosition target : targets) {
            BoardState newState = createNextBoardState(state, target, playerId, allPlayersMoved);
            states.add(newState);
        }
        return states;
    }

    /**
     * Generates a move of a player.
     * @param state The initial state.
     * @param playerId The ID of the player making the move.
     * @param target The chosen square.
     * @return The generated move.
     */
    public static BoardState generateMove(BoardState state, int playerId, SquarePosition target) {
        Square targetSquare = state.getBoard().getSquare(target);
        if (!canPlayerTargetSquare(playerId, targetSquare)) {
            return null;
        }
        boolean allPlayersMoved = state.allPlayersMoved();
        return createNextBoardState(state, target, playerId, allPlayersMoved);
    }

    /**
     * Generates a detailed move of a player.
     * @param state The initial state.
     * @param playerId The ID of the player making the move.
     * @param target The chosen square.
     * @return The generated detailed move.
     */
    public static DetailedMove generateDetailedMove(BoardState state, int playerId, SquarePosition target) {
        Square targetSquare = state.getBoard().getSquare(target);
        if (!canPlayerTargetSquare(playerId, targetSquare)) {
            return null;
        }
        List<DetailedMovePhase> phases = new ArrayList<>();
        int[] playerElectronCounts = state.getAllElectronCounts().clone();
        playerElectronCounts[playerId]++;
        boolean allPlayersMoved = state.allPlayersMoved();
        Board board = state.getBoard().deepCopy();
        phases.add(performFirstPhase(board, playerId, target));
        List<SquarePosition> explosions = findExplosions(board);
        while (explosions.size() > 0 && !playerStoleAllElectrons(playerId, playerElectronCounts, allPlayersMoved)) {
            DetailedMovePhase phase = performPhase(board, playerId, playerElectronCounts, explosions);
            phases.add(phase);
            explosions = findExplosions(board);
        }
        return new DetailedMove(playerId, phases);
    }

    /**
     * Finds all explosions on the board.
     * @param board The board.
     * @return A list of all found explosions.
     */
    private static List<SquarePosition> findExplosions(Board board) {
        List<SquarePosition> explosions = new ArrayList<>();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                SquarePosition position = new SquarePosition(i, j);
                Square square = board.getSquare(position);
                List<SquarePosition> explosionTargets = getExplosionTargets(board, position);
                if (square.electronsCount() >= explosionTargets.size()) {
                    explosions.add(position);
                }
            }
        }
        return explosions;
    }

    /**
     * Perform the first phase of a detailed move.
     * @param board The board.
     * @param playerId The ID of the player making the move.
     * @param target The chosen square.
     * @return The first phase of a detailed move.
     */
    private static DetailedMovePhase performFirstPhase(Board board, int playerId, SquarePosition target) {
        List<SquarePosition> explosions = new ArrayList<>();
        List<SquarePosition> targets = new ArrayList<>();
        targets.add(target);
        Square targetSquare = board.getSquare(target);
        int newElectronsCount = targetSquare.electronsCount() + 1;
        board.setSquare(target, new Square(playerId, newElectronsCount));
        return new DetailedMovePhase(explosions, targets, board.deepCopy());
    }

    /**
     * Perform a phase of a detailed move.
     * @param board The board.
     * @param playerId The ID of the player making the move.
     * @param playerElectronCounts The electron counts of players.
     * @param explosions The list where new explosions will be added.
     * @return The phase of the detailed move.
     */
    private static DetailedMovePhase performPhase(
            Board board,
            int playerId,
            int[] playerElectronCounts,
            List<SquarePosition> explosions) {
        List<SquarePosition> targets = new ArrayList<>();
        for (SquarePosition explosion : explosions) {
            List<SquarePosition> explosionTargets = getExplosionTargets(board, explosion);
            targets.addAll(explosionTargets);
            for (SquarePosition explosionTarget : explosionTargets) {
                Square square = board.getSquare(explosionTarget);
                int oldPlayerId = square.playerId();
                int newPlayerId = playerId;
                int oldElectronsCount = square.electronsCount();
                int newElectronsCount = oldElectronsCount + 1;
                if (oldPlayerId != playerId) {
                    if (oldPlayerId != Board.NO_PLAYER_ID) {
                        playerElectronCounts[oldPlayerId] -= oldElectronsCount;
                    }
                    playerElectronCounts[playerId] += oldElectronsCount;
                }
                board.setSquare(explosionTarget, new Square(newPlayerId, newElectronsCount));
            }
            Square explosionSquare = board.getSquare(explosion);
            int minElectronsCountForExplosion = explosionTargets.size();
            int electronsCountAfterExplosion = explosionSquare.electronsCount() - minElectronsCountForExplosion;
            board.setSquare(explosion, new Square(playerId, electronsCountAfterExplosion));
        }
        return new DetailedMovePhase(explosions, targets, board.deepCopy());
    }

    /**
     * Checks whether a player can add an electron to the given square.
     * @param playerId The ID of the player.
     * @param targetSquare The targeted square.
     * @return True if the player can add an electron to the square, otherwise false.
     */
    private static boolean canPlayerTargetSquare(int playerId, Square targetSquare) {
        int squarePlayerId = targetSquare.playerId();
        return squarePlayerId == Board.NO_PLAYER_ID || squarePlayerId == playerId;
    }

    /**
     * Generates all possible squares where a player can add an electron.
     * @param board The board.
     * @param playerId The ID of the player.
     * @return A list of all positions where the player can add an elcetron.
     */
    private static List<SquarePosition> generateAllMoveTargets(Board board, int playerId) {
        List<SquarePosition> origins = new ArrayList<>();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                Square square = board.getSquare(i, j);
                if (canPlayerTargetSquare(playerId, square)) {
                    origins.add(new SquarePosition(i, j));
                }
            }
        }
        return origins;
    }

    /**
     * Creates the next board state
     * @param state
     * @param target
     * @param playerId
     * @param allPlayersMoved
     * @return
     */
    private static BoardState createNextBoardState(
            BoardState state,
            SquarePosition target,
            int playerId,
            boolean allPlayersMoved) {
        int[] playerElectronCounts = state.getAllElectronCounts().clone();
        playerElectronCounts[playerId]++;
        Board board = state.getBoard().deepCopy();
        Queue<SquarePosition> targets = new LinkedList<>();
        targets.add(target);
        while (!targets.isEmpty() && !playerStoleAllElectrons(playerId, playerElectronCounts, allPlayersMoved)) {
            SquarePosition currentTarget = targets.remove();
            Square currentTargetSquare = board.getSquare(currentTarget);
            List<SquarePosition> explosionTargets = getExplosionTargets(board, currentTarget);
            int minElectronsCountForExplosion = explosionTargets.size();
            int oldElectronsCount = currentTargetSquare.electronsCount();
            int newElectronsCount = oldElectronsCount + 1;
            if (newElectronsCount >= minElectronsCountForExplosion) {
                newElectronsCount -= minElectronsCountForExplosion;
                targets.addAll(explosionTargets);
            }
            int oldPlayerId = currentTargetSquare.playerId();
            if (oldPlayerId != playerId) {
                if (oldPlayerId != Board.NO_PLAYER_ID) {
                    playerElectronCounts[oldPlayerId] -= oldElectronsCount;
                }
                playerElectronCounts[playerId] += oldElectronsCount;
            }
            int newPlayerId;
            if (newElectronsCount == 0) {
                newPlayerId = Board.NO_PLAYER_ID;
            } else {
                newPlayerId = playerId;
            }
            board.setSquare(currentTarget, new Square(newPlayerId, newElectronsCount));
        }
        return new BoardState(board, target, playerElectronCounts);
    }

    /**
     * Checks if a player stole all electrons from other players.
     * @param playerId The ID of the player.
     * @param playerElectronCounts The electron counts of all players.
     * @param allPlayersMoved An indicator whether all players have made a move.
     * @return True if the player stole all electrons from other players, otherwise false.
     */
    private static boolean playerStoleAllElectrons(int playerId, int[] playerElectronCounts, boolean allPlayersMoved) {
        if (!allPlayersMoved) {
            return false;
        }
        for (int id = 0; id < playerElectronCounts.length; id++) {
            if (id != playerId && playerElectronCounts[id] > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets target positions of an explosion.
     * @param board The board.
     * @param position The position of the explosion.
     * @return The target positions of an explosion.
     */
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
