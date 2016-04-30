
//A single game of chess
import java.util.ArrayList;

/**
 * A game class. Used for playing one game
 */
public class ChessGame {

	private Board board;
	private String result;
	private int movesWithoutAgress;
	private int moves;
	private boolean whiteTurn;
	Player white, black;

	ChessGameInterface gameInterface;

	public ChessGame(Player playerOne, Player playerTwo, ChessGameInterface gameInterface) {
		gameSetup(playerOne, playerTwo);
		this.gameInterface = gameInterface;
		result = "Continue";
	}

	/*
	 * public boolean replay() {
	 * // asks to see if a replay is wanted
	 * }
	 */

	/**
	 * ChessGameDriver uses this to process a result
	 * 
	 * @param selectedCoord
	 *            the Coordinate selected
	 * @param destinationCoord
	 *            the Coordinate destined
	 */
	public boolean process(Coordinate selectedCoord, Coordinate destinationCoord) {
		String displayResult = "";
		Player currentPlayer;
		System.out.println(result);
		if (result.equals("Continue")) {
			if (whiteTurn)
				currentPlayer = board.getWhitePlayer();
			else
				currentPlayer = board.getBlackPlayer();

			if (!makeMove(currentPlayer, selectedCoord, destinationCoord)) {
				return false;
			} else {
				gameInterface.displayResult1("");
			}
			whiteTurn = !whiteTurn;
			result = checkGameOver();
			if (result.equals("Continue")) {
				if (whiteTurn)
					displayResult = "White Turn";
				else
					displayResult = "Black Turn";
			} else {
				displayResult = result;
			}
			gameInterface.displayResult0(displayResult);
			return true;
		}
		gameInterface.displayResult0(result);
		return false;
	}

	public boolean makeMove(Player currentPlayer, Coordinate selectedCoord, Coordinate destinationCoord) {

		Piece selectedPiece = board.getPieceAtCoord(selectedCoord);
		System.out.println(selectedPiece);
		if (selectedPiece == null || !currentPlayer.getColor().equals(selectedPiece.getColor()))
			return false;
		Piece cappedPiece;
		if (selectedCoord.equals(destinationCoord))
			return false;
		boolean capture = false;
		if (!(selectedPiece.testMove(board, destinationCoord))) {
			gameInterface.displayResult1("Not Valid");
			return false;
		} else {
			gameInterface.displayResult1("");
		}
		if (ChessGame.testCheck(board, destinationCoord, selectedCoord, whiteTurn)) {
			gameInterface.displayResult1("Check!");
			return false;
		}

		/*
		 * if (selectedPiece instanceof King) {
		 * kingTest((King) selectedPiece, destinationCoord);
		 * }
		 */
		if ((selectedPiece instanceof Rook) && (!((Rook) selectedPiece).hasMoved())) {
			((Rook) selectedPiece).setHasMoved(true);
		}
		cappedPiece = board.replace(destinationCoord, selectedPiece.getCoord());
		if (cappedPiece != null) {
			if (currentPlayer.getColor() == Color.WHITE) {
				getBlackPlayer().removePiece(cappedPiece);
				capture = true;
			} else {
				getWhitePlayer().removePiece(cappedPiece);
				capture = false;
			}
		}
		if (selectedPiece instanceof Pawn && !((Pawn) selectedPiece).hasMoved()) {
			((Pawn) selectedPiece).setHasMoved(true);
		}
		if ((selectedPiece instanceof Pawn) && ((Pawn) selectedPiece).promoteCheck()) {
			selectedPiece = promotePawn((Pawn) selectedPiece);
		}

		currentPlayer.addMove(selectedPiece + destinationCoord.getNotation());
		if (currentPlayer.getColor() == Color.BLACK)
			moves++;
		if ((selectedPiece instanceof Pawn) || capture)
			movesWithoutAgress = 0;
		else
			movesWithoutAgress++;

		board.getLocAt(selectedCoord).setPiece(null);
		board.getLocAt(destinationCoord).setPiece(selectedPiece);
		selectedPiece.setCoord(destinationCoord);

		return true;
	}

	/**
	 * Sets up a game
	 * 
	 * @param PlayerOne
	 *            the first player
	 * @param PlayerTwo
	 *            the second player
	 */
	private void gameSetup(Player playerOne, Player playerTwo) {
		if (oneGoesFirst()) {
			white = playerOne;
			black = playerTwo;
		} else {
			white = playerTwo;
			black = playerOne;
		}
		board = new Board(white, black) {
			@Override
			public void removePieceGUI(Coordinate coord) {
				gameInterface.removePiece(coord);
			}

		};
		whiteTurn = true;
		moves = 0;
		movesWithoutAgress = 0;
	}

	/** If Player One Goes first @return false */
	private boolean oneGoesFirst() {
		return false;
	}

	/** Checks if the game is over */
	private String checkGameOver() {
		if (checkDraw(whiteTurn ? Color.WHITE : Color.BLACK))
			return "Draw";
		if (checkWin())
			return winningPlayer().getName() + " wins!";
		return "Continue";
	}

	/** Checks if a player has won */
	private boolean checkWin() {
		Player white = getWhitePlayer();
		Player black = getBlackPlayer();
		if (whiteTurn && white.getKing().isInCheck(board, getBlackPlayer()))
			return checkMate(white);
		else if (!whiteTurn && black.getKing().isInCheck(board, getWhitePlayer()))
			return checkMate(black);
		return false;
	}

