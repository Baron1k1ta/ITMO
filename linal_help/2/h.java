public class h {
    public static void main(String[] args){
        int[][][] ar = new int[3][3][3];
        ar[0][0][0] = -3;
        ar[0][1][0] = -2;
        ar[0][2][0] = -2;
        ar[0][0][1] = 3;
        ar[0][1][1] = 0;
        ar[0][2][1] = 2;
        ar[0][0][2] = -3;
        ar[0][1][2] = 2;
        ar[0][2][2] = 7;
        ar[1][0][0] = 4;
        ar[1][1][0] = 1;
        ar[1][2][0] = 3;
        ar[1][0][1] = 4;
        ar[1][1][1] = -3;
        ar[1][2][1] = -5;
        ar[1][0][2] = -6;
        ar[1][1][2] = -3;
        ar[1][2][2] = -5;
        ar[2][0][0] = 1;
        ar[2][1][0] = -3;
        ar[2][2][0] = 1;
        ar[2][0][1] = 3;
        ar[2][1][1] = 5;
        ar[2][2][1] = 7;
        ar[2][0][2] = -8;
        ar[2][1][2] = 6;
        ar[2][2][2] = 3;
        int[][] mat = new int[3][3];
        mat[0][0] = 1;
        mat[0][1] = 1;
        mat[0][2] = -4;
        mat[1][0] = -2;
        mat[1][1] = -3;
        mat[1][2] = 4;
        mat[2][0] = 1;
        mat[2][1] = 2;
        mat[2][2] = -1;
        int[] wrong = new int[3];
        wrong[0] = -5;
        wrong[1] = -7;
        wrong[2] = -8;
        int[][][] res = new int[3][3][3];
        for (int a = 0; a < 3; a++){
            for (int b = 0; b < 3; b++){
                for (int c = 0; c < 3; c++){
                    res[a][b][c] = ar[a][b][c] * mat[a][b] * wrong[c];
                }
            }
        }
        System.out.print("[");
        for (int a = 0; a<3; a++) {
            for (int b = 0; b < 3; b++) {
                for (int c = 0; c < 3; c++) {
                    float number = res[a][c][b];
                    if (number == Math.floor(number)) {
                        System.out.print((int) number);
                    } else {
                        System.out.print(number);
                    }
                    if (a == 2 & b == 2 & c == 2) {
                        System.out.print("]");
                        break;
                    }
                    if (b == 2 & c == 2) {
                        System.out.print("; ");
                    } else {
                        System.out.print(", ");
                    }
                }
            }
        }
    }
}
