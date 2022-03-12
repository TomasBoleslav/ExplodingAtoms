/**
 * Contains the main logic of the game and related data structures.
 * 
 * A board is a two-dimensional array of squares, where each square can be
 * owned by a player and contain electrons.
 * 
 * A state of the game is represented by BoardState. These states also represent
 * possible moves of a player.
 * In order for the View to have more possibilities when displaying moves,
 * a DetailedMove is returned instead of a BoardState. It separates the move
 * into phases of explosions that happen at the same time.
 * 
 * The algorithm used to choose a move for an A. I. player is minimax with
 * alpha-beta pruning. The value of a move is the difference between the number
 * of electrons of both players. If there are more moves  with the same value,
 * one is chosen randomly.
 */
package atoms.model;