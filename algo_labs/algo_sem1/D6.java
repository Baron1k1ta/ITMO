import java.util.Objects;
import java.util.Scanner;



public class D6 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        int[] ar1 = new int[n];
        for (int i = 0; i < n; i++) {
            ar1[i] = input.nextInt();
        }
        int m = input.nextInt();
        int[] ar2 = new int[m];
        for (int i = 0; i < m; i++) {
            ar2[i] = input.nextInt();
        }
        int[][] dp = new int[n + 1][m + 1];
        String[][] last = new String[n + 1][m + 1];
        for (int i = 0; i < n + 1; i++) {
            dp[i][0] = 0;
        }
        for (int i = 0; i < m + 1; i++) {
            dp[0][i] = 0;
        }
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                if (ar1[i-1] == ar2[j-1]) {
                    dp[i][j] = 1 + dp[i - 1][j - 1];
                    last[i][j] = i - 1 +" "+ (j - 1);
                } else {
                    if (dp[i - 1][j]> dp[i][j - 1]){
                        dp[i][j] = dp[i - 1][j];
                        last[i][j] = i - 1 +" "+ j;
                    }else{
                        dp[i][j] = dp[i][j-1];
                        last[i][j] = i +" "+ (j - 1);
                    }

                }
            }
        }
        System.out.println(dp[n][m]);
        int[] answer= new int[dp[n][m]];
        int count = dp[n][m]-1;
        int i = n;
        int j = m;
        while(i!=0 && j!=0){
            if (Objects.equals(last[i][j], i - 1 + " " + (j - 1))){
                i--;
                j--;
                answer[count] = ar1[i];
                count--;
            }else{
                if (Objects.equals(last[i][j], i - 1 + " " + j)){
                    i--;
                }else{
                    j--;
                }
            }
        }
        for (int k =0; k<dp[n][m];k++) {
            System.out.print(answer[k] + " ");
        }
    }
}