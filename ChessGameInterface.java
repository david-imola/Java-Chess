
/**ChessGameInterface. ChessGameDriver implements it for displaying result, and the occasion of promoting a pawn*/
public interface ChessGameInterface {
        public void displayResult1(String result);
        public void displayResult0(String result);
        public void promotePawn(Color color, Coordinate coord);
		public void removePiece(Coordinate coord);
}
