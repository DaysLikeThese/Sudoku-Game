import java.util.ArrayList;


public class Solver {
	
	private static int[][] board = new int[9][9]; 

	public Solver() {
		
	}
	
	public boolean solve(int[][] board) {
		this.board = board;
		int x = 0;
		int y = 0;
		
		if (isDone(x, y)) return true;
		x = getXCoord();
		y = getYCoord();
		
		Randp randNums = new Randp(9);		// inputs random num from 1-9 bc also used in generator to generate board
		for (int i = 0; i < 9; i++) {
			int num = randNums.nextInt();
			if (isValid(x, y, num)) {
				board[x][y] = num;
				if (solve(board)) return true;
				board[x][y] = 0;
			}
		}
		return false;		// backtracks
	}
	
	public boolean hasUniqueSolution(int[][] board) {
		int[][] oldSolvedBoard = new int[9][9];
		int[][] newSolvedBoard = new int[9][9];
		
		copy2DArray(oldSolvedBoard, board);
		solve(oldSolvedBoard);		// oldSolvedBoard now contains a solution for board
		
		copy2DArray(newSolvedBoard, board);
		solve(newSolvedBoard);		// newSolvedBoard now contains a solution for board
		int counter = 0;
		while (counter < 10) {
			if (boardsAreEqualAndNoEmpties(oldSolvedBoard, newSolvedBoard)) {
				return false;
			}
			copy2DArray(oldSolvedBoard, newSolvedBoard); // oldSolvedBoard now contains newSolvedBoard
			
			copy2DArray(newSolvedBoard, board);
			solve(newSolvedBoard);	// newSolvedBoard now contains a solution for board
			counter++;
		}
		return true;
	}
	
	private static void copy2DArray(int[][] empty, int[][] filled) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				empty[i][j] = filled[i][j];
			}
		}
	}
	
	private boolean boardsAreEqualAndNoEmpties(int[][] oldSolvedBoard, int[][] newSolvedBoard) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				if ((oldSolvedBoard[i][j] != newSolvedBoard[i][j]) || oldSolvedBoard[i][j] == 0 || newSolvedBoard[i][j] ==0) {
					return false;
				}
			}
		}
		return true;
	}
	
	int currentX = 0;
	int currentY = 0;
	private boolean isDone(Integer xCoord, Integer yCoord) {
		for (yCoord = 0; yCoord < 9; yCoord++) {
			for (xCoord = 0; xCoord < 9; xCoord++) {
				if (board[xCoord][yCoord] == 0) {
					currentX = xCoord;
					currentY = yCoord;
					return false;
				}
			}
		}
		return true;
	}
	
	private int getXCoord() {
		return currentX;
	}
	
	private int getYCoord() {
		return currentY;
	}
	
	public boolean isValid(int xCoord, int yCoord, int num) {
		return (rowIsValid(xCoord, num) && colIsValid(yCoord, num) && blockIsValid(xCoord, yCoord, num));
	}
	
	private boolean rowIsValid(int xCoord, int num) {
		for (int i = 0; i < board.length; i++) {
			if (board[xCoord][i] == num) return false;
		}
		return true;
	}
	
	private boolean colIsValid(int yCoord, int num) {
		for (int i = 0; i < board[0].length; i++) {
			if (board[i][yCoord] == num) return false;
		}
		return true;
	}
	
	private boolean blockIsValid(int xCoord, int yCoord, int num) {
		int edgeOfXCoord = xCoord - xCoord % 3;
		int edgeOfYCoord = yCoord - yCoord % 3;
		
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (board[edgeOfXCoord + i][edgeOfYCoord + j] == num) return false;
			}
		}
		return true;
	}
}
