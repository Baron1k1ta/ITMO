import java.util.Scanner;

public class B6 {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        int m = input.nextInt();
        int[][]field = new int[n][m];
        for (int i = 0; i< n; i++){
            for (int j = 0; j< m; j++){
                field[i][j] = input.nextInt();

            }
        }
        int[][]dp = new int[n][m];
        dp[0][0] = field[0][0];
        for (int i =1; i<n;i++){
            dp[i][0] = dp[i-1][0] + field[i][0];
        }
        for (int i =1; i<m;i++){
            dp[0][i] = dp[0][i-1] + field[0][i];
        }
        for (int i = 1; i< n; i++){
            for (int j = 1; j< m; j++){
                if (dp[i-1][j] > dp[i][j-1]){
                    dp[i][j] = dp[i-1][j] + field[i][j];
                }else{
                    dp[i][j] = dp[i][j-1] + field[i][j];
                }

            }
        }
        int result = dp[n-1][m-1];
        String string = "";
        int i = n-1;
        int j = m-1;
        for (int k = 0; k<n+m-2; k++){
            if (i-1 >=0 && j-1 >=0 && dp[i-1][j] >= dp[i][j-1]){
                i--;
                string = "D" + string;
            }else if(i-1 >=0 && j-1 >=0 && dp[i-1][j] < dp[i][j-1]){
                j--;
                string = "R" + string;
            }else if(i-1<0){
                j--;
                string = "R" + string;
            }else{
                i--;
                string = "D" + string;
            }
        }
        System.out.println(result);
        System.out.println(string);

    }
}
