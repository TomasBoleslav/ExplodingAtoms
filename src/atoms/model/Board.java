package atoms.model;

public final class Board {
    public static final int NO_PLAYER_ID = -1;

    public Board(int size) {
        squares = new Square[size][];
        for (int i = 0; i < size; i++) {
            squares[i] = new Square[size];
            for (int j = 0; j < size; j++) {
                squares[i][j] = new Square(NO_PLAYER_ID, 0);
            }
        }
    }

    private Board(Square[][] squares) {
        this.squares = squares;
    }

    public Square getSquare(int row, int column) {
        return squares[row][column];
    }

    public Square getSquare(SquarePosition position) {
        return squares[position.row()][position.column()];
    }

    public void setSquare(int row, int column, Square square) {
        squares[row][column] = square;
    }

    public void setSquare(SquarePosition position, Square square) {
        squares[position.row()][position.column()] = square;
    }

    public int getSize() {
        return squares.length;
    }

    public Board copy() {
        // only makes sense if squares are immutable
        Square[][] copySquares = new Square[squares.length][];
        for (int i = 0; i < squares.length; i++) {
            copySquares[i] = squares[i].clone();
        }
        return new Board(copySquares);
    }

    private final Square[][] squares;
}
