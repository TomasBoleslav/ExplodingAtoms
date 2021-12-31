package atoms.model;

import java.util.List;

// NOTE: explosions and targets are separated so that first move can be expressed as phase (no explosions, 1 target)

public record DetailedMovePhase(
        List<SquarePosition> explosions,
        List<SquarePosition> targets,
        Board boardAfter) {
}
