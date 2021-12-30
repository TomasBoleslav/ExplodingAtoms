package atoms.model;

import java.util.List;

public record Explosion (SquarePosition origin, List<SquarePosition> targets) {}
