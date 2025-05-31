import java.util.Scanner;

public class L1 {

    public static long mergeAndCount(int[] a, int left, int mid, int right) {
        int it1 = 0, it2 = 0;
        long count = 0;
        int[] result = new int[right - left];

        while (left + it1 < mid && mid + it2 < right) {
            if (a[left + it1] <= a[mid + it2]) {
                result[it1 + it2] = a[left + it1];
                it1++;
            } else {
                result[it1 + it2] = a[mid + it2];
                count += mid - (left + it1);
                it2++;
            }
        }

        while (left + it1 < mid) {
            result[it1 + it2] = a[left + it1];
            it1++;
        }

        while (mid + it2 < right) {
            result[it1 + it2] = a[mid + it2];
            it2++;
        }

        for (int i = 0; i < it1 + it2; i++) {
            a[left + i] = result[i];
        }

        return count;
    }

    public static long mergeSortAndCount(int[] a, int left, int right) {
        if (right - left < 2) {
            return 0;
        }

        int mid = (left + right) / 2;
        long count = 0;

        count += mergeSortAndCount(a, left, mid);
        count += mergeSortAndCount(a, mid, right);
        count += mergeAndCount(a, left, mid, right);

        return count;
    }

    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int n = scan.nextInt();
        int[] numbers = new int[n];

        for (int i = 0; i < n; i++) {
            numbers[i] = scan.nextInt();
        }
        long result = mergeSortAndCount(numbers, 0, numbers.length);
        System.out.println(result);
    }
}