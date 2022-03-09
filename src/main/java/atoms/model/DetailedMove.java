package atoms.model;

import java.util.List;

/**
 * Move that is divided into phases of explosions that happen at the same time.
 * @param playerId The ID of the player the move belongs to.
 * @param phases The phases of explosions.
 */
public record DetailedMove(int playerId, List<DetailedMovePhase> phases) {}
