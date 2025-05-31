public class l {
    public static void main(String[] args) {
        int[][] T = {{-3, -5}, {2, 3}};
        int[][][] A = {{{-1, -6}, {-5, 0}}, {{5, 2}, {1, -4}}};
        int[][][] B = new int[3][3][3];

        for (int l1 = 0; l1 < 2; l1++) {
            for (int i1 = 0; i1 < 2; i1++) {
                for (int n1 = 0; n1 < 2; n1++) {
                    for (int l = 0; l < 2; l++) {
                        for (int i = 0; i < 2; i++) {
                            for (int n = 0; n < 2; n++) {
                                B[l1][i1][n1] += T[l][l1] * T[i][i1] * T[n][n1] * A[l][i][n];
                            }
                        }
                    }
                }
            }
        }

        System.out.print("[");
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    System.out.print(B[b][a][c]);
                    if (a == 1 && b == 1 && c == 1) {
                        System.out.println("]");
                        break;
                    }
                    if (b == 1 && c == 1) {
                        System.out.print("; ");
                    } else {
                        System.out.print(", ");
                    }
                }
            }
        }
    }
}

