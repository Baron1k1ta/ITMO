import java.util.Scanner;


public class G3 {
    static int[] queue1 = new int[10];
    static int[] queue2 = new int[10];
    static int size1 = 0;
    static int size2 = 0;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        int counter = 0;
        for (int i = 0; i < 5; i++) {
            push1(Integer.parseInt(input.next()));
        }
        for (int i = 0; i < 5; i++) {
            push2(Integer.parseInt(input.next()));
        }
        while (true) {
            int card1 = pop1();
            int card2 = pop2();
            if (card1 + 9 == card2) {
                push1(card1);
                push1(card2);

            } else if (card2 + 9 == card1){
                push2(card1);
                push2(card2);
            } else if (card1 > card2){
                push1(card1);
                push1(card2);

            }else{
                push2(card1);
                push2(card2);
            }
            counter++;
            if(counter == 1000_000){
                System.out.println("botva");
                break;
            }
            if (size1 == 0){
                System.out.println("second ");
                System.out.print(counter);
                break;
            }
            if (size2 == 0){
                System.out.println("first");
                System.out.print(counter);
                break;
            }
        }


    }

    public static void push1(int n) {
        queue1[size1] = n;
        size1++;
    }

    public static void push2(int n) {
        queue2[size2] = n;
        size2++;
    }

    public static int pop1() {
        int result = queue1[0];
        for (int i = 0; i < size1 - 1; i++) {
            queue1[i] = queue1[i + 1];
        }
        size1--;
        return result;
    }

    public static int pop2() {
        int result = queue2[0];
        for (int i = 0; i < size2 - 1; i++) {
            queue2[i] = queue2[i + 1];
        }
        size2--;
        return result;
    }


}