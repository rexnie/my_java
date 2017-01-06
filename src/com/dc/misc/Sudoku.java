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
	private static final int BIT1_NN = ((1 << (NN + 1)) - 2); // 0x3fe
	private static final int IGNORE_POSSIBLE = -1;

	private int[][] mInitialValue = new int[NN][NN];
	private int[][] mResultValue = new int[NN][NN];
	// 基于mResultValue存放可能的值:
	// [i][j][0]: i,j对应位置的可能的值，1个bit对应1个数字，使用bit 1--9
	// 0 代表失败了，没有可能性了， -1 代表这个位置是确定的值
	// [i][j][1]: 可能值的数量，即[i][j][0]中1的个数
	private int[][][] mPossibleValues = new int[NN][NN][2];

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

		copyInitialValue();
	}

	/**
	 * 二维数组的深拷贝 java 只有一维数组，多维数组是使用一维数组的引用实现的 所以对多维数组的深拷贝，需要对各维数进行拷贝
	 */
	private void copyInitialValue() {
		for (int i = 0; i < mInitialValue.length; i++) {
			System.arraycopy(mInitialValue[i], 0, mResultValue[i], 0,
					mInitialValue[i].length);
			// mResultValue[i] = mInitialValue[i].clone(); //OK
		}
	}

	/**
	 * 根据当前mResultValue的值，填充mPossibleValues
	 */
	public void resetPossibleValues() {
		for (int i = 0; i < NN; i++) {
			for (int j = 0; j < NN; j++) {
				if (mResultValue[i][j] != 0) {
					mPossibleValues[i][j][0] = IGNORE_POSSIBLE;
					mPossibleValues[i][j][1] = 0;
				} else {
					mPossibleValues[i][j][0] = BIT1_NN;
					mPossibleValues[i][j][1] = NN;
				}
			}
		}

		for (int i = 0; i < NN; i++) {
			for (int j = 0; j < NN; j++) {
				if (mResultValue[i][j] != 0) {
					clearBits(i, j, mResultValue[i][j]);
				}
			}
		}
	}

	private void clearBits(int row, int col, int number) {
		if (row < 0 || row >= NN) {
			System.out.println("clearBits: row error, row = " + row);
		}

		if (col < 0 || col >= NN) {
			System.out.println("clearBits: col error, col = " + col);
		}

		if (number < 1 || number > NN) {
			System.out.println("clearBits: number error, number =" + number);
		}

		// clear row
		for (int j = 0; j < NN; j++) {
			clearBit(row, j, number);
		}

		// clear col
		for (int i = 0; i < NN; i++) {
			clearBit(i, col, number);
		}

		// clear 小矩阵
		int colStart = col / N * N;
		for (int i = row / N * N; i < N; i++) {
			if (i != row) { // ignore row line
				for (int j = colStart; j < N; j++) {
					if (j != col) { // ignore col column
						clearBit(i, j, number);
					}
				}
			}
		}
	}

	private void clearBit(int row, int col, int number) {
		if (mPossibleValues[row][col][0] > 0) {
			if (mPossibleValues[row][col][1] <= 0) {
				System.out.println("clearBit: possible value error");
			}
			mPossibleValues[row][col][0] &= ~(1 << number);
			mPossibleValues[row][col][1] -= 1;
		}
	}

	private void dumpPossibleValues() {
		for (int i = 0; i < NN; i++) {
			for (int j = 0; j < NN; j++) {
				if ((j + 1) % N == 0) {
					System.out.println();
				}
				System.out.printf("%2d", mPossibleValues[i][j][0]);
			}
		}
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
		sudoku.copyInitialValue();
		// sudoku.dumpInitialValue();
		sudoku.dumpResultValue();
		sudoku.resetPossibleValues();
		sudoku.dumpPossibleValues();
	}

}
