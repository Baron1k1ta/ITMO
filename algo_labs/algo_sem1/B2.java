import java.util.Scanner;

public class B2 {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        int N = input.nextInt();
        int M = input.nextInt();
        int[] n = new int[N];
        int[] m = new int[M];
        for (int i = 0; i < N; i++) {
            n[i] = input.nextInt();
        }
        for (int i = 0; i < M; i++) {
            m[i] = input.nextInt();
        }
        for(int i = 0; i< m.length;i++){
            int[] ar = binarySearch(n,m[i]);
            if(ar.length!=0){
                System.out.print(ar[0]);
                System.out.println(" "+ ar[1]);
            }else{
                System.out.println(0);
            }
        }



    }
    public static int[] binarySearch(int[] n, int M ){
        int left = 0;
        int right = n.length - 1;

        while(left <= right){
            int mid = (left + right) /2;
            if (n[mid] == M){
                int i = mid-1;
                int j = mid+1;
                while(i>=0 && n[i] == M){
                    i--;
                }
                while(j< n.length && n[j] == M){
                    j++;
                }
                int[] ar =new int[2];
                ar[0] = i+2;
                ar[1] = j;
                return ar;
            }else if(n[mid] < M){
                left = mid+1;
            }else{
                right = mid -1;
            }
        }
        int[] ar = new int[0];
        return ar;
    }
}