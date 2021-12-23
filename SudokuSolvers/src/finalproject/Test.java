package finalproject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;

public class Test {

    public static void main( String args[] ) throws Exception {
        int N=4;
        for(int a=0;a<(N*N*N);a++){
            int i=(a/(N*N));
            int j=(a/N)%4;
            int n=(a%N)+1;
            System.out.println(n+" "+(i+1)+" "+(j+1));
        }


        int p=3;
        int c=(p)-N*((p)/N)+1;
        int d=15/(N)+1;
        System.out.println(c);
        System.out.println(d);

        int puzzleSize=2;
        ChessSudokuX s = new ChessSudokuX( puzzleSize );

        //int[][] bit=;


        /*
        s.grid=new int[s.N][s.N];
        s.grid[2][2]=9;
        s.grid[1][0]=2;
        //s.grid[1][2]=7;
        //s.grid[2][1]=2;
        //s.knightRule=true;
        s.print();
        s.solve(false);
        s.print();

        //System.out.println(Arrays.toString(s.validEntries(4, 4)));
        */


    }
}
