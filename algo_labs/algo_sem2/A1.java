import java.util.Scanner;
public class A1 {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        int N = scan.nextInt();
        int[] numbers = new int[N];
        for (int i = 0; i < N; i++) {
            numbers[i] = scan.nextInt();
        }
        int power = 1;
        while (power < N) {
            power*=2;
        }
        int[][] tree = new int[power * 2][2];
        build_tree(tree, numbers, 1, 0, N - 1);
        int K = scan.nextInt();
        for (int i = 0; i < K; i++) {
            int left = scan.nextInt();
            int right = scan.nextInt();
            int[] result = request(tree, left -1 , right - 1, 1, 0, N - 1);
            System.out.println(result[0] + " " + result[1]);
        }
    }

    static void build_tree(int[][] tree, int[] numbers, int v, int left, int right) {
        if (left == right) {
            tree[v][0] = numbers[left];
            tree[v][1] = left + 1;
        } else {
            int mid = (left + right) / 2;
            build_tree(tree, numbers, 2 * v, left, mid);
            build_tree(tree, numbers, 2 * v + 1, mid + 1, right);
            if (tree[2 * v][0] >= tree[2 * v + 1][0]) {
                tree[v][0] = tree[2 * v][0];
                tree[v][1] = tree[2 * v][1];
            } else {
                tree[v][0] = tree[2 * v + 1][0];
                tree[v][1] = tree[2 * v + 1][1];
            }
        }
    }

    static int[] request(int[][] tree, int left, int right, int v, int tree_left, int tree_right) {
        if (left <= tree_left && tree_right <= right) {
            return tree[v];
        } else if (tree_right < left || right < tree_left) {
            return new int[]{Integer.MIN_VALUE, -1};
        } else {
            int tm = (tree_left + tree_right) / 2;
            int[] left_result = request(tree, left, right, 2 * v, tree_left, tm);
            int[] right_result = request(tree, left, right, 2 * v + 1, tm + 1, tree_right);
            if (left_result[0] >= right_result[0]) {
                return left_result;
            } else {
                return right_result;
            }
        }
    }
}