public class a {
    public static void main(String[] args){
        int l = 3;
        int last = 0;
        int q = 0;
        int[][][][] ar = new int[3][3][3][3];
        for (int d = 0; d < l; d++) {
            for (int c = 0; c < l; c++) {
                for (int b = 0; b < l; b++) {
                    for (int a = 0; a < l; a++) {
                        System.out.println("ar["+Integer.toString(a)+"][" +Integer.toString(b)+
                                "][" + Integer.toString(c) + "][" + Integer.toString(d) + "]");
                    }
                }
            }
        }

    }
}
