package finalproject;

import java.util.*;
import java.io.*;


public class ChessSudoku
{
	/* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.  For 
	 * a standard Sudoku puzzle, SIZE is 3 and N is 9. 
	 */
	public int SIZE, N;

	/* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
	 * not yet been revealed are stored as 0. 
	 */
	public int grid[][];

	/* Booleans indicating whether of not one or more of the chess rules should be 
	 * applied to this Sudoku. 
	 */
	public boolean knightRule;
	public boolean kingRule;
	public boolean queenRule;
	private boolean solutionFound;

	
	// Field that stores the same Sudoku puzzle solved in all possible ways
	public HashSet<ChessSudoku> solutions = new HashSet<ChessSudoku>();


	/* The solve() method should remove all the unknown characters ('x') in the grid
	 * and replace them with the numbers in the correct range that satisfy the constraints
	 * of the Sudoku puzzle. If true is provided as input, the method should find finds ALL 
	 * possible solutions and store them in the field named solutions. */
	//	CHANGE TO PRIVATE**********************************************!!!!!!!!!!!!!!!!!!!!!!!!!

	public boolean possibleEntry(int x,int y,int n){
		for(int i=0;i<N;i++) {
			if (grid[x][i] == n) {
				return false;
			}
		}
		for(int i=0;i<N;i++){
			if (grid[i][y]==n){
				return false;
			}
		}
		int xFloor= (x/SIZE)*SIZE;
		int yFloor= (y/SIZE)*SIZE;

		for(int i=0;i<SIZE;i++){
			for(int j=0;j<SIZE;j++){
				if(grid[xFloor+i][yFloor+j]==n){

					return false;
				}
			}
		}

		if(knightRule){
			// All possible moves of a knight
			int X[] = { 2, 1, -1, -2, -2, -1, 1, 2 };
			int Y[] = { 1, 2, 2, 1, -1, -2, -2, -1 };
			// Check if each possible move is valid or not
			for (int i = 0; i < 8; i++) {
				// Position of knight after move
				int checkx = x + X[i];
				int checky = y + Y[i];
				// count valid moves
				if (checkx >= 0 && checky >= 0 && checkx < N && checky < N && grid[checkx][checky] == n)
					return false;
			}
		}

		if(kingRule){
			int xStart=x-1;
			int yStart=y-1;

			for(int i=0;i<3;i++){
				for(int j=0;j<3;j++){
					if((x-1)+i>=0 && (y-1)+j>=0 && (x-1)+i<N && (y-1)+j<N && grid[(x-1)+i][(y-1)+j]==n ){
						if(!(j==1 && i==1)){
							return false;
						}

					}
				}
			}

		}

		if(n==N && queenRule){
			int xStart=x-(N-1);
			int yStart=y-(N-1);
			for(int i=0;i<(N*2);i++){

				if(xStart+i>=0 && yStart+i>=0 && xStart+i<N && yStart+i<N && grid[xStart+i][yStart+i]==N && !(xStart+i==x && yStart+i==y)){
					return false;
				}
			}
			xStart=x+(N-1);
			yStart=y-(N-1);
			for(int i=0;i<(N*2);i++){
				System.out.println("po:"+(xStart-i)+" "+(yStart+i));
				if(xStart-i>=0 && yStart+i>=0 && xStart-i<N && yStart+i<N && grid[xStart-i][yStart+i]==N && !(xStart-i==x && yStart+i==y)){
					return false;
				}
			}

		}


		return true;
	}

