package finalproject;

import java.util.*;
import java.io.*;


public class ChessSudokuY
{
    /* SIZE is the size parameter of the Sudoku puzzle, and N is the square of the size.  For
     * a standard Sudoku puzzle, SIZE is 3 and N is 9.
     */
    public int SIZE, N;

    /* The grid contains all the numbers in the Sudoku puzzle.  Numbers which have
     * not yet been revealed are stored as 0.
     */
    public int grid[][];
    private int[][][] possibleGrid;
    private int[] numberCount ;


    /* Booleans indicating whether of not one or more of the chess rules should be
     * applied to this Sudoku.
     */
    public boolean knightRule;
    public boolean kingRule;
    public boolean queenRule;
    private boolean solutionFound;


    // Field that stores the same Sudoku puzzle solved in all possible ways
    public HashSet<ChessSudokuY> solutions = new HashSet<ChessSudokuY>();


    /* The solve() method should remove all the unknown characters ('x') in the grid
     * and replace them with the numbers in the correct range that satisfy the constraints
     * of the Sudoku puzzle. If true is provided as input, the method should find finds ALL
     * possible solutions and store them in the field named solutions. */
    //	CHANGE TO PRIVATE**********************************************!!!!!!!!!!!!!!!!!!!!!!!!!


    private int[] containsEntries(int x,int y){
        //check colomn
        int count=0;
        int[] containsNumber = new int[N+1];
        //check row

        for(int i=0;i<N;i++){
            if(grid[x][i]!=0){
                if(containsNumber[grid[x][i]]==0){
                    count++;
                }
                containsNumber[grid[x][i]]+=1;

            }

            if(grid[i][y]!=0){
                if(containsNumber[grid[i][y]]==0){
                    count++;
                }
                containsNumber[grid[i][y]]+=1;

            }


        }

        //check box
        int xFloor= (x/SIZE)*SIZE;
        int yFloor= (y/SIZE)*SIZE;
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                if(grid[xFloor+i][yFloor+j]!=0) {
                    if(containsNumber[grid[xFloor+i][yFloor+j]]==0){
                        count++;

                    }
                    containsNumber[grid[xFloor+i][yFloor+j]]+=1;

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


                if (checkx >= 0 && checky >= 0 && checkx < N && checky < N && grid[checkx][checky] != 0 ){

                    if(containsNumber[grid[checkx][checky]]==0){
                        count++;
                    }
                    containsNumber[grid[checkx][checky]]+=1;

                }

            }
        }

