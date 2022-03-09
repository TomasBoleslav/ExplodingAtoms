package atoms.model;

import java.util.List;

/**
 * Phase of a detailed move with explosions happening at the same time.
 * @param explosions Positions of explosions.
 * @param targets Positions of explosion targets.
 * @param boardAfter The board after the explosions happen.
 */
public record DetailedMovePhase(
        List<SquarePosition> explosions,
        List<SquarePosition> targets,
        Board boardAfter) {
}
