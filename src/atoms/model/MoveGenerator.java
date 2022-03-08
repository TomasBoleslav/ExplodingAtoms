package atoms.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

// TODO: make it simpler
// MoveGenerator
// - generateMove(state, playerID) -> BoardState
// - generateMoves(state, playerID, target) -> List<BoardState>
// - generateDetailedMove() -> DetailedMove without phases, single explosions
//
// DetailedMove = SquarePosition target, List<Explosions> explosions
// Explosion = SquarePosition origin, List<SquarePosition> targets
//


public class MoveGenerator {
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

    public static BoardState generateMove(BoardState state, int playerId, SquarePosition target) {
        Square targetSquare = state.getBoard().getSquare(target);
        if (!playerCanTargetSquare(playerId, targetSquare)) {
            return null;
        }
        boolean allPlayersMoved = state.allPlayersMoved();
        return createNextBoardState(state, target, playerId, allPlayersMoved);
    }

    public static DetailedMove generateDetailedMove(BoardState state, int playerId, SquarePosition target) {
        Square targetSquare = state.getBoard().getSquare(target);
        if (!playerCanTargetSquare(playerId, targetSquare)) {
            return null;
        }
        List<DetailedMovePhase> phases = new ArrayList<>();
        int[] playerElectronCounts = state.getAllElectronCounts().clone();
        playerElectronCounts[playerId]++;
        boolean allPlayersMoved = state.allPlayersMoved();
        Board board = state.getBoard().copy();
        phases.add(performFirstPhase(board, playerId, target));
        List<SquarePosition> explosions = findExplosions(board);
        while (explosions.size() > 0 && !playerStoleAllElectrons(playerId, playerElectronCounts, allPlayersMoved)) {
            DetailedMovePhase phase = performPhase(board, playerId, playerElectronCounts, explosions);
            phases.add(phase);
            explosions = findExplosions(board);
        }
        return new DetailedMove(playerId, phases);
    }

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

    private static DetailedMovePhase performFirstPhase(Board board, int playerId, SquarePosition target) {
        List<SquarePosition> explosions = new ArrayList<>();
        List<SquarePosition> targets = new ArrayList<>();
        targets.add(target);
        Square targetSquare = board.getSquare(target);
        int newElectronsCount = targetSquare.electronsCount() + 1;
        board.setSquare(target, new Square(playerId, newElectronsCount));
        return new DetailedMovePhase(explosions, targets, board.copy());
    }

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
        return new DetailedMovePhase(explosions, targets, board.copy());
    }

    private static boolean playerCanTargetSquare(int playerId, Square targetSquare) {
        int squarePlayerId = targetSquare.playerId();
        return squarePlayerId == Board.NO_PLAYER_ID || squarePlayerId == playerId;
    }

    private static List<SquarePosition> generateAllMoveTargets(Board board, int playerId) {
        List<SquarePosition> origins = new ArrayList<>();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                Square square = board.getSquare(i, j);
                // TODO: add position only if there is an owned square in close proximity (e.g. max 2 squares away)
                if (playerCanTargetSquare(playerId, square)) {
                    origins.add(new SquarePosition(i, j));
                }
            }
        }
        return origins;
    }

    private static BoardState createNextBoardState(
            BoardState state,
            SquarePosition target,
            int playerId,
            boolean allPlayersMoved) {
        int[] playerElectronCounts = state.getAllElectronCounts().clone();
        playerElectronCounts[playerId]++; // TODO: What if this is raised again in while cycle
        Board board = state.getBoard().copy();
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
