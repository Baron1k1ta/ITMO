import java.util.Scanner;


public class A3 {
    static int[] stack = new int[100];
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
            else if(string.equals("back")){
                System.out.println(back());
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
        stack[size] = n;
        size++;
        return "ok";
    }
    public static int pop(){
        size--;
        return stack[size];
    }
    public static int back(){
        return stack[size-1];
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