/**
 * Contains UI components and event handling.
 *
 * This package uses the Java Swing library to provide a user interface.
 * Components are created in the code manually without any configuration files.
 *
 * The game model package is used for the logic of the game and computation.
 *
 * Moves of players are displayed sequentially in phases of explosions that
 * happen at the same time. Currently, the delays between phases are blocking
 * and prevent event handling during the time when a move is being displayed.
 * This can cause the window to freeze if there are too many explosions.
 * A better solution could be using timers to dispatch events that are handled
 * after a fixed time interval. 
 */
package atoms.view;