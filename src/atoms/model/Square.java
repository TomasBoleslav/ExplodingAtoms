package atoms.model;

/**
 * Square on the game board.
 * @param playerId The ID of the player that owns the square.
 * @param electronsCount The number of electrons on the square.
 */
public record Square(int playerId, int electronsCount) {}
