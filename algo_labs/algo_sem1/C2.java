import java.util.Scanner;

public class C2 {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        int n = input.nextInt();
        double a = input.nextFloat();
        double[] ar = new double[n];
        ar[0] =  a;
        double b = 0.01;
        ar[1] = b;
        double left = b;
        double right = a;
        while (true) {
            double mid = (left + right) / 2;
            ar[0] = a;
            ar[1] = mid;
            double minim = Float.MAX_VALUE;
            for (int i = 2; i < n; i++) {
                ar[i] = 2 * ar[i-1] - ar[i-2] + 2;
                if (minim > ar[i]){
                    minim = ar[i];
                }
            }
            if (minim >0.0001) {
                right = mid;

            } else if(minim<0){
                left = mid;
            }
            else{
                break;
            }

        }
        System.out.printf("%.2f",ar[n-1]);
    }
}
