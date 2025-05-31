//public class i {
//    public static void main(String[] args) {
//        int[][][] ar = new int[3][3][3];
//        ar[0][0][0] = -3;
//        ar[0][1][0] = -2;
//        ar[0][2][0] = -2;
//
//        ar[0][0][1] = 3;
//        ar[0][1][1] = 0;
//        ar[0][2][1] = 2;
//
//        ar[0][0][2] = -3;
//        ar[0][1][2] = 2;
//        ar[0][2][2] = 7;
//
//
//        ar[1][0][0] = 4;
//        ar[1][1][0] = 1;
//        ar[1][2][0] = 3;
//
//        ar[1][0][1] = 4;
//        ar[1][1][1] = -3;
//        ar[1][2][1] = -5;
//
//        ar[1][0][2] = -6;
//        ar[1][1][2] = -3;
//        ar[1][2][2] = -5;
//
//
//        ar[2][0][0] = 1;
//        ar[2][1][0] = -3;
//        ar[2][2][0] = 1;
//
//        ar[2][0][1] = 3;
//        ar[2][1][1] = 5;
//        ar[2][2][1] = 7;
//
//        ar[2][0][2] = -8;
//        ar[2][1][2] = 6;
//        ar[2][2][2] = 3;
//
//        int[][] T = new int[3][3];
//        T[0][0] = -5;
//        T[0][1] = -7;
//        T[0][2] = -8;
//
//        T[1][0] = 2;
//        T[1][1] = 3;
//        T[1][2] = 4;
//
//        T[1][0] = -1;
//        T[1][1] = -1;
//        T[1][2] = -1;
//
//        int[][] S = new int[3][3];
//        S[0][0] = 1;
//        S[0][1] = 1;
//        S[0][2] = -4;
//
//        S[1][0] = -2;
//        S[1][1] = -3;
//        S[1][2] = 4;
//
//        S[1][0] = 1;
//        S[1][1] = 2;
//        S[1][2] = -1;
//        int[][][] res = new int[3][3][3];
//
//        for (int p = 0; p < 3; p++){
//            for (int k = 0; k < 3; k++){
//                for (int r = 0; r < 3; r++){
//                    for (int a = 0; a < 3; a++){
//                        for (int b = 0; b < 3; b++){
//                            for (int c = 0; c < 3; c++){
//                                System.out.println(ar[a][b][c]);
//                                res[p][k][r]+= T[a][p] * S[k][b] * S[r][c] * ar[a][b][c];
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        System.out.print("[");
//        for (int a = 0; a<3; a++) {
//            for (int b = 0; b < 3; b++) {
//                for (int c = 0; c < 3; c++) {
//                    System.out.print(res[a][c][b]);
//                    if (a == 2 & b == 2 & c == 2) {
//                        System.out.print("]");
//                        break;
//                    }
//                    if (b == 2 & c == 2) {
//                        System.out.print("; ");
//                    } else {
//                        System.out.print(", ");
//                    }
//                }
//            }
//        }
//    }
//}
public class i {
    public static void main(String[] args) {
        int[][] T = {{-5, -7, -8},
                {2, 3, 4},
                {-1, -1, -1}};

        int[][] S = {{1, 1, -4},
                {-2, -3, 4},
                {1, 2, -1}};

        int[][][] A = {
                {{-3, -2, -2},
                        {4, 1, 3},
                        {1, -3, 1}},

                {{3, 0, 2},
                        {4, -3, -5},
                        {3, 5, 7}},

                {{-3, 2, 7},
                        {-6, -3, -5},
                        {-8, 6, 3}}
        };

        int[][][] res = new int[3][3][3];

        for (int a = 0; a < 3; a++) {
            for (int b = 0; b < 3; b++) {
                for (int c = 0; c < 3; c++) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            for (int m = 0; m < 3; m++) {
                                res[a][b][c] += T[i][a] * S[b][j] * S[c][m] * A[i][j][m];
                            }
                        }
                    }
                }
            }
        }
        System.out.print("[");
        for (int a = 0; a<3; a++) {
            for (int b = 0; b < 3; b++) {
                for (int c = 0; c < 3; c++) {
                    System.out.print(res[a][c][b]);
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
