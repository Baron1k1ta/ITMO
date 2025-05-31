import java.util.*;

public class F {
    private static final int mod = 1000000007;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        int numValues = scanner.nextInt();
        int target = scanner.nextInt();

        int[] values = new int[numValues];
        for (int i = 0; i < numValues; i++) {
            values[i] = scanner.nextInt();
        }

        long[] results = new long[target + 1];
        Arrays.fill(results, Long.MAX_VALUE);
        Map<Long, Long> memo = new HashMap<>();
        results[0] = 1;

        for (int i = 1; i <= target; i++) {
            results[i] = 0;
            for (int value : values) {
                long currentResult = 0;
                long remaining = i - value;

                if (memo.containsKey(remaining)) {
                    currentResult = memo.get(remaining);
                } else {
                    for (int j = 0; j <= remaining; j++) {
                        currentResult = (currentResult + results[j] * results[Math.toIntExact(remaining - j)] % mod) % mod;
                    }
                    memo.put(remaining, currentResult);
                }

                results[i] = (results[i] + currentResult) % mod;
            }
        }

        for (int i = 1; i < results.length; i++) {
            System.out.print(results[i] + " ");
        }
    }
}