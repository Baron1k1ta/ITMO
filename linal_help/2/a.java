public class a {
    public static void main(String[] args){
        int[][][][][] ar = new int[3][3][3][3][3];

        for(int j =1; j <3; j++){
            for(int t =1; t <3; t++){
                for(int n =1; n <3; n++){
                    for(int l =1; l <3; l++){
                        for(int p =1; p <3; p++){
                            ar[j][t][n][l][p] = 2 * t + n -3 * l - 2* p;
                        }
                    }
                }
            }
        }
        System.out.println(ar[1][1][1][1][1]+ ar[2][1][2][1][1] + ar[1][2][1][2][1] + ar[2][2][2][2][1]);
        System.out.println(ar[1][1][1][1][2]+ ar[2][1][2][1][2] + ar[1][2][1][2][2] + ar[2][2][2][2][2]);
    }
}