	private int[] validEntries(int x,int y){
		//check colomn
		int[] containsNumber = new int[N];
		//check row
		for(int i=0;i<N;i++){
			if(grid[x][i]!=0){
				containsNumber[grid[x][i]-1]=1;
			}
			if(grid[i][y]!=0){
				containsNumber[grid[i][y]-1]=1;;
			}

		}
		//check box
		int xFloor= (x/SIZE)*SIZE;
		int yFloor= (y/SIZE)*SIZE;
		for(int i=0;i<SIZE;i++){
			for(int j=0;j<SIZE;j++){
				if(grid[xFloor+i][yFloor+j]!=0) {
					containsNumber[grid[xFloor+i][yFloor+j]-1]=1;
				}
			}
		}
		//knight
		if(knightRule){
			// All possible moves of a knight
			int X[] = { 2, 1, -1, -2, -2, -1, 1, 2 };
			int Y[] = { 1, 2, 2, 1, -1, -2, -2, -1 };
			// Check if each possible move is valid or not
			for (int i = 0; i < 8; i++) {
				// Position of knight after move
				int checkx = x + X[i];
				int checky = y + Y[i];
				// count valid moves
				if (checkx >= 0 && checky >= 0 && checkx < N && checky < N && grid[checkx][checky] != 0){
					containsNumber[grid[checkx][checky]-1]=1;
				}

			}
		}
		//king
		if(kingRule){
			int xStart=x-1;
			int yStart=y-1;

			for(int i=0;i<3;i++){
				for(int j=0;j<3;j++){
					if((x-1)+i>=0 && (y-1)+j>=0 && (x-1)+i<N && (y-1)+j<N && grid[(x-1)+i][(y-1)+j]!=0 ){
						containsNumber[grid[(x-1)+i][(y-1)+j]-1]=1;
					}
				}
			}

		}
		//queen
		if(queenRule){
			int xStart=x-(N-1);
			int yStart=y-(N-1);
			for(int i=0;i<(N*2);i++){
				if(xStart+i>=0 && yStart+i>=0 && xStart+i<N && yStart+i<N && grid[xStart+i][yStart+i]==N){
					containsNumber[grid[xStart+i][yStart+i]-1]=1;
				}
			}
			xStart=x+(N-1);
			yStart=y-(N-1);
			for(int i=0;i<(N*2);i++){
				//System.out.println("po:"+(xStart-i)+" "+(yStart+i));
				if(xStart-i>=0 && yStart+i>=0 && xStart-i<N && yStart+i<N && grid[xStart-i][yStart+i]==N){
					containsNumber[grid[xStart-i][yStart+i]-1]=1;
				}
			}
		}
			int size=0;
			int[] validNumbersBigArray=new int[N];
			for(int i=0;i<N;i++){
				if(containsNumber[i]==0){
					validNumbersBigArray[size]=i+1;
					size++;
				}
			}
			int[] validNumbers= new int[size];
			for(int i=0;i<size;i++){
				validNumbers[i]=validNumbersBigArray[i];
			}

			return validNumbers;

	}


	private void solveR(boolean allSolutions){
		solutionFound=false;
		for(int y=0;y<N;y++){
			for(int x=0;x<N;x++){
				if(grid[x][y]==0) {
					int[] possible = this.validEntries(x, y);
					int n=0;
					while (!solutionFound && n< possible.length) {
						//System.out.println(Arrays.toString(possible));
						grid[x][y] = possible[n];
						solveR(allSolutions);
							if (!solutionFound) {
								grid[x][y] = 0;
							}
						n++;
					}
					return;
				}
			}
		}

		if(allSolutions){
			ChessSudoku newSolution= new ChessSudoku(SIZE);

			for(int x=0;x<newSolution.N;x++){
				for(int y=0;y<newSolution.N;y++){
					newSolution.grid[x][y]=this.grid[x][y];
				}
			}
			solutions.add(newSolution);
		}else{
			solutionFound=true;
		}
	};


	public void solve(boolean allSolutions) {
		double start=System.nanoTime();
		solveR(allSolutions);
		double finish=System.nanoTime();
		double time=(finish-start)/1000000000;
		System.out.println("Time to solve:"+time+"seconds");


		if(allSolutions){
			for(ChessSudoku solveState:solutions){
				for(int x=0;x<solveState.N;x++){
					for(int y=0;y<solveState.N;y++){
						this.grid[x][y]=solveState.grid[x][y];
					}
				}
				break;
			}
		}
	}

	

	/*****************************************************************************/
	/* NOTE: YOU SHOULD NOT HAVE TO MODIFY ANY OF THE METHODS BELOW THIS LINE. */
	/*****************************************************************************/

	/* Default constructor.  This will initialize all positions to the default 0
	 * value.  Use the read() function to load the Sudoku puzzle from a file or
	 * the standard input. */
	public ChessSudoku( int size ) {
		SIZE = size;
		N = size*size;

		grid = new int[N][N];
		for( int i = 0; i < N; i++ ) 
			for( int j = 0; j < N; j++ ) 
				grid[i][j] = 0;
	}


