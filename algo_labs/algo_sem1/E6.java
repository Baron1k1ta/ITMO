import java.util.Scanner;



public class E6 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String str1 = input.next();
        String str2 = input.next();
        int n = str1.length();
        int m = str2.length();
        int[][] dp = new int[n + 1][m + 1];
        dp[0][0] = 0;
        for (int i = 1; i < n + 1; i++) {
            dp[i][0] = dp[i-1][0]+1;
        }
        for (int i = 1; i < m + 1; i++) {
            dp[0][i] = dp[0][i-1]+1;
        }
        for (int i = 1; i < n + 1; i++) {
            for (int j = 1; j < m + 1; j++) {
                if (str1.charAt(i-1)==str2.charAt(j-1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = dp[i-1][j-1] + 1;
                }
                dp[i][j] = Math.min(dp[i][j],dp[i-1][j]+1);
                dp[i][j] = Math.min(dp[i][j],dp[i][j-1]+1);
            }
        }
        System.out.println(dp[n][m]);

    }
}