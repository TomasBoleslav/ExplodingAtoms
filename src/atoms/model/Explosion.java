package atoms.model;

import java.util.List;

/**
 * Explosion of an atom.
 * @param origin The position of the explosion's origin.
 * @param targets The target positions of electrons.
 */
public record Explosion (SquarePosition origin, List<SquarePosition> targets) {}
