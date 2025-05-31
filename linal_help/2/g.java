public class g {
    public static void main(String[] args){
        int[][][][][] ar = new int[2][2][2][2][2];
        ar[0][0][0][0][0] = 3;
        ar[0][1][0][0][0] = 2;
        ar[0][0][1][0][0] = 3;
        ar[0][1][1][0][0] = -4;
        ar[0][0][0][0][1] = 3;
        ar[0][1][0][0][1] = 1;
        ar[0][0][1][0][1] = -2;
        ar[0][1][1][0][1] = -2;

        ar[1][0][0][0][0] = -4;
        ar[1][1][0][0][0] = 1;
        ar[1][0][1][0][0] = 1;
        ar[1][1][1][0][0] = 1;
        ar[1][0][0][0][1] = -1;
        ar[1][1][0][0][1] = -4;
        ar[1][0][1][0][1] = 3;
        ar[1][1][1][0][1] = -4;



        ar[0][0][0][1][0] = 3;
        ar[0][1][0][1][0] = 0;
        ar[0][0][1][1][0] = -3;
        ar[0][1][1][1][0] = -2;
        ar[0][0][0][1][1] = 3;
        ar[0][1][0][1][1] = -4;
        ar[0][0][1][1][1] = 2;
        ar[0][1][1][1][1] = 0;

        ar[1][0][0][1][0] = -1;
        ar[1][1][0][1][0] = -3;
        ar[1][0][1][1][0] = -2;
        ar[1][1][1][1][0] = -2;
        ar[1][0][0][1][1] = -1;
        ar[1][1][0][1][1] = -3;
        ar[1][0][1][1][1] = 3;
        ar[1][1][1][1][1] = 0;

        int[][][][] res = new int[2][2][2][2];
        for (int a = 0; a < 2; a++){
            for (int b = 0; b < 2; b++){
                for (int c = 0; c < 2; c++){
                    for (int d = 0; d < 2; d++){
                        res[a][b][c][d] = 3 * ar[a][b][0][c][d] - 2 * ar[a][b][1][c][d];
                    }
                }
            }
        }
        System.out.print("[");
        for (int a = 0; a<2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    for (int d = 0; d < 2; d++) {
                        float number = res[b][d][c][a];
                        if (number == Math.floor(number)) {
                            System.out.print((int) number);
                        } else {
                            System.out.print(number);
                        }
                        if (a == 1 & b == 1 & c == 1 & d == 1) {
                            System.out.print("]");
                            break;
                        }
                        if (c == 1 & d == 1) {
                            System.out.print("; ");
                        } else {
                            System.out.print(", ");
                        }
                    }
                }
            }
        }
    }
}
