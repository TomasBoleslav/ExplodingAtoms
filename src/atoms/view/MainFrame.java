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

// TODO BUG: game does not wait between Human player move and AI player move

import atoms.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

public final class MainFrame extends JFrame {
    public MainFrame() {
        setTitle("Exploding Atoms");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        isAIPlayer = new boolean[2];
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
        JPanel player1Settings = createPlayerSettingsPanel(0);
        JPanel player2Settings = createPlayerSettingsPanel(1);
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

    private JPanel createPlayerSettingsPanel(int playerId) {
        JPanel playerSettings = new JPanel(new FlowLayout());
        JLabel label = new JLabel("Player " + (playerId + 1) + ":");
        label.setFont(fontNormal);
        JComboBox<String> playerTypeComboBox = new JComboBox<>(new String[] {HUMAN_PLAYER, AI_PLAYER});
        playerTypeComboBox.addItemListener(e -> {
            String playerType = (String)playerTypeComboBox.getSelectedItem();
            if (AI_PLAYER.equals(playerType)) {
                isAIPlayer[playerId] = true;
            } else {
                isAIPlayer[playerId] = false;
            }
        });
        playerTypeComboBox.setFont(fontNormal);
        playerSettings.add(label);
        playerSettings.add(playerTypeComboBox);
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
        boardPanel = new BoardPanel();
        boardPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isAIPlayer[gameModel.getCurrentPlayerId()]) {
                    // TODO: isAIPlayer does not work, because
                    return;
                }
                SquarePosition target = boardPanel.getSquarePositionFromPoint(e.getX(), e.getY());
                if (target == null) {
                    return;
                }
                moveAsHumanPlayer(target);
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
        return boardPanel;
    }

    private void updateGameStatus() {
        if (gameModel.isGameOver()) {
            int winnerNumber = playerNumberFromId(gameModel.getWinnerId());
            currentPlayerLabel.setText("Player " + winnerNumber + " has won!");
        } else {
            int currentPlayerNumber = playerNumberFromId(gameModel.getCurrentPlayerId());
            currentPlayerLabel.setText("Player " + currentPlayerNumber + " on the move");
        }
    }

    private int playerNumberFromId(int playerId) {
        return playerId + 1;
    }

    // TODO: problem - application is frozen when displaying move
    // Solution: display phases separately, always use invokeLater
    // Complications:
    // - requires use of boolean control variable isDisplayingMove, which will prevent click events on board
    // - problem if player changes scenes fast - if move is still being displayed
    //   - old invokes must be detected (using counter variable as gameModel ID?) - inside invokes - and thrown away
    //     if (gameModelId == myMovePhaseId) { performMovePhase; invokeLater(this function); }
    // Generalization - RunnableSequence - sequence of jobs to run after each other in sequence with given delay
    // - internally - create list of lambdas (or Runnable-s), each lambda will perform a job and call next lambda with
    //   invokeLater
    // OR JUST YIELD THE THREAD - will it work? does not work
    // - maybe make a while cycle where sleeping will be done with while loop and yielding until time is up

    // Create ActionListener-s in a loop (store them in list)
    // Loop through ActionListener-s, create timers - put there another action listener that starts the next timer
    // new Timer(delay, actionListener for drawing, actionListener for starting nextTimer)
    // maybe iterate in reverse through phases and create Timer-s from last to first, so that they are known?


    private void animateMove(DetailedMove move, boolean markFirstPhase) {
        final int DELAY_BETWEEN_MOVES = 300;
        for (int i = 0; i < move.phases().size(); i++) {
            DetailedMovePhase phase = move.phases().get(i);
            if (i == 0) {
                if (markFirstPhase) {
                    drawExplosionsAndTargets(phase.explosions(), phase.targets());
                    sleep(DELAY_BETWEEN_MOVES);
                    drawBoard(phase.boardAfter());
                } else {
                    drawBoard(phase.boardAfter());
                }
            } else {
                sleep(DELAY_BETWEEN_MOVES);
                drawExplosionsAndTargets(phase.explosions(), phase.targets());
                sleep(DELAY_BETWEEN_MOVES);
                drawBoard(phase.boardAfter());
            }
        }
    }

    private void drawExplosionsAndTargets(List<SquarePosition> explosions, List<SquarePosition> targets) {
        boardPanel.setExplosions(explosions);
        boardPanel.setTargets(targets);
        boardPanel.paintImmediately(0, 0, boardPanel.getWidth(), boardPanel.getHeight());
    }

    private void drawBoard(Board board) {
        boardPanel.setExplosions(null);
        boardPanel.setTargets(null);
        boardPanel.setBoard(board);
        boardPanel.paintImmediately(0, 0, boardPanel.getWidth(), boardPanel.getHeight());
    }

    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
        }
    }

    private JPanel createGameControlPanel() {
        JPanel gameControlPanel = new JPanel(new GridLayout(2, 1));
        gameControlPanel.setBorder(border);

        JPanel statusPanel = new JPanel(new GridLayout(3, 1));
        JLabel statusHeading = new JLabel("Status");
        statusHeading.setFont(fontHeading);
        currentPlayerLabel = new JLabel("Player 1 on the move");
        winnerLabel = new JLabel("Player 1 has won!");
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
        gameModel = new GameModel();
        Board board = gameModel.getCurrentBoardCopy();
        drawBoard(board);
        updateGameStatus();
        cardLayout.show(contentPanel, GAME_PANEL_NAME);
        if (isCurrentPlayerAI()) {
            SwingUtilities.invokeLater(this::moveAsAIPlayer);
        }
    }

    private void moveAsHumanPlayer(SquarePosition target) {
        DetailedMove move = gameModel.performMove(target);
        performMove(move, false);
    }

    private void moveAsAIPlayer() {
        DetailedMove move = gameModel.performAIMove();
        sleep(300);
        performMove(move, true);
    }

    private void performMove(DetailedMove move, boolean markFirstPhase) {
        animateMove(move, markFirstPhase);
        updateGameStatus();
        if (isCurrentPlayerAI()) {
            SwingUtilities.invokeLater(this::moveAsAIPlayer);
        }
    }

    private boolean isCurrentPlayerAI() {
        return isAIPlayer[gameModel.getCurrentPlayerId()];
    }

    public void quitGame() {
        cardLayout.show(contentPanel, MENU_PANEL_NAME);
    }

    private static final String MENU_PANEL_NAME = "menuPanel";
    private static final String GAME_PANEL_NAME = "gamePanel";
    private static final String HUMAN_PLAYER = "Human";
    private static final String AI_PLAYER = "Computer";
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
    private GameModel gameModel;
    private boolean[] isAIPlayer;

    // TODO: if immediate repainting is a problem, how to paint steps?
}
