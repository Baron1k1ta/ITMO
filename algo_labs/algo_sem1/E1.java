import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
public class E1 {
    public static int[] sort(int[] numbers) {
        int length = numbers.length;
        if (length == 1) {
            return numbers;
        }
        int left[] = sort(Arrays.copyOfRange(numbers, 0, (length + 1) / 2));
        int right[] = sort(Arrays.copyOfRange(numbers, (length + 1) / 2, length));
        return merge(left, right);

    }

    public static int[] merge(int[] a, int[] b) {
        int n = a.length;
        int m = b.length;
        int c[] = new int[n + m];
        int i = 0;
        int j = 0;
        while (i < n | j < m) {
            if (i < n & j < m) {
                if (a[i] > b[j]) {
                    c[i + j] = a[i];
                    i++;
                } else {
                    c[i + j] = b[j];
                    j++;
                }
            } else {
                if (i >= n) {
                    c[i + j] = b[j];
                    j++;
                } else {
                    c[i + j] = a[i];
                    i++;
                }

            }
        }
        return c;
    }

    public static void main(String[] args) throws IOException{
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String[] s = reader.readLine().split(" ");
        int barrels = Integer.parseInt(s[0]);
        int buckets = Integer.parseInt(s[1]);
        int bucket_capacity = Integer.parseInt(s[2]);
        s = reader.readLine().split(" ");
        int[] a = new int[barrels];
        long count = 0;

        for (int i = 0; i < a.length; i++) {
            a[i] = Integer.parseInt(s[i]);
            while (a[i] >= bucket_capacity && buckets > 0) {
                count += bucket_capacity;
                buckets--;
                a[i] -= bucket_capacity;
            }
        }

        if (buckets == 0) {
            System.out.println(count);
        } else {
            a = sort(a);
            int i = 0;
            while (buckets > 0 && i < barrels) {
                buckets--;
                count += a[i];
                i++;
            }
            System.out.println(count);
        }
    }
}