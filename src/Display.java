import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JLabel;
import java.awt.BorderLayout;

public class Display extends JComponent implements MouseListener, MouseMotionListener, ActionListener, KeyListener {

	public static final int ROWS = 9;
	public static final int COLS = 9;
	private final int X_GRID_OFFSET = 70; // 70 pixels from left
	private final int Y_GRID_OFFSET = 50; // 50 pixels from top
	private final int TITLE_HEIGHT = 30;
	private final int BOX_WIDTH = 50;
	private final int BOX_HEIGHT = 50;
	private final int THIN_LINE = 1;
	private final int THICK_LINE = 5;
	private static JFrame frame;

	private static JLabel emptyLabel = new JLabel("");
	private static Box[][] boxes = new Box[9][9];	// for the grid
	private static JLabel[][] boxLabels = new JLabel[9][9];	// for labels
	private static int[][] boxValues = new int[9][9];	// for values
	private static int[] horizontalLine = new int[10];	// for values of ______
	private static int[] verticalLine = new int[10];  // for values of ____

	private int x = -1;
	private int y = -1;

	private final int DISPLAY_WIDTH;
	private final int DISPLAY_HEIGHT;

	// buttons
	private GenerateBoardButton generateBoard;
	private SolveAllButton solveAll;
	private ClearBoardButton clearBoard;

	private boolean paintloop = false;

	public Display(JFrame jf, int width, int height) {
		DISPLAY_WIDTH = width;
		DISPLAY_HEIGHT = height;
		frame = jf;
		init();
	}

	public void init() {
		setSize(DISPLAY_WIDTH, DISPLAY_HEIGHT);

		JLabel numLabel = new JLabel("SUDOKU");
		
		for (int row = 0; row < ROWS; row++) {
			for (int col = 0; col < COLS; col++) {
				int rx = X_GRID_OFFSET + col * BOX_WIDTH;
				int ry = Y_GRID_OFFSET + row * BOX_HEIGHT;
				boxLabels[row][col] = emptyLabel;
				Rectangle rect = new Rectangle(rx + 1, ry + 1, BOX_WIDTH - 3, BOX_HEIGHT - 3);
				boxLabels[row][col].setBounds(rect);
				boxLabels[row][col].setVerticalAlignment(JLabel.CENTER);
				boxLabels[row][col].setHorizontalAlignment(JLabel.CENTER);
				frame.getContentPane().add(boxLabels[row][col]);
				boxValues[row][col] = 0;		
				boxes[row][col] = new Box(row, col);
			}
		}

		frame.addMouseListener(this);
		frame.addMouseMotionListener(this);
		frame.addKeyListener(this);
		frame.setFocusable(true);

		generateBoard = new GenerateBoardButton();
		generateBoard.setBounds(75, 535, 125, 36);
		add(generateBoard);
		generateBoard.setVisible(true);

		solveAll = new SolveAllButton();
		solveAll.setBounds(245, 535, 100, 36);
		add(solveAll);
		solveAll.setVisible(true);

		clearBoard = new ClearBoardButton();
		clearBoard.setBounds(390, 535, 125, 36);
		add(clearBoard);
		clearBoard.setVisible(true);

		Container container = frame.getContentPane();

		JTextField textField = new JTextField(16);
		container.add(textField);
		textField.addActionListener(this);
		initBoard();
		repaint();
	}

	// sample board
	public void initBoard() {

	}

