import java.util.Scanner;

public class A7 {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        int m = input.nextInt();
        int[] weights = new int[n];
        for (int i = 0; i < n; i++) {
            weights[i] = input.nextInt();
        }
        int[][] dp = new int[n + 1][m + 1];
        dp[0][0] = 0;
        for (int i = 1; i < m + 1; i++) {
            dp[0][i] = -10;
        }
        for (int i = 1; i < n + 1; i++) {
            for (int j = 0; j < m + 1; j++) {
                dp[i][j] = dp[i - 1][j];
                if (j >= weights[i - 1] && dp[i-1][j-weights[i - 1]] != -10) {
                    dp[i][j] = dp[i - 1][j - weights[i-1]];
                }


            }
        }
        for (int i = m; i >= 0; i--) {
            if (dp[n][i] == 0) {
                System.out.println(i);
                break;
            }
        }
    }
}