	/* readInteger is a helper function for the reading of the input file.  It reads
	 * words until it finds one that represents an integer. For convenience, it will also
	 * recognize the string "x" as equivalent to "0". */
	static int readInteger( InputStream in ) throws Exception {
		int result = 0;
		boolean success = false;

		while( !success ) {
			String word = readWord( in );

			try {
				result = Integer.parseInt( word );
				success = true;
			} catch( Exception e ) {
				// Convert 'x' words into 0's
				if( word.compareTo("x") == 0 ) {
					result = 0;
					success = true;
				}
				// Ignore all other words that are not integers
			}
		}

		return result;
	}


	/* readWord is a helper function that reads a word separated by white space. */
	static String readWord( InputStream in ) throws Exception {
		StringBuffer result = new StringBuffer();
		int currentChar = in.read();
		String whiteSpace = " \t\r\n";
		// Ignore any leading white space
		while( whiteSpace.indexOf(currentChar) > -1 ) {
			currentChar = in.read();
		}

		// Read all characters until you reach white space
		while( whiteSpace.indexOf(currentChar) == -1 ) {
			result.append( (char) currentChar );
			currentChar = in.read();
		}
		return result.toString();
	}


	/* This function reads a Sudoku puzzle from the input stream in.  The Sudoku
	 * grid is filled in one row at at time, from left to right.  All non-valid
	 * characters are ignored by this function and may be used in the Sudoku file
	 * to increase its legibility. */
	public void read( InputStream in ) throws Exception {
		for( int i = 0; i < N; i++ ) {
			for( int j = 0; j < N; j++ ) {
				grid[i][j] = readInteger( in );
			}
		}
	}


	/* Helper function for the printing of Sudoku puzzle.  This function will print
	 * out text, preceded by enough ' ' characters to make sure that the printint out
	 * takes at least width characters.  */
	void printFixedWidth( String text, int width ) {
		for( int i = 0; i < width - text.length(); i++ )
			System.out.print( " " );
		System.out.print( text );
	}


	/* The print() function outputs the Sudoku grid to the standard output, using
	 * a bit of extra formatting to make the result clearly readable. */
	public void print() {
		// Compute the number of digits necessary to print out each number in the Sudoku puzzle
		int digits = (int) Math.floor(Math.log(N) / Math.log(10)) + 1;

		// Create a dashed line to separate the boxes 
		int lineLength = (digits + 1) * N + 2 * SIZE - 3;
		StringBuffer line = new StringBuffer();
		for( int lineInit = 0; lineInit < lineLength; lineInit++ )
			line.append('-');

		// Go through the grid, printing out its values separated by spaces
		for( int i = 0; i < N; i++ ) {
			for( int j = 0; j < N; j++ ) {
				printFixedWidth( String.valueOf( grid[i][j] ), digits );
				// Print the vertical lines between boxes 
				if( (j < N-1) && ((j+1) % SIZE == 0) )
					System.out.print( " |" );
				System.out.print( " " );
			}
			System.out.println();

			// Print the horizontal line between boxes
			if( (i < N-1) && ((i+1) % SIZE == 0) )
				System.out.println( line.toString() );
		}
	}


	/* The main function reads in a Sudoku puzzle from the standard input, 
	 * unless a file name is provided as a run-time argument, in which case the
	 * Sudoku puzzle is loaded from that file.  It then solves the puzzle, and
	 * outputs the completed puzzle to the standard output. */
	public static void main( String args[] ) throws Exception {
		InputStream in = new FileInputStream("veryHard4x4.txt");

		// The first number in all Sudoku files must represent the size of the puzzle.  See
		// the example files for the file format.
		int puzzleSize = readInteger( in );
		if( puzzleSize > 100 || puzzleSize < 1 ) {
			System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
			System.exit(-1);
		}

		ChessSudoku s = new ChessSudoku( puzzleSize );
		
		// You can modify these to add rules to your sudoku
		s.knightRule = false;
		s.kingRule = false;
		s.queenRule = false;
		
		// read the rest of the Sudoku puzzle
		s.read( in );

		System.out.println("Before the solve:");
		s.print();
		System.out.println();

		// Solve the puzzle by finding one solution.
		s.solve(false);

		// Print out the (hopefully completed!) puzzle
		System.out.println("After the solve:");
		s.print();

	}
}

