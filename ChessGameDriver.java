import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

/** The Driver for this program */
public class ChessGameDriver extends JFrame implements MouseMotionListener, MouseListener, ChessGameInterface {
	JLayeredPane layeredPane;
	JPanel chessBoard;
	JLabel chessPiece;
	int xAdjustment;
	int yAdjustment;

	Color promoteColor = null;

	Coordinate selectedCoord, destinationCoord;
	JLabel output0, output1;

	final java.awt.Color brown = new java.awt.Color(136, 67, 8);

	ChessGame game;

	/* Consructs a ChessGameDriver */
	public ChessGameDriver() {
		Dimension boardSize = new Dimension(600, 600);
		Dimension windowSize = new Dimension(600, 630);

		// Using a layered pane
		// A layered pane adds depth to our JFrame
		layeredPane = new JLayeredPane();
		getContentPane().add(layeredPane);
		layeredPane.setPreferredSize(windowSize);
		layeredPane.addMouseListener(this);
		layeredPane.addMouseMotionListener(this);

		// Add our empty chessboard to the pane
		chessBoard = new JPanel();
		layeredPane.add(chessBoard, JLayeredPane.DEFAULT_LAYER);
		chessBoard.setLayout(new GridLayout(9, 8)); // GridLayout perfect for
													// chess game
		chessBoard.setPreferredSize(boardSize);
		chessBoard.setBounds(0, 0, boardSize.width, boardSize.height);

		for (int i = 0; i < 64; i++) {
			JPanel square = new JPanel(new BorderLayout());
			chessBoard.add(square);

			int row = (i / 8) % 2; // alternates colors
			if (row == 0)
				square.setBackground(i % 2 == 0 ? java.awt.Color.white : brown);
			else
				square.setBackground(i % 2 == 0 ? brown : java.awt.Color.white);
		}

		// add output text
		output0 = new JLabel("White turn", JLabel.LEFT);
		chessBoard.add(output0);
		output1 = new JLabel("", JLabel.RIGHT);
		chessBoard.add(output1);

		// Add black pieces to the board
		int letter;
		for (letter = 0; letter <= 7; letter += 7)
			addPiece("BlackRook.png", 0, letter);
		for (letter = 1; letter <= 6; letter += 5)
			addPiece("BlackKnight.png", 0, letter);
		for (letter = 2; letter <= 5; letter += 3)
			addPiece("BlackBishop.png", 0, letter);
		for (letter = 0; letter <= 7; ++letter)
			addPiece("BlackPawn.png", 1, letter);
		addPiece("BlackQueen.png", 0, 3);
		addPiece("BlackKing.png", 0, 4);

		// white pieces to the board
		for (letter = 0; letter <= 7; letter += 7)
			addPiece("Rook.png", 7, letter);
		for (letter = 1; letter <= 6; letter += 5)
			addPiece("Knight.png", 7, letter);
		for (letter = 2; letter <= 5; letter += 3)
			addPiece("Bishop.png", 7, letter);
		for (letter = 0; letter <= 7; ++letter)
			addPiece("Pawn.png", 6, letter);
		addPiece("Queen.png", 7, 3);
		addPiece("King.png", 7, 4);

		// finally, creates new instance of game
		game = new ChessGame(new Player("Black"), new Player("White"), this);

	}

	private void addPiece(ImageIcon ico, int num, int letter) {
		Coordinate coord = new Coordinate(num, letter);
		addPiece(ico, coord);
	}

	/**
	 * Adds a piece to the Location (square) at given coordinate with ImageIcon
	 * displayed
	 */
	private void addPiece(ImageIcon ico, Coordinate coord) {
		JLabel piece = new JLabel(ico);
		JPanel panel = (JPanel) chessBoard.getComponent(coordToComponentIndex(coord));
		panel.add(piece);
	}

	/**
	 * Adds a piece to the Location (square) at given coordinate, with the name
	 * of the icon displayed
	 * 
	 * @param ico
	 *            the name of the Icon
	 * @param num
	 *            the num of the Coordinate
	 * @param letter
	 *            the letter of the Coordinate
	 */
	private void addPiece(String ico, int num, int letter) {
		addPiece(new ImageIcon(ico), num, letter);
	}

