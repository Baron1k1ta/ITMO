public class j {
    public static void main(String[] args){
        int[][] ar = new int[3][3];
        ar[0][0] = -2;
        ar[0][1] = 0;
        ar[0][2] = -4;

        ar[1][0] = 3;
        ar[1][1] = 1;
        ar[1][2] = -1;

        ar[1][0] = 4;
        ar[1][1] = 2;
        ar[1][2] = -3;

        int[][] T = new int[3][3];
        T[0][0] = 8;
        T[0][1] = 19;
        T[0][2] = -9;

        T[1][0] = 11;
        T[1][1] = 27;
        T[1][2] = -10;

        T[1][0] = 4;
        T[1][1] = 10;
        T[1][2] = -3;

        int[][] S = new int[3][3];
        S[0][0] = 19;
        S[0][1] = -33;
        S[0][2] = 53;

        S[1][0] = -7;
        S[1][1] = 12;
        S[1][2] = -19;

        S[1][0] = 2;
        S[1][1] = -4;
        S[1][2] = 7;
        int[][] res = new int[3][3];

        for (int a = 0; a <3; a++){
            for (int b = 0; b <3; b++){
                for (int  n = 0; n <3; n++){
                    for (int i = 0; i <3; i++){
                        res[a][b] += S[n][a] * S[i][b] * ar[n][i];
                    }
                }
            }
        }
        for (int a = 0; a <3; a++) {
            System.out.println();
            for (int b = 0; b < 3; b++) {
                System.out.print(res[a][b] + " ");
            }
        }
    }
}
