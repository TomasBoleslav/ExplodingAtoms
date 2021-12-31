package atoms.view;

// How to represent board.
// Board - size (width, height), 2D array of squares
// Square - number of atoms, playerID

// How to represent player - Human, AI
// - id (more than 2 players possible)
// HumanPlayer - nothing else
// AIPlayer - int (enum) difficulty

// Player class may not be required - just an array of IDs
// - statistics can be gathered from board (scanning squares with corresponding ID)
// - list of players managed by Controller or Model?

// model = new Model();
// model.newGame()

// List of players managed by controller:
// - Model.performHumanMove(int playerID, Position position) -> VerboseMove (ModelMove is internal)
// - Model.performAIMove(int playerID) -> VerboseMove
// -

// List of players managed by Model:
// - Model.getCurrentPlayer() -> Player
// - Model.setNextHumanMove(Position position)
// - Model.getMove()

// How to represent a move
// ModelMove - just outcome (not stages)
// - must contain enough information to reverse the move
// generateMoves(Board) -> List<ModelMove>
// ModelMove: Board outcomeBoard, // all squares of outcome board would have to be allocated
// or
// ModelMove: List<Change>, where Change=List<(Position position, Square before, Square after)>
//

// VerboseMove - separated into stages, how should a stage be represented?
// MoveStage/MovePhase - initialState
// - View must be as simple as possible - just shows stage
// - moves are sorted in queue in groups of moves (explosions) that happen at the same time
// - Explosion: Position from, Position[] targets (positions of electrons, can be used for animations)
// - SingleMove/MovePhase: List<Explosion>, Board boardAfter // Visually: All explosion positions will have red background
//   and 4 electrons, then the board will be reset with boardAfter, then in the next phase other positions will become red, ...
//   or you can also mark target positions with e.g. yellow color
// - VerboseMove: int playerID, List<SingleMove>

// DETECT DRAW - endless loop of explosions


import javax.swing.*;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("Exploding Atoms");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel("Hello World");
        getContentPane().add(label);
        pack();
        setVisible(true);
    }
}
