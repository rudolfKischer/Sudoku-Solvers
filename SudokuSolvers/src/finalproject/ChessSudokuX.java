package finalproject;

import java.util.*;
import java.io.*;


public class ChessSudokuX {
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
    private LinkedMatrix linkedMatrix;


    // Field that stores the same Sudoku puzzle solved in all possible ways
    public HashSet<ChessSudokuX> solutions = new HashSet<ChessSudokuX>();


    /* The solve() method should remove all the unknown characters ('x') in the grid
     * and replace them with the numbers in the correct range that satisfy the constraints
     * of the Sudoku puzzle. If true is provided as input, the method should find finds ALL
     * possible solutions and store them in the field named solutions. */
    //	CHANGE TO PRIVATE**********************************************!!!!!!!!!!!!!!!!!!!!!!!!!

    //class for doubly linked matrix
    private class LinkedMatrix{
        //subclass for the nodes and links

        mNode root;
        mNode[] rowHeader;
        int[][] solutionGrid;
        int[]   solutionNumbers;
        int     solutionNumbersSize;
        boolean finished;

        LinkedMatrix(){
            root=new mNode();
            rowHeader= new mNode[N*N*N+1];
            solutionGrid=new int[N][N];
            solutionNumbers= new int[N*N];
            finished=false;
        }

        class mNode{
            int numColRowID;
            char type;//'C'(colomn),'R'(root),'N'(node)
            int columnNum;
            int count=0;
            mNode columnHead;
            mNode right;
            mNode left;
            mNode up;
            mNode down;
            //method for covering
            //this should only ever be called on a colomn head
            void columnCover(){
                this.right.left=this.left;
                this.left.right=this.right;
                for(mNode row =this.down;row !=this;row=row.down){
                    for(mNode right =row.right;right !=row;right=right.right){
                        right.up.down=right.down;
                        right.down.up=right.up;
                        this.count--;
                    }
                }
            }
            //method for covering
            void columnUnCover(){
                for(mNode row =this.up;row !=this;row=row.up){
                    for(mNode right =row.left;right !=row;right=right.left){
                        right.up.down=right;
                        right.down.up=right;
                        this.count++;
                    }
                }
                this.right.left=this;
                this.left.right=this;

            }

            void countNumOfNodes(){
                int counter=0;
                for(mNode pointer=this.down;pointer !=this; pointer=pointer.down){
                    counter++;
                }
                //System.out.println(counter);

                count=counter;
            }

            mNode findBestColumn(){
                int min=Integer.MAX_VALUE;
                mNode bestColumn=this.right;
                for(mNode pointer=this.right;pointer!=this;pointer=pointer.right){
                    if(pointer.count<min){
                        min=pointer.count;
                        bestColumn=pointer;

                    }
                }
                return bestColumn;
            }


        }

        private void countColumns(){

            for(mNode pointer=root.right;pointer != root;pointer=pointer.right){
                pointer.countNumOfNodes();
            }
        }

