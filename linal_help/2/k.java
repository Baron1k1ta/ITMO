public class k {
    public static void main(String[] args) {
        int[][] T = {{8, 19, -9},
                {11, 27, -10},
                {4, 10, -3}};
        int[][] S = {{19, -33, 53},
                {-7, 12, -19},
                {2, -4, 7}};

        int[][] A = {{-2, 0, -4},
                {3, 1, -1},
                {4, 2, -3}};
        int[][] B = new int[3][3];

        for (int i1 = 0; i1 < 3; i1++) {
            for (int j1 = 0; j1 < 3; j1++) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        B[i1][j1] += S[i1][i] * S[j1][j] * A[i][j];
                    }
                }
            }
        }

        System.out.print("[");
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (j ==2 && i ==2){
                    System.out.print(B[i][j]);
                    break;
                }
                System.out.print(B[i][j] + (j == 2 ? "; " : ", "));
            }
        }
        System.out.println("]");
    }
}

