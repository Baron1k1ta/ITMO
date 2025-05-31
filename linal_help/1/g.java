public class g {
    public static void main(String[] args) {
        float[][][] ar = new float[2][2][2];
        float[][][] res = new float[2][2][2];
        ar[0][0][0] = -2;
        ar[1][0][0] = -4;
        ar[0][1][0] = 6;
        ar[1][1][0] = 5;

        ar[0][0][1] = -1;
        ar[1][0][1] = 5;
        ar[0][1][1] = 2;
        ar[1][1][1] = -5;

        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 2; b++) {
                for (int c = 0; c < 2; c++) {
                    res[a][b][c] = (ar[a][b][c]- ar[c][b][a])/2;
                }

            }
        }
        System.out.print("[");
        for (int a = 0; a<2; a++){
            for (int b = 0; b<2; b++){
                for (int c = 0; c<2; c++){
                    float number = res[a][c][b];
                    if(number == Math.floor(number)){
                        System.out.print((int) number);
                    }else{
                        System.out.print(number);
                    }
                    if (a == 1 & b == 1 & c == 1){
                        System.out.print("]");
                        break;
                    }
                    if (b == 1 & c == 1){
                        System.out.print("; ");
                    }else{
                        System.out.print(", ");
                    }
                }
            }
        }
    }
}
