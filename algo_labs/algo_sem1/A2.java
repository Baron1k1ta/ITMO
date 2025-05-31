import java.util.Scanner;

public class A2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int N = scanner.nextInt();
        int K = scanner.nextInt();
        int[] array = new int[N];
        int[] numbersToSearch = new int[K];
        for (int i = 0; i < N; i++) {
            array[i] = scanner.nextInt();
        }
        for (int i = 0; i < K; i++) {
            numbersToSearch[i] = scanner.nextInt();
        }
        for (int number : numbersToSearch) {
            if (binarySearch(array, number)) {
                System.out.println("YES");
            } else {
                System.out.println("NO");
            }
        }
    }

    private static boolean binarySearch(int[] array, int key) {
        int left = 0;
        int right = array.length - 1;
        while (left <= right) {
            int mid = (left + right) / 2;
            if (array[mid] == key) {
                return true;
            } else if (array[mid] < key) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return false;
    }
}