public class f {
    public static void main(String[] args){
        float[][][][][] ar = new float[2][2][2][2][2];
        float[][][][][] res = new float[2][2][2][2][2];
        ar[0][0][0][0][0] = 4;
        ar[1][0][0][0][0] = -3;
        ar[0][1][0][0][0] = 2;
        ar[1][1][0][0][0] = -2;
        ar[0][0][1][0][0] = 3;
        ar[1][0][1][0][0] = -2;
        ar[0][1][1][0][0] = -3;
        ar[1][1][1][0][0] = 4;
        ar[0][0][0][1][0] = 3;
        ar[1][0][0][1][0] = 4;
        ar[0][1][0][1][0] = 3;
        ar[1][1][0][1][0] = -3;
        ar[0][0][1][1][0] = -6;
        ar[1][0][1][1][0] = -2;
        ar[0][1][1][1][0] = -5;
        ar[1][1][1][1][0] = -1;
        ar[0][0][0][0][1] = 4;
        ar[1][0][0][0][1] = -4;
        ar[0][1][0][0][1] = -2;
        ar[1][1][0][0][1] = -6;
        ar[0][0][1][0][1] = 0;
        ar[1][0][1][0][1] = -2;
        ar[0][1][1][0][1] = 3;
        ar[1][1][1][0][1] = 0;
        ar[0][0][0][1][1] = 3;
        ar[1][0][0][1][1] = 0;
        ar[0][1][0][1][1] = 3;
        ar[1][1][0][1][1] = -2;
        ar[0][0][1][1][1] = -2;
        ar[1][0][1][1][1] = -5;
        ar[0][1][1][1][1] = -4;
        ar[1][1][1][1][1] = -2;

        for (int a = 0; a<2; a++){
            for (int b = 0; b<2; b++){
                for (int c = 0; c<2; c++){
                    for (int d = 0; d<2; d++){
                        for (int e = 0; e<2; e++){
                            res[a][b][c][d][e] = (ar[a][b][c][d][e] + ar[b][a][c][d][e])/2;
                        }
                    }
                }
            }
        }
        System.out.print("[");
        for (int a = 0; a<2; a++){
            for (int b = 0; b<2; b++){
                for (int c = 0; c<2; c++){
                    for (int d = 0; d<2; d++){
                        for (int e = 0; e<2; e++){
                            float number = res[b][e][a][d][c];
                            if(number == Math.floor(number)){
                                System.out.print((int) res[b][e][d][a][c]);
                            }else{
                                System.out.print(res[b][e][d][a][c]);
                            }
                            if (a == 1 & b == 1 & c == 1 & d==1 & e==1){
                                System.out.print("]");
                                break;
                            }
                            if (c==1 & d == 1 & e== 1){
                                System.out.print("; ");
                            }else{
                                System.out.print(", ");
                            }

                        }
                    }
                }
            }
        }
    }
}

