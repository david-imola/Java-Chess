
/**ChessGameInterface. ChessGameDriver implements it for displaying result, and the occasion of promoting a pawn*/
public interface ChessGameInterface {
	/**GUI displays the secondary result from the chess game*/
        public void displayResult1(String result);
        /**GUI displays the primary result from the chess game*/
        public void displayResult0(String result);
        /**GUI promote the pawn into a queen*/
        public void promotePawn(Color color, Coordinate coord);
        /**GUI remove a piece from the board*/
		public void removePiece(Coordinate coord);
}
