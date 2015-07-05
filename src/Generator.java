import java.util.ArrayList;

public class Generator {

	public Generator() {
		
	}

	public static void remove(int[][] board) {
		ArrayList<Integer> randPositions = getRandPositions();
		int index = 0;
		while (index < 81) {
			System.out.println(randPositions);
			int boardPos = randPositions.remove(index);
			// copy of board
			int[][] test = new int[9][9];
			copy2DArray(test, board);
			test[(boardPos - 1) % 9][(boardPos - 1) / 9] = 0;
			Solver solver = new Solver();
			if (!solver.solve(test)) {
				randPositions.add(index, boardPos);
				index++;
			} else {
				board[(boardPos - 1) % 9][(boardPos - 1) / 9] = 0;
				System.out.println("REMOVED:");
				for (int i = 0; i < 9; i++) {
					for (int j = 0; j < 9; j++) {
						System.out.print(board[j][i] + " ");
					}
					System.out.println();
				}
			}
			if (solver.hasUniqueSolution(board)) {
				break;
			}
			System.out.println(index);
		}
	}

	public static int[][] fillBoard() {
		int[][] board = new int[9][9];
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				board[i][j] = 0;		
			}
		}
		Solver solver = new Solver();
		solver.solve(board);
		return board;
	}
	
	private static void copy2DArray(int[][] test, int[][] board) {
		for (int i = 0; i < 9; i++) {
			for (int j = 0; j < 9; j++) {
				test[i][j] = board[i][j];
			}
		}
	}
	
	private static ArrayList<Integer> getRandPositions() {
		ArrayList<Integer> randPositions = new ArrayList<Integer>();
		Randp rand = new Randp(81);
		for(int i = 0; i < 81; i++) {
			randPositions.add(rand.nextInt());
		}
		return randPositions;
	}
}