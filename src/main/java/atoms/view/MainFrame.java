package atoms.view;

import atoms.model.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

/**
 * Main frame of the application.
 */
public final class MainFrame extends JFrame {

    /**
     * Creates the main frame.
     */
    public MainFrame() {
        setTitle(TITLE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        isAIPlayer = new boolean[GameModel.PLAYERS_COUNT];
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

    private static final String TITLE = "Exploding Atoms";
    private static final String MENU_PANEL_NAME = "menuPanel";
    private static final String GAME_PANEL_NAME = "gamePanel";
    private static final String HUMAN_PLAYER = "Human";
    private static final String AI_PLAYER = "Computer";
    private static final Font fontNormal = new Font("Courier New", Font.PLAIN, 14);
    private static final Font fontHeading = new Font("Courier New", Font.BOLD, 32);
    private static final Dimension preferredFrameSize = new Dimension(640, 480);
    private static final EmptyBorder border = new EmptyBorder(10, 10, 10, 10);
    private static final int DELAY_BETWEEN_MOVE_PHASES = 300;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private JPanel menuPanel;
    private JPanel gamePanel;
    private JLabel currentPlayerLabel;
    private JLabel winnerLabel;
    private BoardPanel boardPanel;
    private GameModel gameModel;
    private boolean[] isAIPlayer;

    /**
     * Creates a panel with the main menu.
     * @return A panel with the main menu.
     */
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

    /**
     * Creates a panel with player settings.
     * @param playerId The ID of the player.
     * @return A panel with player settings.
     */
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

    /**
     * Creates a panel with the game.
     * @return A panel with the game.
     */
    private JPanel createGamePanel() {
        JPanel gamePanel = new JPanel(new BorderLayout());
        gamePanel.setBorder(border);

        gamePanel.add(createBoardPanel(), BorderLayout.CENTER);
        gamePanel.add(createGameControlPanel(), BorderLayout.LINE_END);

        gamePanel.setPreferredSize(preferredFrameSize);
        return gamePanel;
    }

    /**
     * Creates a board panel.
     * @return A board panel.
     */
    private JPanel createBoardPanel() {
        boardPanel = new BoardPanel();
        boardPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (isAIPlayer[gameModel.getCurrentPlayerId()]) {
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

    /**
     * Updates status of the game.
     */
    private void updateGameStatus() {
        if (gameModel.isGameOver()) {
            int winnerNumber = playerNumberFromId(gameModel.getWinnerId());
            currentPlayerLabel.setText("Player " + winnerNumber + " has won!");
        } else {
            int currentPlayerNumber = playerNumberFromId(gameModel.getCurrentPlayerId());
            currentPlayerLabel.setText("Player " + currentPlayerNumber + " on the move");
        }
    }

    /**
     * Gets the number of a player from their ID.
     * @param playerId The ID of the player.
     * @return The number of a player from their ID.
     */
    private int playerNumberFromId(int playerId) {
        return playerId + 1;
    }

    /**
     * Animates a move on the board.
     * @param move The move to animate.
     * @param markFirstPhase An indicator whether the first phase should be marked.
     */
    private void animateMove(DetailedMove move, boolean markFirstPhase) {
        for (int i = 0; i < move.phases().size(); i++) {
            DetailedMovePhase phase = move.phases().get(i);
            if (i == 0) {
                if (markFirstPhase) {
                    drawExplosionsAndTargets(phase.explosions(), phase.targets());
                    sleep(DELAY_BETWEEN_MOVE_PHASES);
                    drawBoard(phase.boardAfter());
                } else {
                    drawBoard(phase.boardAfter());
                }
            } else {
                sleep(DELAY_BETWEEN_MOVE_PHASES);
                drawExplosionsAndTargets(phase.explosions(), phase.targets());
                sleep(DELAY_BETWEEN_MOVE_PHASES);
                drawBoard(phase.boardAfter());
            }
        }
    }

    /**
     * Draws explosions and their targets.
     * @param explosions Explosions to draw.
     * @param targets Targets of explosions to draw.
     */
    private void drawExplosionsAndTargets(List<SquarePosition> explosions, List<SquarePosition> targets) {
        boardPanel.setExplosions(explosions);
        boardPanel.setTargets(targets);
        boardPanel.paintImmediately(0, 0, boardPanel.getWidth(), boardPanel.getHeight());
    }

    /**
     * Draws a board.
     * @param board The board to draw.
     */
    private void drawBoard(Board board) {
        boardPanel.setExplosions(null);
        boardPanel.setTargets(null);
        boardPanel.setBoard(board);
        boardPanel.paintImmediately(0, 0, boardPanel.getWidth(), boardPanel.getHeight());
    }

    /**
     * Makes the current thread sleep for a certain amount of time.
     * @param milliseconds The number of milliseconds the thread should sleep.
     */
    private void sleep(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ex) {
        }
    }

    /**
     * Creates a game control panel.
     * @return A game control panel.
     */
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

    /**
     * Starts a game.
     */
    private void playGame() {
        gameModel = new GameModel();
        Board board = gameModel.getCurrentBoardCopy();
        drawBoard(board);
        updateGameStatus();
        cardLayout.show(contentPanel, GAME_PANEL_NAME);
        if (isCurrentPlayerAI()) {
            SwingUtilities.invokeLater(this::moveAsAIPlayer);
        }
    }

    /**
     * Performs a move as a human player.
     * @param target The target square where to add an electron.
     */
    private void moveAsHumanPlayer(SquarePosition target) {
        if (gameModel.isGameOver()) {
            return;
        }
        DetailedMove move = gameModel.performMove(target);
        if (move == null) {
            return;
        }
        performMove(move, false);
    }

    /**
     * Performs a move as an AI player.
     */
    private void moveAsAIPlayer() {
        if (gameModel.isGameOver()) {
            return;
        }
        DetailedMove move = gameModel.performAIMove();
        sleep(DELAY_BETWEEN_MOVE_PHASES);
        performMove(move, true);
    }

    /**
     * Performs the given move.
     * @param move The move to perform.
     * @param markFirstPhase An indicator whether the first phase should be marked.
     */
    private void performMove(DetailedMove move, boolean markFirstPhase) {
        animateMove(move, markFirstPhase);
        updateGameStatus();
        if (isCurrentPlayerAI()) {
            SwingUtilities.invokeLater(this::moveAsAIPlayer);
        }
    }

    /**
     * Checks if the current player is an AI player.
     * @return True if the current player is an AI player, otherwise false.
     */
    private boolean isCurrentPlayerAI() {
        return isAIPlayer[gameModel.getCurrentPlayerId()];
    }

    /**
     * Quits the current game.
     */
    private void quitGame() {
        cardLayout.show(contentPanel, MENU_PANEL_NAME);
    }
}