        //king
        if(kingRule){

            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if((x-1)+i>=0 && (y-1)+j>=0 && (x-1)+i<N && (y-1)+j<N && grid[(x-1)+i][(y-1)+j]!=0 ){
                        if(containsNumber[grid[(x-1)+i][(y-1)+j]]==0){
                            count++;
                        }
                        containsNumber[grid[(x-1)+i][(y-1)+j]]+=1;

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
                    if(containsNumber[grid[xStart+i][yStart+i]]==0){
                        count++;
                    }
                    containsNumber[grid[xStart+i][yStart+i]]+=1;

                }
            }
            xStart=x+(N-1);
            yStart=y-(N-1);
            for(int i=0;i<(N*2);i++){
                //System.out.println("po:"+(xStart-i)+" "+(yStart+i));
                if(xStart-i>=0 && yStart+i>=0 && xStart-i<N && yStart+i<N && grid[xStart-i][yStart+i]==N){
                    if(containsNumber[grid[xStart-i][yStart+i]]==0){
                        count++;
                    }
                    containsNumber[grid[xStart-i][yStart+i]]+=1;

                }
            }
        }
        /*
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

         */
        containsNumber[0]=count;

        return containsNumber;

    }

    private int part(int[] list,int start,int finish){
        int pivot=numberCount[list[finish]-1];

        int i=(start-1);

        for(int j=start;j<=finish-1;j++){
            if(numberCount[list[j]-1]<pivot){
                i++;
                //swap
                int temp=list[i];
                list[i]=list[j];
                list[j]=temp;
            }

        }
        //swap
        int temp=list[i+1];
        list[i+1]=list[finish];
        list[finish]=temp;
        return i+1;

    }

    private void quickSort(int[] list,int start,int finish){
        if(start<finish){
            int pivot=part(list,start,finish);

            quickSort(list, start,pivot-1);
            quickSort(list,pivot+1,finish);
        }
    }

    private int[] getValidNumbers(int x, int y){
        int[] contains = possibleGrid[x][y];
        int[] validNumbers= new int[N-contains[0]];
        int size=0;
        for(int k=1;k<contains.length;k++){
            if(contains[k]==0){
                validNumbers[size]=k;
                size++;
            }
        }
        //System.out.println(Arrays.toString(validNumbers)+"unsorted");
        //System.out.println(Arrays.toString(numberCount));
        //quickSort(validNumbers,0,validNumbers.length-1);
        //System.out.println(Arrays.toString(validNumbers)+"sorted");

        return validNumbers;
    }

    private void updatePossible(int x,int y,int n,int bool){
        int boolAdder=(int) (Math.pow(-1, bool + 1));

        for(int i=0;i<N;i++){
            //row
                if(bool==(possibleGrid[x][i][n]+boolAdder)){
                    possibleGrid[x][i][0]+=boolAdder;
                }
                possibleGrid[x][i][n]+=boolAdder;

            //col
            if(bool==(possibleGrid[i][y][n]+boolAdder)){
                possibleGrid[i][y][0]+=boolAdder;
            }
            possibleGrid[i][y][n]+=boolAdder;

        }

        //check box
        int xFloor= (x/SIZE)*SIZE;
        int yFloor= (y/SIZE)*SIZE;
        for(int i=0;i<SIZE;i++){
            for(int j=0;j<SIZE;j++){
                if(bool==(possibleGrid[xFloor+i][yFloor+j][n]+boolAdder)){
                    possibleGrid[xFloor+i][yFloor+j][0]+=boolAdder;
                }
                possibleGrid[xFloor+i][yFloor+j][n]+=boolAdder;
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
                if (checkx >= 0 && checky >= 0 && checkx < N && checky < N){
                    if(bool==(possibleGrid[checkx][checky][n]+boolAdder)){
                        possibleGrid[checkx][checky][0]+=boolAdder;
                    }
                    possibleGrid[checkx][checky][n]+=boolAdder;
                }

            }
        }
        //king
        if(kingRule){
            for(int i=0;i<3;i++){
                for(int j=0;j<3;j++){
                    if((x-1)+i>=0 && (y-1)+j>=0 && (x-1)+i<N && (y-1)+j<N){

                        if(bool==(possibleGrid[(x-1)+i][(y-1)+j][n]+boolAdder)){
                            possibleGrid[(x-1)+i][(y-1)+j][0]+=boolAdder;
                        }
                        possibleGrid[(x-1)+i][(y-1)+j][n]+=boolAdder;
                    }
                }
            }

        }
        //queen
        if(queenRule && n==N){
            int xStart=x-(N-1);
            int yStart=y-(N-1);
            for(int i=0;i<(N*2);i++){
                if(xStart+i>=0 && yStart+i>=0 && xStart+i<N && yStart+i<N ){
                    if(bool==(possibleGrid[xStart+i][yStart+i][n]+boolAdder)){
                        possibleGrid[xStart+i][yStart+i][0]+=boolAdder;
                    }
                    possibleGrid[xStart+i][yStart+i][n]+=boolAdder;
                }
            }
            xStart=x+(N-1);
            yStart=y-(N-1);
            for(int i=0;i<(N*2);i++){
                if(xStart-i>=0 && yStart+i>=0 && xStart-i<N && yStart+i<N ){
                    if(bool==(possibleGrid[xStart-i][yStart+i][n]+boolAdder)){
                        possibleGrid[xStart-i][yStart+i][0]+=boolAdder;
                    }
                    possibleGrid[xStart-i][yStart+i][n]+=boolAdder;
                }
            }
        }



    }


    private void solveR(boolean allSolutions,int k){
        int mostEntries=0;
        int x=0;
        int y=0;
        for(int i=0;i<N;i++){
            for(int j=0;j<N;j++) {
                if(grid[i][j]==0 && possibleGrid[i][j][0]>mostEntries){
                    mostEntries=possibleGrid[i][j][0];
                    x=i;
                    y=j;
                }
            }
        }

        if(k==0){
            if(allSolutions){
                ChessSudokuY newSolution= new ChessSudokuY(SIZE);

                for(int p=0;p<newSolution.N;p++){
                    for(int f=0;f<newSolution.N;f++){
                        newSolution.grid[p][f]=this.grid[p][f];
                        newSolution.knightRule=this.knightRule;
                        newSolution.kingRule=this.kingRule;
                        newSolution.kingRule=this.kingRule;
                    }
                }
                solutions.add(newSolution);
            }else{
                solutionFound=true;
            }
        }


        int[] possible=getValidNumbers(x,y);
        int n=0;
        if(possible.length==0){
            return;
        }
        while (!solutionFound && n<possible.length) {
            grid[x][y] = possible[n];
            numberCount[possible[n]-1]+=1;
            updatePossible(x,y,possible[n],1);
            solveR(allSolutions,k-1);
            if (!solutionFound) {
                grid[x][y] = 0;
                numberCount[possible[n]-1]-=1;
                updatePossible(x,y,possible[n],0);
            }
            n++;
        }
        return;
    };


    public void solve(boolean allSolutions) {
        possibleGrid=new int[N][N][];
        numberCount= new int[N];
        int k=0;
        for(int x=0;x<N;x++){
            for(int y=0;y<N;y++){
                if(grid[x][y]==0){
                    k++;
                }else{
                    numberCount[grid[x][y]-1]+=1;
                }
                possibleGrid[x][y]=this.containsEntries(x,y);
            }
        }



        System.out.println(k);
        double start=System.nanoTime();
        solveR(allSolutions,k);
        double finish=System.nanoTime();
        double time=(finish-start)/1000000000;
        System.out.println("Time to solve:"+time+"seconds");


        if(allSolutions){
            for(ChessSudokuY solveState:solutions){
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
    public ChessSudokuY( int size ) {
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
        InputStream in = new FileInputStream("queen4x4.txt");

        // The first number in all Sudoku files must represent the size of the puzzle.  See
        // the example files for the file format.
        int puzzleSize = readInteger( in );
        if( puzzleSize > 100 || puzzleSize < 1 ) {
            System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
            System.exit(-1);
        }

        ChessSudokuY s = new ChessSudokuY( puzzleSize );

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
        if(true){
            for(ChessSudokuY set: s.solutions){
                set.print();
            }
        }

        // Print out the (hopefully completed!) puzzle
        System.out.println("After the solve:");
        s.print();

    }
}

