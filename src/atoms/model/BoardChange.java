package atoms.model;

// Maybe ElectronsCount will suffice? But then there will have to be StolenSquares list
public record BoardChange (SquarePosition position, Square oldSquare, Square newSquare) {}
