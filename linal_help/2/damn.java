public class suk {
    public static void main(String[] args){
        int[][] b = new int[4][4];
        int[] a = new int[4];
        a[0] = -3;
        a[1] = 3;
        a[2] = 4;
        a[3] = -4;
        b[0][0] = 6;
        b[1][0] = 6;
        b[2][0] = 5;
        b[3][0] = 3;

        b[0][1] = -4;
        b[1][1] = -5;
        b[2][1] = 2;
        b[3][1] = 1;

        b[0][2] = 1;
        b[1][2] = -2;
        b[2][2] = 0;
        b[3][2] = -2;

        b[0][3] = -6;
        b[1][3] = -2;
        b[2][3] = 3;
        b[3][3] = 2;
        int[][][] res = new int[4][4][4];
        for (int i =0; i<4; i++){
            for (int j =0; j<4; j++){
                for (int z =0; z<4; z++){
                    res[i][j][z]= a[i] * b[j][z];
                }
            }
        }
        System.out.print("[");
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                for (int c = 0; c < 4; c++) {
                    System.out.print(res[i][c][j]);
                    if (i == 3 && j == 3 && c == 3) {
                        System.out.println("]");
                        break;
                    }
                    if (j == 3 && c == 3) {
                        System.out.print("; ");
                    } else {
                        System.out.print(", ");
                    }
                }
            }
        }
    }
}
