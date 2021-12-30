package atoms.model;

import java.util.List;

public record DetailedMovePhase(List<Explosion> explosions, Board boardAfter) {}
