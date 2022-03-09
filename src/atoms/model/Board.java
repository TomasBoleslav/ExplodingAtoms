package atoms.model;

/**
 * Board for the Exploding Atoms game.
 */
public final class Board {
    public static final int NO_PLAYER_ID = -1;

    /**
     * Creates a board of the given size.
     * @param size The size of the board.
     */
    public Board(int size) {
        squares = new Square[size][];
        for (int i = 0; i < size; i++) {
            squares[i] = new Square[size];
            for (int j = 0; j < size; j++) {
                squares[i][j] = new Square(NO_PLAYER_ID, 0);
            }
        }
    }

    /**
     * Creates a board with the given squares.
     * @param squares
     */
    private Board(Square[][] squares) {
        this.squares = squares;
    }

    /**
     * Gets a square at the given position specified as coordinates.
     * @param row The row.
     * @param column The column.
     * @return The square at the given position.
     */
    public Square getSquare(int row, int column) {
        return squares[row][column];
    }

    /**
     * Gets a square at the given position specified as coordinates.
     * @param position The position of the square.
     * @return The square at the given position.
     */
    public Square getSquare(SquarePosition position) {
        return squares[position.row()][position.column()];
    }

    /**
     * Sets square at the given position.
     * @param position The position of the square.
     * @param square The new square.
     */
    public void setSquare(SquarePosition position, Square square) {
        squares[position.row()][position.column()] = square;
    }

    /**
     * Gets the size of the board.
     * @return The size of the board.
     */
    public int getSize() {
        return squares.length;
    }

    /**
     * Creates a deep copy of the board.
     * @return A deep copy of the board.
     */
    public Board deepCopy() {
        Square[][] copySquares = new Square[squares.length][];
        for (int i = 0; i < squares.length; i++) {
            copySquares[i] = squares[i].clone();
        }
        return new Board(copySquares);
    }

    private final Square[][] squares;
}
