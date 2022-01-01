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


import atoms.model.Board;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

// TODO: card layout
public final class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Exploding Atoms");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        menuPanel = createMenuPanel();
        gamePanel = createGamePanel();
        contentPanel.add(menuPanel, MENU_PANEL_NAME);
        contentPanel.add(gamePanel, GAME_PANEL_NAME);
        setContentPane(contentPanel);
        pack();
        setVisible(true);
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel(new GridLayout(3, 1));
        menuPanel.setBorder(border);

        JLabel heading = new JLabel("Exploding Atoms");
        heading.setFont(fontHeading);
        heading.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel settingsPanel = new JPanel(new GridLayout(3, 1));
        JPanel player1Settings = createPlayerSettingsPanel(1);
        JPanel player2Settings = createPlayerSettingsPanel(2);
        settingsPanel.add(player1Settings);
        settingsPanel.add(player2Settings);

        JPanel buttonsPanel = new JPanel();
        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> playGame());
        playButton.setFont(fontNormal);
        buttonsPanel.add(playButton);

        menuPanel.add(heading);
        menuPanel.add(settingsPanel);
        menuPanel.setPreferredSize(preferredFrameSize);
        menuPanel.add(buttonsPanel);
        return menuPanel;
    }

    private JPanel createPlayerSettingsPanel(int playerNumber) {
        JPanel playerSettings = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Player " + playerNumber + ":");
        JComboBox<String> comboBox = new JComboBox<>(new String[] {"Human", "Computer"});
        label.setFont(fontNormal);
        comboBox.setFont(fontNormal);
        playerSettings.add(label);
        playerSettings.add(comboBox);
        return playerSettings;
    }

    private JPanel createGamePanel() {
        //JPanel gamePanel = new JPanel(new GridLayout(1, 2));
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBorder(border);

        gamePanel.add(createBoardPanel(), BorderLayout.CENTER);
        gamePanel.add(createGameControlPanel(), BorderLayout.LINE_END);
        /*
        gamePanel.add(createBoardPanel());
        gamePanel.add(createStatisticsPanel());
*/
        gamePanel.setPreferredSize(preferredFrameSize);
        return gamePanel;
    }

    private JPanel createBoardPanel() {
        boardPanel = new BoardPanel(new Board(8));
        boardPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Timer timer = new Timer(3000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {
                        boardPanel.setBoard(new Board(8));
                        boardPanel.repaint();
                    }
                });
                timer.setRepeats(false); // Only execute once
                timer.start(); // Go go go!
                /*
                boardPanel.setBoard(new Board(8));
                boardPanel.repaint();*/
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
        //boardPanel.add(new Label("Board"));
        return boardPanel;
    }

    private JPanel createGameControlPanel() {
        JPanel gameControlPanel = new JPanel(new GridLayout(2, 1));
        gameControlPanel.setBorder(border);

        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        JLabel statusHeading = new JLabel("Status");
        statusHeading.setFont(fontHeading);
        currentPlayerLabel = new JLabel("Player 1 on the move");
        winnerLabel = new JLabel("Player 1 wins!");
        winnerLabel.setVisible(false);
        statusPanel.add(statusHeading);
        statusPanel.add(currentPlayerLabel);
        statusPanel.add(winnerLabel);

        JPanel buttonsPanel = new JPanel();
        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(e -> quitGame());
        buttonsPanel.add(quitButton);

        gameControlPanel.add(statusPanel);
        gameControlPanel.add(buttonsPanel);
        return gameControlPanel;
    }

    public void playGame() {
        cardLayout.show(contentPanel, GAME_PANEL_NAME);
    }

    public void quitGame() {
        cardLayout.show(contentPanel, MENU_PANEL_NAME);
    }

    private static final String MENU_PANEL_NAME = "menuPanel";
    private static final String GAME_PANEL_NAME = "gamePanel";
    private static final Font fontNormal = new Font("Courier New", Font.PLAIN, 14);
    private static final Font fontHeading = new Font("Courier New", Font.BOLD, 32);
    private static final Dimension preferredFrameSize = new Dimension(640, 480);
    private static final EmptyBorder border = new EmptyBorder(10, 10, 10, 10);
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private JPanel gamePanel;
    private JLabel currentPlayerLabel;
    private JLabel winnerLabel;
    private BoardPanel boardPanel;

    // TODO: if immediate repainting is a problem, how to paint steps?
}
