public class e {
    public static void main(String[] args){
        float[][][][][] ar = new float[2][2][2][2][2];
        float[][][][][] res = new float[2][2][2][2][2];
        ar[0][0][0][0][0] = 5;
        ar[1][0][0][0][0] = 2;
        ar[0][1][0][0][0] = 4;
        ar[1][1][0][0][0] = 3;
        ar[0][0][1][0][0] = 3;
        ar[1][0][1][0][0] = -5;
        ar[0][1][1][0][0] = 0;
        ar[1][1][1][0][0] = -1;
        ar[0][0][0][1][0] = 3;
        ar[1][0][0][1][0] = -2;
        ar[0][1][0][1][0] = -3;
        ar[1][1][0][1][0] = 2;
        ar[0][0][1][1][0] = 1;
        ar[1][0][1][1][0] = 0;
        ar[0][1][1][1][0] = 0;
        ar[1][1][1][1][0] = -6;
        ar[0][0][0][0][1] = 3;
        ar[1][0][0][0][1] = 3;
        ar[0][1][0][0][1] = 1;
        ar[1][1][0][0][1] = -1;
        ar[0][0][1][0][1] = 1;
        ar[1][0][1][0][1] = 4;
        ar[0][1][1][0][1] = -1;
        ar[1][1][1][0][1] = 1;
        ar[0][0][0][1][1] = -6;
        ar[1][0][0][1][1] = 3;
        ar[0][1][0][1][1] = -4;
        ar[1][1][0][1][1] = 3;
        ar[0][0][1][1][1] = -2;
        ar[1][0][1][1][1] = 3;
        ar[0][1][1][1][1] = -6;
        ar[1][1][1][1][1] = 5;

        for (int a = 0; a<2; a++){
            for (int b = 0; b<2; b++){
                for (int c = 0; c<2; c++){
                    for (int d = 0; d<2; d++){
                        for (int e = 0; e<2; e++){
                            float val = (ar[a][b][c][d][e] + ar[a][b][c][e][d] + ar[a][d][c][b][e] + ar[a][d][c][e][b] + ar[a][e][c][d][b] + ar[a][e][c][b][d]
                                    + ar[b][a][c][d][e] + ar[b][a][c][e][d] + ar[b][d][c][a][e] + ar[b][d][c][e][a] + ar[b][e][c][d][a] + ar[b][e][c][a][d]
                                    + ar[d][a][c][b][e] + ar[d][a][c][e][b] + ar[d][b][c][a][e] + ar[d][b][c][e][a] + ar[d][e][c][a][b] + ar[d][e][c][b][a]
                                    + ar[e][a][c][b][d] + ar[e][a][c][d][b] + ar[e][b][c][a][d] + ar[e][b][c][d][a] + ar[e][d][c][a][b] + ar[e][d][c][b][a])/24;
                            res[a][b][c][d][e] = val;
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

