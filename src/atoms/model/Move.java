package atoms.model;

import java.util.ArrayList;
import java.util.List;

// Maybe separate Move and DetailedMove creation?\
// Move can be created quickly with BoardChanges
// - it does not need to know which explosion belongs to which wave
// - the explosions just need to be in queue

// But Move will then be unusable for conversion to DetailedMove
// - DetailedMove will have to be created from scratch from Position

final class Move {
    public Move(int playerId, SquarePosition origin) {
        this.playerId = playerId;
        this.origin = origin;
        explosionWaves = new ArrayList<>();
        stolenPositions = new ArrayList<>();
    }

    public void addExplosionWave(ExplosionWave explosionWave) {
        explosionWaves.add(explosionWave);
    }

    public void addStolenSquare(int originalPlayerId, SquarePosition position) {
        stolenPositions.add(new StolenPosition(originalPlayerId, position));
    }

    public void apply(Board board) {
        Square originSquare = board.getSquare(origin);
        originSquare.incElectronsCount();
        for (ExplosionWave explosionWave : explosionWaves) {
            for (Explosion explosion : explosionWave) {
                Square explosionSquare = board.getSquare(explosion.origin());
                explosionSquare.decElectronsCount(explosion.targets().size());
                // Neighboring atoms cannot explode in the same wave -> there is no explosion
                // origin among targets, and it will not be zeroed out later
                for (SquarePosition target : explosion.targets()) {
                    Square targetSquare = board.getSquare(target);
                    targetSquare.incElectronsCount();
                    targetSquare.setPlayerId(playerId);
                }
            }
        }
    }

    public void undo(Board board) {
        for (int i = explosionWaves.size() - 1; i >= 0; i--) {
            ExplosionWave explosionWave = explosionWaves.get(i);
            for (Explosion explosion : explosionWave) {
                for (SquarePosition target : explosion.targets()) {
                    Square targetSquare = board.getSquare(target);
                    targetSquare.decElectronsCount();
                }
                Square explosionSquare = board.getSquare(explosion.origin());
                explosionSquare.incElectronsCount(explosion.targets().size() - 1);
            }
        }
        Square originSquare = board.getSquare(origin);
        originSquare.decElectronsCount();
        for (StolenPosition stolenPosition : stolenPositions) {
            Square stolenSquare = board.getSquare(stolenPosition.position());
            stolenSquare.setPlayerId(stolenPosition.originalPlayerId());
        }
    }

    public SquarePosition getOrigin() {
        return origin;
    }

    public List<ExplosionWave> getExplosionWaves() {
        return explosionWaves;
    }

    private record StolenPosition(int originalPlayerId, SquarePosition position) {}

    private final int playerId;
    private final SquarePosition origin;
    private final List<ExplosionWave> explosionWaves;
    private final List<StolenPosition> stolenPositions;
}
