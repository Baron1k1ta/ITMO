import java.util.Scanner;


public class B3 {
    static int[] queue = new int[100];
    static int size = 0;
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        while (true){
            String string = input.next();
            if (string.equals("push")){
                string = input.next();
                System.out.println(push(Integer.parseInt(string)));
            }
            else if(string.equals("pop")){
                System.out.println(pop());
            }
            else if(string.equals("front")){
                System.out.println(front());
            }
            else if(string.equals("size")){
                System.out.println(size());
            }
            else if(string.equals("clear")){
                System.out.println(clear());
            }
            else if(string.equals("exit")){
                System.out.println(exit());
                break;
            }
        }


    }
    public static String push(int n){
        queue[size] = n;
        size++;
        return "ok";
    }
    public static int pop(){
        int result = queue[0];
        for (int i = 0; i< size-1; i++){
            queue[i] = queue[i+1];
        }
        size--;
        return result;
    }
    public static int front(){
        return queue[0];
    }
    public static int size(){
        return size;
    }
    public static String clear(){
        size = 0;
        return "ok";
    }
    public static String exit(){
        return "bye";
    }

}