        //method for building the matrix for sudoku including chess sudoku
        private int[][] buildBitmap(){
            int colNum=N*N*4;
            int rowNum=N*N*N+1;

            int[][] bitMap= new int[colNum][rowNum];
            for(int i=0;i<N;i++){
                for(int j=0;j<N;j++){
                    for(int n=0;n<N;n++){
                        //only 1 per square
                        bitMap[i*N+j][i*N*N+j*N+n]=1;
                        //only 1 per row
                        bitMap[N*N+i*N+n][i*N*N+j*N+n]=1;
                        //only 1 per column
                        bitMap[2*(N*N)+j*N+n][i*N*N+j*N+n]=1;
                        //only 1 per region
                        bitMap[3*(N*N)+SIZE*(i/SIZE)*N+(j/SIZE)*N+n][i*N*N+j*N+n]=1;

                    }
                }
            }
            for(int i=0;i<colNum;i++){
                bitMap[i][rowNum-1]=2;
            }
            return bitMap;
        }
        //traverse matrix
        private void traverseMatrix(){
            int[][] gridTraversed=new int[N*N*4][N*N*N+1];
            int i=0;
            for(mNode pointer=root.right;pointer !=root;pointer=pointer.right){
                for(mNode pointerDown=pointer.down;pointerDown !=pointer;pointerDown=pointerDown.down){
                    gridTraversed[i][pointerDown.numColRowID]=1;
                }
                i++;
            }
            for(int x =0; x<gridTraversed[0].length;x++){
                for(int y =0; y<gridTraversed.length;y++){
                    if(gridTraversed[y][x] !=0){
                        System.out.print(gridTraversed[y][x]);
                    }else{
                        System.out.print(" ");
                    }

                }
                System.out.println();
            }
        }
        //method for converting that matrix into a linked matrix
        private void buildLinkedMatrix(){
            int colNum=N*N*4;
            int rowNum=N*N*N+1;
            mNode[][] nodeMatrix=new mNode[colNum][rowNum];
            for(int i=0;i<colNum;i++){
                for(int j=0;j<rowNum;j++){
                    nodeMatrix[i][j]=new mNode();
                }
            }
            int[][] bitMap=buildBitmap();

            for(int i=0;i<colNum;i++){
                for(int j=0;j<rowNum;j++){
                    if(bitMap[i][j] !=0){

                        //left
                        int p=i;
                        int k=j;
                        if((p-1)<0){
                            p=colNum-1;
                        }else{ p--;}
                        while(bitMap[p][k]==0){
                            if((p-1)<0){
                                p=colNum-1;
                            }else{ p--;}
                        }
                        nodeMatrix[i][j].left =nodeMatrix[p][k];

                        //right
                        p=(i+1)%colNum;
                        k=j;
                        while(bitMap[p][k]==0){
                            p=(p+1)%colNum;
                        }
                        nodeMatrix[i][j].right =nodeMatrix[p][k];

                        //up
                        p=i;
                        k=j;
                        if((k-1)<0){
                            k=rowNum-1;
                        }else{ k--;}
                        while(bitMap[p][k]==0){
                            if((k-1)<0){
                                k=rowNum-1;
                            }else{ k--;}
                        }
                        nodeMatrix[i][j].up =nodeMatrix[p][k];

                        //down
                        p=i;
                        k=(j+1)%rowNum;;
                        while(bitMap[p][k]==0){
                            k=(k+1)%rowNum;
                        }
                        nodeMatrix[i][j].down =nodeMatrix[p][k];

                        //hearer and number
                        nodeMatrix[i][j].columnHead= nodeMatrix[i][rowNum-1];
                        nodeMatrix[i][j].numColRowID=j;
                        //row header
                        rowHeader[j]= nodeMatrix[i][j];


                    }
                }
            }
            //label colomns heads
            for(int i=0;i<colNum;i++){
                nodeMatrix[i][rowNum-1].type= 'C';
                nodeMatrix[i][rowNum-1].numColRowID= i;
            }
            //make root
            root.type='R';
            root.left = nodeMatrix[colNum-1][rowNum-1];
            root.right= nodeMatrix[0][rowNum-1];
            nodeMatrix[colNum-1][rowNum-1].right=root;
            nodeMatrix[0][rowNum-1].left=root;
        }
        //method for reading the grid and covering the appropriate rows for the given grid
        private void addPuzzle(){
            for(int i=0;i<grid.length;i++){
                for(int j=0;j<grid.length;j++){
                    if(grid[i][j]!=0){
                        //solutionGrid[i][j]=grid[i][j];
                        int item=i*N*N+j*N+(grid[i][j]-1);
                        solutionNumbers[solutionNumbersSize]=item;
                        solutionNumbersSize++;
                        mNode puzzleNode=rowHeader[item];
                        puzzleNode.columnHead.columnCover();
                        for(mNode pointer=puzzleNode.right;pointer !=puzzleNode;pointer=pointer.right){
                            pointer.columnHead.columnCover();
                        }
                    }
                }
            }
        }
        //method for solving the matrix recursively
        private void solver(int k){
            if(root.left==root && root.right==root){
                for(int p=0;p<solutionNumbersSize;p++){
                    int i=(solutionNumbers[p]/(N*N));
                    int j=(solutionNumbers[p]/N)%N;
                    int n=(solutionNumbers[p]%N)+1;
                    grid[i][j]=n;
                }
                finished=true;
                return;
            }
            mNode column=root.findBestColumn();
            //mNode column=root.left;
            if(column.count==0){
                return;
            }
            column.columnCover();
            for(mNode verticalPointer=column.down; verticalPointer != column && !finished; verticalPointer =verticalPointer.down){
                solutionNumbers[solutionNumbersSize]=verticalPointer.numColRowID;
                solutionNumbersSize++;

                for(mNode pointerHorizontal=verticalPointer.right;pointerHorizontal !=verticalPointer;pointerHorizontal=pointerHorizontal.right){
                    pointerHorizontal.columnHead.columnCover();
                }
                //System.out.print(k+" ");
                solver(k+1);

                for(mNode pointerHorizontal=verticalPointer.left;pointerHorizontal !=verticalPointer;pointerHorizontal=pointerHorizontal.left){
                    pointerHorizontal.columnHead.columnUnCover();
                }
                solutionNumbersSize--;
                solutionNumbers[solutionNumbersSize]=0;

            }
            column.columnUnCover();
        }


    }


    public void solve(boolean allSolutions) {
        this.linkedMatrix=new LinkedMatrix();
        linkedMatrix.buildLinkedMatrix();
        linkedMatrix.countColumns();
        //linkedMatrix.traverseMatrix();
        linkedMatrix.addPuzzle();
        System.out.println("System built");

        double start=System.nanoTime();
        linkedMatrix.solver(0);
        double finish=System.nanoTime();
        double time=(finish-start)/1000000000;
        System.out.println("Time to solve:"+time+"seconds");

    }



    /*****************************************************************************/
    /* NOTE: YOU SHOULD NOT HAVE TO MODIFY ANY OF THE METHODS BELOW THIS LINE. */
    /*****************************************************************************/

    /* Default constructor.  This will initialize all positions to the default 0
     * value.  Use the read() function to load the Sudoku puzzle from a file or
     * the standard input. */
    public ChessSudokuX( int size ) {
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
        InputStream in = new FileInputStream("easy3x3.txt");

        // The first number in all Sudoku files must represent the size of the puzzle.  See
        // the example files for the file format.
        int puzzleSize = readInteger( in );
        if( puzzleSize > 100 || puzzleSize < 1 ) {
            System.out.println("Error: The Sudoku puzzle size must be between 1 and 100.");
            System.exit(-1);
        }

        ChessSudokuX s = new ChessSudokuX( puzzleSize );

        s.knightRule = false;
        s.kingRule = false;
        s.queenRule = false;

        s.read( in );

        System.out.println("Before the solve:");
        s.print();
        System.out.println();

        // Solve the puzzle by finding one solution.
        int count=0;
        for(int i=0;i<s.N;i++){
            for(int j=0;j<s.N;j++){
                if(s.grid[i][j]==0){
                    count++;
                }
            }
        }
        System.out.println("number of empty cells:"+count);

        s.solve(false);

        // Print out the (hopefully completed!) puzzle
        System.out.println("After the solve:");
        s.print();
    }


}

