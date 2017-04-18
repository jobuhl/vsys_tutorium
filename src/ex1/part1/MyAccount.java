package ex1.part1;

/**
 * Created by Jojo on 30.03.17.
 */
public class MyAccount {

    private static int number;

    public static int getNumber() {
        return number;
    }

    public static void setNumber(int number) {
        MyAccount.number = number;
    }

    public static int setNumber2(int number, int b) {
        MyAccount.number = number + b;

        return MyAccount.number;
        //TEST
    }
}
