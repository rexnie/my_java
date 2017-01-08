package com.dc.misc;

import java.util.Stack;

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
	// 最终的结果矩阵:
	// 0 表示这个位置还没有填过数
	// i(i=1,2,...9)代表对应位置填的数字
	private int[][] mResultValue = new int[NN][NN];
	
	// 基于mResultValue存放可能的值:
	// [i][j][0]: i,j对应位置的可能的值，1个bit对应1个数字，使用bit 1--9
	// 0 代表失败了，没有可能性了， -1 代表这个位置已经填过数字了
	// [i][j][1]: 可能值的数量，即[i][j][0]中1的个数
	private int[][][] mPossibleValues = new int[NN][NN][2];
	
	// mResultValue中的值是否全填满了
	private boolean mIsAllFilled;
	
	// 存放下一个要猜的数字
	private Guess mNextGuess = new Guess(0, 0, BIT1_NN, NN);
	
	private boolean mIsPossibleValuesUpdated = false;
	
	private boolean mIsFail = false;
	
	private Stack<Guess> mGuessStack = new Stack();
	private Stack<Steps> mStepStack;
	
	private class Steps {
		int row;
		int col;
		int num;
		public Steps(int r, int c, int n) {
			if (r < 0 || r >= NN) {
				System.out.println("Steps: row error, r = " + r);
			}

			if (c < 0 || c >= NN) {
				System.out.println("Steps: col error, c = " + c);
			}

			if (n < 1 || n > NN) {
				System.out.println("Steps: n error, n =" + n);
			}
			
			row = r;
			col = c;
			num = n;
		}
		
		public String toString() {
			return "Steps: row=" + row + ",col=" + col + ",num=" + num + "\n";
		}
	}
	
	private class Guess {
		int row;
		int col;
		int possible;
		int bitCount;
		Stack<Steps> stepStack;

		public Guess(int r, int c, int n, int bc) {
			if (r < 0 || r >= NN) {
				System.out.println("Guess: row error, r = " + r);
			}

			if (c < 0 || c >= NN) {
				System.out.println("Guess: col error, c = " + c);
			}

			if (n < 0 || n > BIT1_NN) {
				System.out.println("Guess: n error, n =" + n);
			}
			
			if (bc < 1 || bc > NN) {
				System.out.println("Guess: bc error, bc =" + bc);
			}
			
			row = r;
			col = c;
			possible = n;
			bitCount = bc;
			stepStack = new Stack<Steps>();
		}
		
		public Stack<Steps> getStepStack() {
			return stepStack;
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Guess dump:\n");
			sb.append("row=" + row + ",col=" + col + ",possible=0x" + Integer.toString(possible, 16)
					+ ",bitCount=" + bitCount);
			sb.append(",stepStack size=" + stepStack.size() + "\n");
			
			sb.append(stepStack);
			return sb.toString();
		}
	}

	public void mainLoop() {
		scanPossibleValuesWithoutGuess();

		while(true) {
			scanPossibleValues();
			if (mIsFail) { // 这个路径走不通
				dumpInternalValues("mainLoop: mIsFail=true");

				//resumeResultValue(mStepStack); // 恢复上次猜数字之后的步骤
				mStepStack = null;
				
				Guess top;
				while(!mGuessStack.isEmpty() && (mGuessStack.peek().bitCount == 0)) {
					top = mGuessStack.pop();
					resumeResultValue(top.stepStack);
				}
				
				if (mGuessStack.isEmpty()) {
					System.out.println("mainLoop: mGuessStack is empty");
					break;
				} else {
				
					top = mGuessStack.peek();
					mStepStack = top.stepStack;

					resumeResultValue(mStepStack);
					// 把上次猜数字时，入栈的可能值出栈
					int numGuess = number2BitNumber(top.possible);
					mResultValue[top.row][top.col] = numGuess;
					top.possible &= ~ (1 << numGuess);
					top.bitCount -= 1;
					
					mStepStack.push(new Steps(top.row, top.col, numGuess));
					
					resetPossibleValues();
					mIsFail = false;
				}
	
			} else if (mIsAllFilled) {
				System.out.println("mainLoop: All Filled, Congratulations");
				break;
			} else if (mNextGuess.bitCount < NN) {
				// 要猜数字，从mNextGuess.possible里选最小的先猜
				int numGuess = number2BitNumber(mNextGuess.possible);
				if (numGuess == 0) {
					System.out.println("scanPossibleValues: error,numGuess=" + numGuess);
					break;
				}
				System.out.printf("mainLoop: guess, row=%d,col=%d,num=%d\n",
						mNextGuess.row, mNextGuess.col, numGuess);
				
				// mNextGuess.possible里除去最小的，剩下的入栈
				mGuessStack.push(new Guess(mNextGuess.row, mNextGuess.col,
						mNextGuess.possible & (~ (1 << numGuess)), mNextGuess.bitCount - 1));
				mStepStack = mGuessStack.peek().stepStack;
				// 新建一个mStepsTop，给这次猜数字后放Steps,如果猜错了，这些Steps可恢复
				mStepStack.push(new Steps(mNextGuess.row, mNextGuess.col, numGuess));
				
				mResultValue[mNextGuess.row][mNextGuess.col] = numGuess;
				clearBits(mNextGuess.row, mNextGuess.col, numGuess);
			}
		} //while
		
		dumpInternalValues("mainLoop: end");
	}
	
	public void scanPossibleValues() {
		do {
			mIsPossibleValuesUpdated = false;
			mIsAllFilled = true;
			mNextGuess.bitCount = NN;
			dumpInternalValues("scanPossibleValues: do");
			for (int i = 0; i < NN; i++) {
				for (int j = 0; j < NN; j++) {
					//System.out.printf("scanPossibleValues: i=%d,j=%d\n", i, j);
					if (mPossibleValues[i][j][0] >= 0) { //忽略那些已经填过数字的位置
						mIsAllFilled = false;
						if (mPossibleValues[i][j][1] == 0) { //此位置没有可以填的数字，失败了
							System.out.printf("scanPossibleValues: fail, i=%d,j=%d\n",i,j);
							dumpInternalValues("scanPossibleValues: fail");
							System.out.println(mGuessStack);

							mIsFail = true;
							return;
						} else if (mPossibleValues[i][j][1] == 1) { //此位置只有一个数字可以填
								mResultValue[i][j] = number2BitNumber(mPossibleValues[i][j][0]);
								
								// 入栈，保证失败时，mResultValue[i][j]的值可恢复
								mStepStack.push(new Steps(i, j, mResultValue[i][j]));
								clearBits(i, j, mResultValue[i][j]);
						} else if (mPossibleValues[i][j][1] < mNextGuess.bitCount) {
							// 扫描的过程中，也保存可能需要猜的位置
							mNextGuess.possible = mPossibleValues[i][j][0];
							mNextGuess.row = i;
							mNextGuess.col = j;
							mNextGuess.bitCount = mPossibleValues[i][j][1];
						}
					}
				}
			}
			//System.out.println("scanPossibleValues: mIsPossibleValuesUpdated="
			//		+ mIsPossibleValuesUpdated);
			dumpInternalValues("scanPossibleValues: after do");
		} while (mIsPossibleValuesUpdated == true);
	}
	
	private void resumeResultValue(Stack<Steps> stack) {
		System.out.println("********resumeResultValue\n");
		while(!stack.isEmpty()) {
			Steps top = stack.pop();
			System.out.printf("resumeResultValue: row=%d, col=%d, num=%d\n", top.row, top.col, top.num);
			mResultValue[top.row][top.col] = 0;
		}
	}
	
	/**
	 * 找出num中从右起第一个为1的位置，并将其转化为1-9的数字
	 * 如 num=0x110时，返回4
	 * @param num
	 * @return
	 */
	private int number2BitNumber(int num) {
		switch(Integer.lowestOneBit(num)) {
		case 0:
			return 0;
		case 2:
			return 1;
		case 4:
			return 2;
		case 8:
			return 3;
		case 16:
			return 4;
		case 32:
			return 5;
		case 64:
			return 6;
		case 128:
			return 7;
		case 256:
			return 8;
		case 512:
			return 9;
		default:
			System.out.println("error number, num=" + num);
			return 0;
		}
	}
	
	public void checkResult() {
		if (mIsFail) {
			System.out.println("fail");
			return;
		}
		
		// TODO 优化
		for (int i = 0; i < NN; i++) {
			for (int j = 0; j < NN; j++){
				if (mPossibleValues[i][j][0] == 0) {
					mIsFail = true;
					return;
				}
			}
		}
	}
	
	/**
	 * 还没有开始猜之前，找出确定的值，直接填入
	 * 我们假设所有的问题都有解
	 */
	public void scanPossibleValuesWithoutGuess() {
		do {
			mIsPossibleValuesUpdated = false;
			for (int i = 0; i < NN; i++) {
				for (int j = 0; j < NN; j++) {
					//System.out.printf("scanPossibleValuesStackless:i=%d,j=%d\n", i, j);
					if (mPossibleValues[i][j][1] == 1) {
						mResultValue[i][j] = mPossibleValues[i][j][0];
						clearBits(i, j, mResultValue[i][j]);
					}
				}
			}
			//System.out.println("scanPossibleValuesStackless:mIsPossibleValuesUpdated="
			//		+ mIsPossibleValuesUpdated);
		} while (mIsPossibleValuesUpdated == true);
	}
	
	public void setInitialValue() {
		/**
		 * 超难题目1
     0  1  2  3  4  5  6  7  8
----------------------------------------
 0|                    8     7
 1|        3     7     1      
 2|  9        6           4   
 3|     1           8  4  7   
 4|  8        4     5        3
 5|     6  4  7           1   
 6|     2           1        4
 7|        8     2     9      
 8|  1     9                  
 
 答案：
 0|  6  5  2  1  9  4  8  3  7
 1|  4  8  3  5  7  2  1  9  6
 2|  9  7  1  6  8  3  2  4  5
 3|  3  1  5  2  6  8  4  7  9
 4|  8  9  7  4  1  5  6  2  3
 5|  2  6  4  7  3  9  5  1  8
 6|  7  2  6  9  5  1  3  8  4
 7|  5  4  8  3  2  7  9  6  1
 8|  1  3  9  8  4  6  7  5  2
		 *
		 */
/*
		mInitialValue[0][6] = 8;
		mInitialValue[0][8] = 7;

		mInitialValue[1][2] = 3;
		mInitialValue[1][4] = 7;
		mInitialValue[1][6] = 1;

		mInitialValue[2][0] = 9;
		mInitialValue[2][3] = 6;
		mInitialValue[2][7] = 4;

		mInitialValue[3][1] = 1;
		mInitialValue[3][5] = 8;
		mInitialValue[3][6] = 4;
		mInitialValue[3][7] = 7;

		mInitialValue[4][0] = 8;
		mInitialValue[4][3] = 4;
		mInitialValue[4][5] = 5;
		mInitialValue[4][8] = 3;

		mInitialValue[5][1] = 6;
		mInitialValue[5][2] = 4;
		mInitialValue[5][3] = 7;
		mInitialValue[5][7] = 1;

		mInitialValue[6][1] = 2;
		mInitialValue[6][5] = 1;
		mInitialValue[6][8] = 4;

		mInitialValue[7][2] = 8;
		mInitialValue[7][4] = 2;
		mInitialValue[7][6] = 9;

		mInitialValue[8][0] = 1;
		mInitialValue[8][2] = 9;
*/

/**
   超难题目2
 		
     0  1  2  3  4  5  6  7  8
----------------------------------------
 0|                    8     7
 1|        3     7     1      
 2|  9        6           4   
 3|     1           8  4  7   
 4|  8        4     5        3
 5|     6  4  7           1   
 6|     2           1        4
 7|        8     2     9      
 8|  1     9                   		
 
 答案：
 
     0  1  2  3  4  5  6  7  8
----------------------------------------
 0|  6  5  2  1  9  4  8  3  7
 1|  4  8  3  5  7  2  1  9  6
 2|  9  7  1  6  8  3  2  4  5
 3|  3  1  5  2  6  8  4  7  9
 4|  8  9  7  4  1  5  6  2  3
 5|  2  6  4  7  3  9  5  1  8
 6|  7  2  6  9  5  1  3  8  4
 7|  5  4  8  3  2  7  9  6  1
 8|  1  3  9  8  4  6  7  5  2
 */
		mInitialValue[0][6] = 1;
		mInitialValue[0][7] = 9;

		mInitialValue[1][2] = 1;
		mInitialValue[1][7] = 7;
		mInitialValue[1][8] = 8;

		mInitialValue[2][0] = 6;
		mInitialValue[2][3] = 8;
		mInitialValue[2][5] = 5;

		mInitialValue[3][2] = 8;
		mInitialValue[3][7] = 2;
		mInitialValue[3][8] = 6;

		mInitialValue[4][3] = 3;
		mInitialValue[4][4] = 7;
		mInitialValue[4][5] = 2;

		mInitialValue[5][0] = 2;
		mInitialValue[5][1] = 5;
		mInitialValue[5][6] = 9;

		mInitialValue[6][3] = 6;
		mInitialValue[6][5] = 7;
		mInitialValue[6][8] = 4;

		mInitialValue[7][0] = 7;
		mInitialValue[7][1] = 4;
		mInitialValue[7][6] = 5;

		mInitialValue[8][1] = 3;
		mInitialValue[8][2] = 6;

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
		
		//dumpPossibleValues();
		
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
		
		mPossibleValues[row][col][0] = -1;
		mPossibleValues[row][col][1] = 0;
		mIsPossibleValuesUpdated = true;

		// clear row
		for (int j = 0; j < NN; j++) {
			clearBit(row, j, number);
		}

		// clear column
		for (int i = 0; i < NN; i++) {
			clearBit(i, col, number);
		}

		// clear 小矩阵
		int rowStart = row / N * N;
		int colStart = col / N * N;
		//System.out.printf("clearBits: row=%d, col=%d, rowStart=%d, colStart=%d\n",
		//		row, col, rowStart, colStart);
		for (int i = rowStart; i < N + rowStart; i++) {
			if (i != row) { // 忽略row行
				for (int j = colStart; j < N + colStart; j++) {
					if (j != col) { // 忽略col列
						clearBit(i, j, number);
					}
				}
			}
		}
	}

	/**
	 * 清除mPossibleValues中对应的number bit
	 * @param row
	 * @param col
	 * @param number
	 */
	private void clearBit(int row, int col, int number) {
		if ((mPossibleValues[row][col][0] > 0) && //没有填数字
				(((mPossibleValues[row][col][0] >>> number) & 1) == 1)) { //对应的number位为1
		
			if (mPossibleValues[row][col][1] <= 0) {
				System.out.printf("clearBit: possible value error,row=%d, col=%d, 0=0x%x, 1=%d\n",
						row, col, mPossibleValues[row][col][0], mPossibleValues[row][col][1]);
			}
			mPossibleValues[row][col][0] &= ~(1 << number); // 清除mPossibleValues中对应的number bit
			mPossibleValues[row][col][1] -= 1; // 可能的性减1
			mIsPossibleValuesUpdated = true;
		}
	}

	private void dumpPossibleValues() {
		System.out.println("********* dump mPossibleValues *********\n");
		
		for (int i = 0; i < NN; i++) {
			for (int j = 0; j < NN; j++) {
				dumpPossibleValue(mPossibleValues[i][j][0], mPossibleValues[i][j][1]);
			}
			System.out.println("");
		}
		System.out.println("\n");
	}
	
	private void dumpPossibleValue(int possibleValueBits, int bitNumber) {
		if (possibleValueBits < 0) {
			System.out.printf("%d#%-11d", bitNumber, -1);
			return;
		}
		
		int space=0;
		System.out.printf("%d#", bitNumber);
		for (int i = 1; i <= NN; i++) {
			if (((possibleValueBits >>> i) & 1) == 0) {
				space++; //没有的数字不打印出来，统计个数方便输出对齐
			} else {
				System.out.printf("%d", i);
			}
		}
		while (space-- != 0) {
			System.out.printf(" ");
		}
		System.out.printf("  ");
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
				if (a[i][j] != 0) {
					System.out.printf("%3d", a[i][j]);
				} else {
					System.out.printf("   ");
				}
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
	
	public void dumpInternalValues(String msg) {
		System.out.println("*********** " + msg);
		dumpResultValue();
		dumpPossibleValues();
	}
	
	public void test() {
		int x = 0x0;
		
		System.out.printf("y=%d",Integer.bitCount(x));
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("in main");
		Sudoku sudoku = new Sudoku();
		sudoku.setInitialValue();
		//sudoku.copyInitialValue();
		// sudoku.dumpInitialValue();
		//sudoku.dumpResultValue();
		
		sudoku.resetPossibleValues();
		//sudoku.dumpInternalValues("main()");
		
		sudoku.mainLoop();
		
		//sudoku.test();
	}

}
