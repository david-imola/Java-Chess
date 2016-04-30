/**
 * A class containing a 2d, 8x8 array of Locations. Each location contains
 * a piece, and null if there's not one there
 */
public abstract class Board {
	private final Location[][] BOARD;
	// A board of Locations; 8x8 with the first [] detailing the number(y)
	// axis, and the second [] the letter(x) axis

	private static final int LEFT_BOUND = 0;
	private static final int RIGHT_BOUND = 7;
	private static final int UP_BOUND = 0;
	private static final int DOWN_BOUND = 7;
	private final Player WHITE_PLAYER;
	private final Player BLACK_PLAYER;

	/** Constructor for board with a white player and black player */
	public Board(Player w, Player b) {
		BOARD = new Location[DOWN_BOUND + 1][RIGHT_BOUND + 1];
		w.setColor(Color.WHITE);
		b.setColor(Color.BLACK);
		WHITE_PLAYER = w;
		BLACK_PLAYER = b;
		boardInit();
	}

	/** Adds a Location to the board at row num, column letter */
	private void addToBoard(Location loc, int num, int letter) {
		BOARD[num][letter] = loc;
	}

	/** Checks if a location contains a piece at the given coordinate */
	public boolean isEmpty(Coordinate coord) {
		int num = coord.getNum();
		int letter = coord.getLetter();
		return BOARD[num][letter].getPiece() == null;
	}

	/**
	 * Checks if a certain coordinate is within the bounds for a chess board
	 * 
	 * @return true if the coordinate is within the board
	 */
	public static boolean isValid(Coordinate coord) {
		int num = coord.getNum();
		int letter = coord.getLetter();
		return num <= RIGHT_BOUND && letter <= DOWN_BOUND && num >= LEFT_BOUND && letter >= UP_BOUND;
	}

	/**
	 * Initializes the board with black and white squares, and black/white
	 * pieces with their respective Players
	 */
	public void boardInit() {
		Color color = Color.WHITE; // Better count on this working becuase
		// it's white by default because it has to be initialised
		Color squareColor = Color.WHITE;
		for (int num = 0; num < 8; num++) { // "number"(y) axis
			for (int letter = 0; letter < 8; letter++) { // "letter"(x) axis
				addToBoard(new Location(null, new Coordinate(num, letter), squareColor), num, letter);
				if (squareColor == Color.WHITE)
					squareColor = Color.BLACK;
				else
					squareColor = Color.WHITE;
			}
		}

		// Add white pieces to the board
		int letter;
		for (letter = 0; letter <= 7; letter += 7)
			addPiece(new Rook(Color.WHITE, new Coordinate(7, letter)));
		for (letter = 1; letter <= 6; letter += 5)
			addPiece(new Knight(Color.WHITE, new Coordinate(7, letter)));
		for (letter = 2; letter <= 5; letter += 3)
			addPiece(new Bishop(Color.WHITE, new Coordinate(7, letter)));
		for (letter = 0; letter <= 7; ++letter)
			addPiece(new Pawn(Color.WHITE, new Coordinate(6, letter)));
		addPiece(new Queen(Color.WHITE, new Coordinate(7, 3)));

		King whiteKing = new King(Color.WHITE, new Coordinate(7, 4));
		addPiece(whiteKing);
		WHITE_PLAYER.setKing(whiteKing);

		// Black pieces to the board
		for (letter = 0; letter <= 7; letter += 7)
			addPiece(new Rook(Color.BLACK, new Coordinate(0, letter)));
		for (letter = 1; letter <= 6; letter += 5)
			addPiece(new Knight(Color.BLACK, new Coordinate(0, letter)));
		for (letter = 2; letter <= 5; letter += 3)
			addPiece(new Bishop(Color.BLACK, new Coordinate(0, letter)));
		for (letter = 0; letter <= 7; ++letter)
			addPiece(new Pawn(Color.BLACK, new Coordinate(1, letter)));

		addPiece(new Queen(Color.BLACK, new Coordinate(0, 3)));

		King blackKing = new King(Color.BLACK, new Coordinate(0, 4));
		addPiece(blackKing);
		BLACK_PLAYER.setKing(blackKing);
	}

	/**
	 * Adds piece at the location given by the coordinate that belongs to Piece
	 * p
	 */
	public void addPiece(Piece p) {
		if (p.getColor().equals(Color.BLACK))
			BLACK_PLAYER.addPiece(p);
		else
			WHITE_PLAYER.addPiece(p);

		BOARD[p.getNum()][p.getLetter()].setPiece(p);
	}

	/**
	 * Returns the location in the array at the given Coordinate
	 * 
	 * @return the Location at the give Coordinate
	 */
	public Location getLocAt(Coordinate coord) { // access a location on the
													// board using notation
		int num = coord.getNum();
		int letter = coord.getLetter();
		return BOARD[num][letter];
	}

	/**
	 * Sets the Location's Piece at Coordinate to to the Piece at the Location
	 * at Coordinate from
	 * 
	 * @param to
	 *            the new Coordinate to which the Piece will be moved
	 * @param from
	 *            the old coordinate from which the Piece will be moved
	 * @return the Piece that previously occupied the Location at Coordoinate to
	 */
	public Piece replace(Coordinate to, Coordinate from) {
		Piece piece = getPieceAtCoord(to);
		Piece movedPiece = getPieceAtCoord(from);
		this.getLocAt(to).setPiece(movedPiece);
		this.getLocAt(from).setPiece(null);
		movedPiece.setCoord(to);
		return piece;
	}

	/** @return the White Player */
	public Player getWhitePlayer() {
		return WHITE_PLAYER;
	}

	/** @return the Black Player */
	public Player getBlackPlayer() {
		return BLACK_PLAYER;
	}

	/**
	 * @return the piece at the Location occupied by Coordinate coord
	 * @param coord
	 *            the Coordinate of the desired Piece's Location
	 */
	public Piece getPieceAtCoord(Coordinate coord) {
		return getLocAt(coord).getPiece();
	}

	/**
	 * A method to remove a piece from the GUI when a piece doesn't take over a
	 * piece normally (Only used in en passant pawn move)
	 */
	public abstract void removePieceGUI(Coordinate pawnRemove);

}
