public class d {
    public static void main(String[] args){
        float [][][][][] ar= new float[2][2][2][2][2];
        ar[0][0][0][0][0] = 0;
        ar[0][1][0][0][0] = -3;
        ar[0][0][1][0][0] = -4;
        ar[0][1][1][0][0] = -2;

        ar[1][0][0][0][0] = 2;
        ar[1][1][0][0][0] = 3;
        ar[1][0][1][0][0] = 0;
        ar[1][1][1][0][0] = -1;

        ar[0][0][0][1][0] = 3;
        ar[0][1][0][1][0] = -4;
        ar[0][0][1][1][0] = 1;
        ar[0][1][1][1][0] = -3;

        ar[1][0][0][1][0] = 3;
        ar[1][1][0][1][0] = 2;
        ar[1][0][1][1][0] = -3;
        ar[1][1][1][1][0] = -3;


        ar[0][0][0][0][1] = -1;
        ar[0][1][0][0][1] = 4;
        ar[0][0][1][0][1] = 1;
        ar[0][1][1][0][1] = -5;

        ar[1][0][0][0][1] = 5;
        ar[1][1][0][0][1] = 4;
        ar[1][0][1][0][1] = 6;
        ar[1][1][1][0][1] = 3;

        ar[0][0][0][1][1] = 1;
        ar[0][1][0][1][1] = -3;
        ar[0][0][1][1][1] = -2;
        ar[0][1][1][1][1] = 3;

        ar[1][0][0][1][1] = -4;
        ar[1][1][0][1][1] = 5;
        ar[1][0][1][1][1] = -3;
        ar[1][1][1][1][1] = -5;

        float[][][][][] res = new float[2][2][2][2][2];

        for (int a = 0; a<2; a++){
            for (int b = 0; b<2; b++){
                for (int c = 0; c<2; c++){
                    for (int d = 0; d<2; d++){
                        for (int e = 0; e<2; e++){
                            res[a][b][c][d][e] = (ar[a][b][c][d][e] - ar[a][b][e][d][c] + ar[a][c][e][d][b] -
                                    ar[a][c][b][d][e] + ar[a][e][b][d][c] - ar[a][e][c][d][b] + ar[b][e][c][d][a] -
                                    ar[b][e][a][d][c] + ar[b][c][a][d][e] - ar[b][c][e][d][a] + ar[b][a][e][d][c] - ar[b][a][c][d][e] +
                                    ar[c][a][b][d][e] - ar[c][a][e][d][b] + ar[c][b][e][d][a] - ar[c][b][a][d][e] + ar[c][e][a][d][b] -
                                    ar[c][e][b][d][a] + ar[e][c][b][d][a] - ar[e][c][a][d][b] + ar[e][b][a][d][c] - ar[e][b][c][d][a] +
                                    ar[e][a][c][d][b] - ar[e][a][b][d][c])/24;

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