	public void paintComponent(Graphics g) {
		final int TIME_BETWEEN_REPLOTS = 1000; // change to your liking

		g.setColor(Color.BLACK);
		drawGrid(g);
		if ((x >= 0 && x < COLS) && (y >= 0 && y < ROWS)) {
			//Box box = new Box(x, y, true, Color.BLUE);
			drawBox(g);

			boxLabels[x][y].setVisible(true);
			boxLabels[x][y].setOpaque(true);
			boxLabels[x][y].setForeground(Color.red);
			frame.setVisible(true);
		}

		drawButtons();

		g.drawRect(0, 0, DISPLAY_WIDTH - 15, 30);
		JLabel title = new JLabel("SUDOKU");
		title.setBounds(0, 0, DISPLAY_WIDTH, 30);
		title.setVerticalAlignment(JLabel.CENTER);
		title.setHorizontalAlignment(JLabel.CENTER);
		title.setForeground(Color.MAGENTA);
		frame.getContentPane().add(title);

		if (paintloop) {
			try {
				Thread.sleep(TIME_BETWEEN_REPLOTS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nextGeneration();
			repaint();
		}
	}

	public void togglePaintLoop() {
		paintloop = !paintloop;
	}

	public void setPaintLoop(boolean value) {
		paintloop = value;
	}

	private void drawGrid(Graphics g) {
		Graphics2D thinLine = (Graphics2D) g;
		thinLine.setStroke(new BasicStroke(THIN_LINE));
		for (int row = 0; row <= ROWS; row++) { // draws horizontal lines
			g.drawLine(X_GRID_OFFSET, Y_GRID_OFFSET + (row * (BOX_HEIGHT)),
					X_GRID_OFFSET + COLS * (BOX_WIDTH), Y_GRID_OFFSET
					+ (row * (BOX_HEIGHT)));
			horizontalLine[row] = TITLE_HEIGHT + Y_GRID_OFFSET
					+ (row * (BOX_HEIGHT));
		}

		for (int col = 0; col <= COLS; col++) { // draws vertical lines
			g.drawLine(X_GRID_OFFSET + (col * (BOX_WIDTH)), Y_GRID_OFFSET,
					X_GRID_OFFSET + (col * (BOX_WIDTH)), Y_GRID_OFFSET + ROWS
					* (BOX_HEIGHT));
			verticalLine[col] = X_GRID_OFFSET + (col * (BOX_WIDTH)); 
		}

		Graphics2D thickLine = (Graphics2D) g;
		thickLine.setStroke(new BasicStroke(THICK_LINE));
		for (int row = 0; row <= ROWS; row += 3) {
			g.drawLine(X_GRID_OFFSET, Y_GRID_OFFSET + (row * (BOX_HEIGHT)),
					X_GRID_OFFSET + COLS * (BOX_WIDTH), Y_GRID_OFFSET
					+ (row * (BOX_HEIGHT)));
		}

		for (int col = 0; col <= COLS; col += 3) {
			g.drawLine(X_GRID_OFFSET + (col * (BOX_WIDTH)), Y_GRID_OFFSET,
					X_GRID_OFFSET + (col * (BOX_WIDTH)), Y_GRID_OFFSET + ROWS
					* (BOX_HEIGHT));

		}
	}

	private void drawButtons() {
		generateBoard.repaint();
		solveAll.repaint();
		// revealOneNumber.repaint();
	}

	private void nextGeneration() {

	}

	public void mouseClicked(MouseEvent arg0) {
		System.out.println(arg0.getX());
		System.out.println(arg0.getY());
		int xCoord = arg0.getX();
		int yCoord = arg0.getY();

		int horizontalLineNum = -1;
		while (horizontalLineNum < ROWS
				&& yCoord > horizontalLine[horizontalLineNum + 1]) {
			horizontalLineNum++;
		}

		int verticalLineNum = -1;
		while (verticalLineNum < COLS
				&& xCoord > verticalLine[verticalLineNum + 1]) {
			verticalLineNum++;
		}

		x = verticalLineNum;
		y = horizontalLineNum;
		System.out.println("Just clicked box " + x + " by " + y);
		repaint();
	}

	void drawBox(Graphics g) {
		boxes[x][y].draw(x, y, X_GRID_OFFSET, Y_GRID_OFFSET, BOX_WIDTH, BOX_HEIGHT,
				THIN_LINE, THICK_LINE, g);
	}

	public void mouseEntered(MouseEvent arg0) {

	}

	public void mouseExited(MouseEvent arg0) {

	}

	public void mousePressed(MouseEvent arg0) {

	}

	public void mouseReleased(MouseEvent arg0) {

	}

	public void mouseDragged(MouseEvent arg0) {

	}

	public void mouseMoved(MouseEvent arg0) {

	}

	private class GenerateBoardButton extends JButton implements ActionListener {
		GenerateBoardButton() {
			super("Generate Board");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			clearBoard(boxValues);
			Generator generator = new Generator();
			boxValues = generator.fillBoard();	
			generator.remove(boxValues);
			System.out.println("\nFINAL BOARD:");
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					System.out.print(boxValues[j][i] + " ");
				}
				System.out.println();
			}
			String color = "blue";
			putBoard(boxValues, color);

			if (this.getText().equals("Generate Board")) {
				togglePaintLoop();
				setText("Generated Board");
			} else {
				togglePaintLoop();
				setText("Generate Board");
			}
			repaint();
		}
	}

	private class SolveAllButton extends JButton implements ActionListener {
		SolveAllButton() {
			super("Solve All");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			Solver solver = new Solver();

			System.out.println("ORIGINAL:");
			for (int i = 0; i < 9; i++) {
				for (int j = 0; j < 9; j++) {
					System.out.print(boxValues[j][i] + " ");
				}
				System.out.println();
			}

			if (solver.solve(boxValues)) {
				System.out.println("SOLVED:");
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						System.out.print(boxValues[j][i] + " ");
					}
					System.out.println();
				}
				String color = "black";
				putBoard(boxValues, color);
			}

