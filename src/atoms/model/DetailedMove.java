package atoms.model;

import java.util.List;

// Phase1: List<SquarePosition> explosionPositions, List<SquarePosition> targets, Board boardAfter
// Phase2: List<Explosion> explosions, Board boardAfter

public record DetailedMove(int playerId, List<DetailedMovePhase> phases) {}