	/**
	 * Checks if the current player has a check mate
	 * 
	 * @return true if the current player has a checkmate
	 */
	private boolean checkMate(Player currentPlayer) {
		ArrayList<Piece> pieces = currentPlayer.getPieces();
		for (Piece piece : pieces) {
			if (piece.hasMove(board, (King) currentPlayer.getKing(), whiteTurn))
				return false;
		}
		return true;
	}

	/**
	 * Checks if theres a draw
	 * 
	 * @return true if theres a draw
	 */
	private boolean checkDraw(Color color) {

		if (fiftyMoveRule())
			return true;
		if (unwinnableGame())
			return true;
		if (stalemate(color))
			return true;
		if (threeMoveRule())
			return true;
		return false;

	}

	/** @return true if a game hits 50 moves */
	private boolean fiftyMoveRule() {
		return movesWithoutAgress == 50;
	}

	/** @return true if a game is unwinnable */
	private boolean unwinnableGame() {
		return (!matingMater(board.getWhitePlayer()) && !matingMater(board.getBlackPlayer()));
	}

	/**
	 * Used to determine if a game is unwinnable
	 * 
	 * @param player
	 *            the current player
	 * @return true if a game is still winnable
	 */
	private boolean matingMater(Player player) {
		Color squareColor;
		int bishopWhiteCount = 0;
		int bishopBlackCount = 0;
		int bishopSum;
		int knightCount = 0;
		ArrayList<Piece> pieces = player.getPieces();
		for (Piece piece : pieces) {
			if (piece instanceof Queen)
				return true;
			if (piece instanceof Rook)
				return true;
			if (piece instanceof Pawn)
				return true;
			if (piece instanceof Bishop) {
				squareColor = board.getLocAt(piece.getCoord()).getColor();
				if (squareColor == Color.WHITE)
					bishopWhiteCount++;
				else
					bishopBlackCount++;
			}
			if (piece instanceof Knight)
				knightCount++;
		}
		bishopSum = bishopWhiteCount + bishopBlackCount;
		if (bishopSum == 0)
			return knightCount >= 3;
		if ((bishopSum >= 1) && (knightCount >= 1))
			return true;
		return (bishopWhiteCount >= 1) && (bishopBlackCount >= 1);
	}

	/** @return true if either player cant beat the other */
	public boolean stalemate(Color color) {
		ArrayList<Piece> pieces;
		if (color == Color.WHITE)
			pieces = board.getWhitePlayer().getPieces();
		else
			pieces = board.getBlackPlayer().getPieces();
		for (Piece piece : pieces) {
			if (piece.hasMove(board, getWhitePlayer().getKing(), whiteTurn))
				return false;
		}
		return true;
	}

	/** @return true if the board is in the same position after three moves */
	public boolean threeMoveRule() {
		ArrayList<String> whiteMoves = board.getWhitePlayer().getMoves();
		ArrayList<String> blackMoves = board.getBlackPlayer().getMoves();
		if ((whiteMoves.size() < 3) && (blackMoves.size() < 3))
			return false;
		return testLastMoves(whiteMoves) && testLastMoves(blackMoves);
	}

	/** Used to help threeMoveRule() */
	private boolean testLastMoves(ArrayList<String> moves) {
		String move = moves.get(moves.size() - 3);
		for (int i = 2; i > 0; i--)
			if (!move.equals(moves.get(moves.size() - i)))
				return false;
		return true;
	}

	/** Tests if a player is in check */
	public static boolean testCheck(Board otherBoard, Coordinate to, Coordinate from, boolean turn) {
		Piece tempPiece = otherBoard.replace(to, from);
		Player player;
		Player oppPlayer;
		King king;
		boolean inCheck = false;
		if (turn) {
			player = otherBoard.getWhitePlayer();
			oppPlayer = otherBoard.getBlackPlayer();
		} else {
			player = otherBoard.getBlackPlayer();
			oppPlayer = otherBoard.getWhitePlayer();
		}
		king = player.getKing();
		if (tempPiece != null) {
			oppPlayer.removePiece(tempPiece);
		}
		if (king.isInCheck(otherBoard, oppPlayer)) {
			inCheck = true;
		}
		otherBoard.getLocAt(to).getPiece().setCoord(from);
		otherBoard.getLocAt(from).setPiece(otherBoard.getLocAt(to).getPiece());
		otherBoard.getLocAt(to).setPiece(tempPiece);

		if (tempPiece != null)
			oppPlayer.addPiece(tempPiece);
		return inCheck;
	}

	/**
	 * Promotes a pawn to a queen when it reaches the other side
	 * 
	 * @param piece
	 *            the Pawn to be promoted
	 * @return Queen the newly created QueenÏ
	 */
	public Queen promotePawn(Pawn piece) {
		Queen newPiece = promotedPiece(piece.getCoord(), piece.getColor());
		board.getLocAt(piece.getCoord()).setPiece(newPiece);
		if (piece.getColor() == Color.WHITE) {
			board.getWhitePlayer().addPiece(newPiece);
			board.getWhitePlayer().removePiece(piece);
		} else {
			board.getBlackPlayer().addPiece(newPiece);
			board.getBlackPlayer().removePiece(piece);
		}
		gameInterface.promotePawn(piece.getColor(), piece.getCoord());
		return newPiece;
	}

	/** The queen that the promoted pawn turns into */
	public Queen promotedPiece(Coordinate coord, Color color) {
		return new Queen(color, coord);
	}

	/** @return the winner of this game */
	private Player winningPlayer() {
		if (whiteTurn)
			return board.getBlackPlayer();
		else
			return board.getWhitePlayer();
	}

	public Player getWhitePlayer() {
		return board.getWhitePlayer();
	}

	public Player getBlackPlayer() {
		return board.getBlackPlayer();
	}

	public Board getBoard() {
		return board;
	}

}