	/**
	 * Adds a piece to the Location (square) at given coordinate, with the name
	 * of the icon displayed
	 * 
	 * @param ico
	 *            the name of the Icon
	 * @param coord
	 *            the Coordinate of the new Piece
	 */
	private void addPiece(String ico, Coordinate coord) {
		addPiece(new ImageIcon(ico), coord);
	}

	public void mousePressed(MouseEvent e) {
		chessPiece = null;
		Component c = chessBoard.findComponentAt(e.getX(), e.getY());

		if (c instanceof JPanel || c == output0 || c == output1) // if its the
																	// output or
																	// not a
																	// piece
			return;

		selectedCoord = componentIndexToCoord(chessBoard.getComponentZOrder(c.getParent()));

		Point parentLocation = c.getParent().getLocation();
		xAdjustment = parentLocation.x - e.getX(); // the left up corner of
													// piece minus the initial
													// mouse click location
		// this keeps the piece drawn relative to the mouse movement
		yAdjustment = parentLocation.y - e.getY();
		chessPiece = (JLabel) c;
		chessPiece.setLocation(e.getX() + xAdjustment, e.getY() + yAdjustment);
		chessPiece.setSize(chessPiece.getWidth(), chessPiece.getHeight());
		layeredPane.add(chessPiece, JLayeredPane.DRAG_LAYER); // for when a
																// piece is
																// dragged
	}

	/** Called when mouse dragged. Moves the chess piece around */
	public void mouseDragged(MouseEvent me) {
		if (chessPiece == null)
			return;
		chessPiece.setLocation(me.getX() + xAdjustment, me.getY() + yAdjustment);
		// this keeps the piece drawn relative to the mouse movement
	}

	/**
	 * Called when mouse released. If there was a piece selected, drop it on its
	 * new location
	 */
	public void mouseReleased(MouseEvent e) {
		if (chessPiece == null)
			return;

		Component c = chessBoard.findComponentAt(e.getX(), e.getY());
		if (c == output0 || c == output1)
			return;

		chessPiece.setVisible(false);
		Container parent;
		boolean isPiece = c instanceof JLabel;
		if (isPiece)
			parent = c.getParent();
		else
			parent = (Container) c;

		destinationCoord = componentIndexToCoord(chessBoard.getComponentZOrder(parent));
		System.out.println("Selected Coord: " + selectedCoord);
		System.out.println("Destination Coord: " + destinationCoord);
		boolean result;
		if (Board.isValid(destinationCoord))
			result = game.process(selectedCoord, destinationCoord);
		else
			result = false;
		if (!result) {
			JPanel prev = (JPanel) chessBoard.getComponent(coordToComponentIndex(selectedCoord));
			prev.add(chessPiece);
			chessPiece.setVisible(true);
			resetCoords();
			return;
		}
		resetCoords();

		if (isPiece)
			parent.remove(0);

		if (promoteColor != null) {
			// if it's not null, a pawn is being promoted (to a queen)
			chessPiece = new JLabel(new ImageIcon(promoteColor == Color.BLACK ? "BlackQueen.png" : "Queen.png"));
			promoteColor = null;
		}

		parent.add(chessPiece);

		chessPiece.setVisible(true);

	}

	private void resetCoords() {
		selectedCoord = null;
		destinationCoord = null;
		// prevents interference
	}

	public void mouseClicked(MouseEvent e) {

	}

	public void mouseMoved(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {

	}

	public void mouseExited(MouseEvent e) {

	}

	/** @return the Coordinate at the Location Index in the chessBoard */
	public static Coordinate componentIndexToCoord(int component) {
		int row = component / 8; // aka num
		int col = component % 8; // aka letter
		return new Coordinate(row, col);
	}

	/**
	 * @return the index of a component in the chessboard based on a coordinate
	 */
	public static int coordToComponentIndex(Coordinate coord) {

		return 8 * coord.num + coord.letter;
	}

	public static void main(String[] args) {
		JFrame frame = new ChessGameDriver();
		frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setResizable(true);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	@Override
	public void displayResult0(String result) {
		output0.setText(result);
	}

	@Override
	public void displayResult1(String result) {
		output1.setText(result);
	}

	@Override
	public void promotePawn(Color color, Coordinate coord) {
		promoteColor = color;
	}

	@Override
	public void removePiece(Coordinate coord) {
		JPanel panel = (JPanel) chessBoard.getComponent(coordToComponentIndex(coord));
		panel.removeAll();
		panel.repaint();
	}

}
