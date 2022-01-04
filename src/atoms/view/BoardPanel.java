package atoms.view;

import atoms.model.Board;
import atoms.model.Square;
import atoms.model.SquarePosition;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Point;
import java.util.List;

public class BoardPanel extends JPanel {

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setTargets(List<SquarePosition> targets) {
        this.targets = targets;
    }

    public void setExplosions(List<SquarePosition> explosions) {
        this.explosions = explosions;
    }

    public Board getBoard() {
        return board;
    }

    public SquarePosition getSquarePositionFromPoint(int x, int y) {
        int squareSize = getSquareSize();
        int row = y / squareSize;
        int column = x / squareSize;
        if (row >= board.getSize() || column >= board.getSize()) {
            return null;
        }
        return new SquarePosition(row, column);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (board == null) {
            return;
        }

        setAntialiasing(g);
        drawSquares(g);
        drawExplosions(g);
        drawTargets(g);
        drawAtoms(g);
    }

    private static void setAntialiasing(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        RenderingHints renderingHints = new RenderingHints(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHints(renderingHints);
    }

    private void drawSquares(Graphics g) {
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                Rectangle squareRect = getSquareRect(i, j);
                Color color;
                if (squareIsWhite(i, j)) {
                    color = whiteSquareColor;
                } else {
                    color = blackSquareColor;
                }
                g.setColor(color);
                g.fillRect(squareRect.x, squareRect.y, squareRect.width, squareRect.height);
            }
        }
    }

    private Rectangle getSquareRect(int i, int j) {
        int squareSize = getSquareSize();
        int x = j * squareSize;
        int y = i * squareSize;
        return new Rectangle(x, y, squareSize, squareSize);
    }

    private int getSquareSize() {
        int boardSideLength = Math.min(this.getWidth(), this.getHeight());
        return boardSideLength / board.getSize();
    }

    private static boolean squareIsWhite(int i, int j) {
        if (i % 2 == 0) {
            return j % 2 == 0;
        } else {
            return j % 2 == 1;
        }
    }

    private void drawExplosions(Graphics g) {
        if (explosions != null) {
            drawSquareList(g, explosions, explosionSquareColor);
        }
    }

    private void drawTargets(Graphics g) {
        if (targets != null) {
            drawSquareList(g, targets, targetSquareColor);
        }
    }

    private void drawSquareList(Graphics g, List<SquarePosition> squarePositions, Color color) {
        g.setColor(color);
        for (SquarePosition position : squarePositions) {
            Rectangle squareRect = getSquareRect(position.row(), position.column());
            g.fillRect(squareRect.x, squareRect.y, squareRect.width, squareRect.height);
        }
    }

    private void drawAtoms(Graphics g) {
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                Square square = board.getSquare(i, j);
                if (square.electronsCount() > 0) {
                    Rectangle squareRect = getSquareRect(i, j);
                    Color color = playerColors[square.playerId()];
                    drawAtom(g, squareRect, color, square.electronsCount());
                }
            }
        }
    }

    private void drawAtom(Graphics g, Rectangle squareRect, Color color, int electronsCount) {
        int electronSize = squareRect.width / 4;
        Point squareCenter = new Point(
                squareRect.x + squareRect.width / 2,
                squareRect.y + squareRect.height / 2
        );
        if (electronsCount == 1) {
            drawElectron(g, squareCenter, electronSize, color);
            return;
        }
        Point[] electronCenters = getRegularPolygon(squareCenter, electronSize, electronsCount);
        for (Point electronCenter : electronCenters) {
            drawElectron(g, electronCenter, electronSize, color);
        }
    }

    private void drawElectron(Graphics g, Point center, int size, Color color) {
        int x = center.x - size / 2;
        int y = center.y - size / 2;
        g.setColor(color);
        g.fillOval(x, y, size, size);
        g.setColor(Color.BLACK);
        g.drawOval(x, y, size, size);
    }

    private static Point[] getRegularPolygon(Point center, int diameter, int sidesCount) {
        Point[] points = new Point[sidesCount];
        Point firstPoint = new Point(center.x - diameter / 2, center.y);
        points[0] = firstPoint;
        double angleRad = (2 * Math.PI) / sidesCount;
        for (int i = 1; i < sidesCount; i++) {
            points[i] = rotatePoint(firstPoint, center, i * angleRad);
        }
        return points;
    }

    private static Point rotatePoint(Point point, Point center, double angleRad) {
        int shiftedX = point.x - center.x;
        int shiftedY = point.y - center.y;
        double rotatedX = shiftedX * Math.cos(angleRad) - shiftedY * Math.sin(angleRad);
        double rotatedY = shiftedX * Math.sin(angleRad) + shiftedY * Math.cos(angleRad);
        return new Point(
                (int)rotatedX + center.x,
                (int)rotatedY + center.y
        );
    }

    private final static Color whiteSquareColor = Color.WHITE;
    private final static Color blackSquareColor = Color.BLACK;
    private final static Color[] playerColors = new Color[] { Color.CYAN, Color.GREEN };
    private final static Color explosionSquareColor = new Color(200, 0, 0);
    private final static Color targetSquareColor = new Color(200, 200, 0);
    private Board board;
    private List<SquarePosition> explosions;
    private List<SquarePosition> targets;
}
