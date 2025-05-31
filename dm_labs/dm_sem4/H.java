import java.util.*;

public class H {
    static final int mod = 998244353;
    static long n, k, temp;
    static List<Long> leftCombs = new ArrayList<>();
    static List<Long> result = new ArrayList<>();
    static long[][] binomialCoeff;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        k = sc.nextLong();
        n = sc.nextLong();
        binomialCoeff = new long[(int)k][(int)k];
        result.add(1L);

        for (int i = 0; i < k; i++) {
            binomialCoeff[i][0] = 1;
        }
        for (int i = 1; i < k; i++) {
            for (int j = 1; j <= i; j++) {
                binomialCoeff[i][j] = (binomialCoeff[i - 1][j - 1] + binomialCoeff[i - 1][j]) % mod;
            }
        }

        for (int i = 0; i < (k + 1) / 2; i++) {
            if (i % 2 == 0) {
                leftCombs.add((mod - binomialCoeff[(int)(k - 1 - i)][i]) % mod);
            } else {
                leftCombs.add(binomialCoeff[(int)(k - 1 - i)][i]);
            }
        }

        for (int i = 1; i < k - 1; i++) {
            temp = 0;
            for (int j = 0; j < i; j++) {
                temp = (temp + result.get(j) * result.get(i - 1 - j)) % mod;
            }
            result.add(temp);
        }

        for (long i = k - 1; i < n; i++) {
            temp = 0;
            for (int j = 1; j < (k + 1) / 2; j++) {
                temp = (temp + leftCombs.get(j) * result.get((int)(i - j))) % mod;
            }
            result.add(temp);
        }

        for (long val : result) {
            System.out.println(val);
        }
    }
}