package atoms.model;

import java.util.List;

// forbid move that starts 2 or more squares away from allied or enemy squares
// - it may cause speed up in the beginning

// Don't have difficulties - just have 1 difficulty computing to the maximum possible depth

// Consider not separating move into explosions
// - but animation with multiple explosions would be faster if they happen in waves
// - yeah, separate move into explosions for faster animations (what if e.g. 10 explosions happen? move would take a lot of seconds)
// How should animations look like:
// - explosion origin is red
// - explosion targets are yellow
// - squares can have up to 7 circles

public class BoardState {
    private final SquarePosition origin;
    private final Board board;
    private final int[] playerSquareCounts;
    private final int[] playerElectronCounts;
}
