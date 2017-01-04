package com.dc.misc;

/**
 * 使用栈来解决数独问题
 * 
 * @author niedaocai
 *
 */
public class Sudoku {
	private static final int N = 3;
	private static final int NN = N * N;

	private int[][] mInitialValue = new int[NN][NN];
	private int[][] mResultValue = new int[NN][NN];

	public void setInitialValue() {
		mInitialValue[0][1] = 8;
		mInitialValue[0][6] = 4;

		mInitialValue[1][1] = 9;
		mInitialValue[1][2] = 5;
		mInitialValue[1][3] = 7;
		mInitialValue[1][4] = 2;
		mInitialValue[1][5] = 4;
		mInitialValue[1][6] = 6;
		mInitialValue[1][8] = 8;

		mInitialValue[2][1] = 4;
		mInitialValue[2][3] = 3;
		mInitialValue[2][5] = 9;
		mInitialValue[2][6] = 7;

		mInitialValue[3][0] = 9;
		mInitialValue[3][1] = 1;
		mInitialValue[3][3] = 4;
		mInitialValue[3][4] = 7;
		mInitialValue[3][8] = 2;

		mInitialValue[4][3] = 2;
		mInitialValue[4][5] = 8;

		mInitialValue[5][0] = 8;
		mInitialValue[5][4] = 9;
		mInitialValue[5][5] = 1;
		mInitialValue[5][7] = 4;
		mInitialValue[5][8] = 7;

		mInitialValue[6][2] = 9;
		mInitialValue[6][3] = 1;
		mInitialValue[6][5] = 7;
		mInitialValue[6][7] = 8;

		mInitialValue[7][0] = 1;
		mInitialValue[7][2] = 2;
		mInitialValue[7][3] = 8;
		mInitialValue[7][4] = 5;
		mInitialValue[7][5] = 3;
		mInitialValue[7][6] = 9;
		mInitialValue[7][7] = 7;

		mInitialValue[8][2] = 8;
		mInitialValue[8][7] = 6;
	}

	private void dumpArray(int[][] a) {
		// 打印出列号
		System.out.printf("   ");
		for (int i = 0; i < NN; i++) {
			System.out.printf("%3d", i);
		}

		System.out.printf("\n----------------------------------------\n");

		for (int i = 0; i < NN; i++) {
			for (int j = 0; j < NN; j++) {
				// 打印出行号
				if (j == 0) {
					System.out.printf("%2d|", i);
				}
				System.out.printf("%3d", a[i][j]);
			}
			System.out.println();
		}
		System.out.println("\n");
	}

	public void dumpInitialValue() {
		System.out.println("********* dump mInitialValue *********");
		dumpArray(mInitialValue);
	}

	public void dumpResultValue() {
		System.out.println("********* dump mResultValue *********");
		dumpArray(mResultValue);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Sudoku sudoku = new Sudoku();
		sudoku.setInitialValue();
		sudoku.dumpInitialValue();
		sudoku.dumpResultValue();
	}

}
