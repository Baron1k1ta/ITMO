public class m {
    public static void main(String[] args) {
        int[][] S = {{-15, -28, -30, 81}, {0, 1, 1, -4}, {4, 8, 8, -25}, {-5, -9, -10, 25}};
        int[][] A = {{-3, 4, 0, 5}, {-4, 1, 5, -4}, {3, 1, -1, -4}, {-3, 4, -5, 2}};
        int[][] B = new int[4][4];
        for (int i1 = 0; i1 < 4; i1++) {
            for (int p1 = 0; p1 < 4; p1++) {
                for (int i = 0; i < 4; i++) {
                    for (int p = 0; p < 4; p++) {
                        B[i1][p1] += S[i1][i] * S[p1][p] * A[i][p];
                    }
                }
            }
        }

        System.out.print("[");
        for (int a = 0; a < 4; a++) {
            for (int b = 0; b < 4; b++) {
                if (b == 3) {
                    System.out.print(B[a][b] + "; ");
                } else {
                    System.out.print(B[a][b] + ", ");
                }

            }
        }
    }
}