			//			if (this.getText().equals("Start")) {
			//				togglePaintLoop();
			//				setText("Stop");
			//			} else {
			//				togglePaintLoop();
			//				setText("Start");
			//			}
			repaint();
		}
	}

	private class ClearBoardButton extends JButton implements ActionListener {
		ClearBoardButton() {
			super("Clear Board");
			addActionListener(this);
		}

		public void actionPerformed(ActionEvent arg0) {
			clearBoard(boxValues);

			repaint();
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	public void putBoard(int[][] board, String color) {
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				if (board[i][j] != 0) {
					String stringVal = String.valueOf(board[i][j]);
					JLabel numLabel = new JLabel(stringVal);
					int rx = X_GRID_OFFSET + i * BOX_WIDTH;
					int ry = Y_GRID_OFFSET + j * BOX_HEIGHT;
					Rectangle rect = new Rectangle(rx + 1, ry + 1,
							BOX_WIDTH - 3, BOX_HEIGHT - 3);
					numLabel.setBounds(rect);
					numLabel.setVerticalAlignment(JLabel.CENTER);
					numLabel.setHorizontalAlignment(JLabel.CENTER);
					if (color.equals("blue")) {
						numLabel.setForeground(Color.BLUE);
					} else if (color.equals("black")) {
						numLabel.setForeground(Color.BLACK);
					}

					// only add label if no label there yet
					frame.getContentPane().add(numLabel);
					if (boxLabels[i][j] == emptyLabel) {
						boxLabels[i][j] = numLabel;
					} else {
						frame.getContentPane().remove(numLabel);
					}
					boxValues = board;
				}
			}

		}

	}

	//removes all labels from board, sets boxValues to 0
	//changed from boxLabels[i][j] = num, to = emptyLabel
	public void clearBoard(int[][] board) {
		int counter = 0;
		for (int i = 0; i < board.length; i++) {
			for (int j = 0; j < board[0].length; j++) {
				boxValues[i][j] = 0;
				if (boxLabels[i][j] != emptyLabel) {
					frame.getContentPane().remove(boxLabels[i][j]);
					boxLabels[i][j] = emptyLabel;
					System.out.println(boxLabels[i][j] + " ");
					counter++;
				}

			}
			System.out.println(counter);

		}
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
		System.out.println(arg0);

		if ( (x < 0) || (x >= COLS) || (y < 0) || (y >= ROWS) )
			return;

		int key = arg0.getKeyCode();
		if (key == KeyEvent.VK_ENTER) {	
			JLabel emptyLabel = new JLabel("");
			int rx = X_GRID_OFFSET + x * BOX_WIDTH;
			int ry = Y_GRID_OFFSET + y * BOX_HEIGHT;
			Rectangle rect = new Rectangle(rx + 1, ry + 1, BOX_WIDTH - 3,
					BOX_HEIGHT - 3);
			emptyLabel.setBounds(rect);
			emptyLabel.setVerticalAlignment(JLabel.CENTER);
			emptyLabel.setHorizontalAlignment(JLabel.CENTER);

			//			frame.getContentPane().remove(boxLabels[x][y]);
			if (boxLabels[x][y] != emptyLabel) {
				frame.getContentPane().remove(boxLabels[x][y]);
				boxLabels[x][y] = emptyLabel;
			}

			frame.getContentPane().add(emptyLabel);

			boxLabels[x][y] = emptyLabel;
			boxValues[x][y] = 0;

			emptyLabel.setVisible(true);
			emptyLabel.setOpaque(true);
			emptyLabel.setForeground(Color.red);
			return;

		}
		char inputC = arg0.getKeyChar();
		if (Character.isDigit(inputC) && Character.getNumericValue(inputC) != 0){
			int inputNum = Character.getNumericValue(inputC);
			System.out.println("inputNum: " + inputNum);
			System.out.println("Just typed in box " + x + " by " + y);

			String label = String.valueOf(inputNum);
			JLabel numLabel = new JLabel(label);
			int rx = X_GRID_OFFSET + x * BOX_WIDTH;
			int ry = Y_GRID_OFFSET + y * BOX_HEIGHT;
			Rectangle rect = new Rectangle(rx + 1, ry + 1, BOX_WIDTH - 3,
					BOX_HEIGHT - 3);
			numLabel.setBounds(rect);
			numLabel.setVerticalAlignment(JLabel.CENTER);
			numLabel.setHorizontalAlignment(JLabel.CENTER);
			frame.getContentPane().remove(boxLabels[x][y]);
			frame.getContentPane().add(numLabel);
			numLabel.setVisible(true);
			numLabel.setOpaque(true);
			numLabel.setForeground(Color.red);

			if (checkRow(x, y, inputNum) && checkCol(x, y, inputNum) && checkSquare(x, y, inputNum)) {
				boxLabels[x][y] = numLabel;
				boxValues[x][y] = inputNum;	
			}
			else {
				frame.getContentPane().remove(numLabel);
				frame.getContentPane().add(boxLabels[x][y]);
				boxLabels[x][y].setVisible(true);
				boxLabels[x][y].setOpaque(true);
				boxLabels[x][y].setForeground(Color.red);
			}
			//repaint();
		}
	}

	private static boolean checkRow(int x, int y, int inputNum) {
		for (int col = 0; col < COLS; col++) {
			if (inputNum == boxValues[col][y]) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkCol(int x, int y, int inputNum) {
		for (int row = 0; row < ROWS; row++) {
			if (inputNum == boxValues[x][row]) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkSquare(int x, int y, int inputNum) {
		int leftX = x / 3 * 3; 
		int topY = y / 3 * 3;

		for (int col = leftX; col < leftX + 3; col++) {
			for (int row = topY; row < topY + 3; row++) {
				if (inputNum == boxValues[col][row]) {
					return false;
				}
			}
		}
		return true;
	}
}
