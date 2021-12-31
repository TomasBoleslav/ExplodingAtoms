package atoms.model;

import java.util.List;

public record DetailedMove(int playerId, List<DetailedMovePhase> phases) {}
