package info.kgeorgiy.ja.baronov.bank;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

/**
 * The {@code Client} class is a utility class
 */
public final class Client {
    /** Utility class. */
    private Client() {}

    /**
     * The main method which serves as the entry point for the client application.
     * @param args command-line arguments:
     *             <ul>
     *               <li>args[0] - First name of the person</li>
     *               <li>args[1] - Last name of the person</li>
     *               <li>args[2] - Passport ID of the person (integer)</li>
     *               <li>args[3] - Account ID</li>
     *               <li>args[4] - Amount of money to add to the account (integer)</li>
     *             </ul>
     */
    public static void main(final String... args) throws RemoteException {
        if (args.length != 5 || args[0] == null || args[1] == null || args[2] == null || args[3] == null || args[4] == null) {
            System.err.println("Wrong arguments");
            return;
        }
        final Bank bank;
        try {
            bank = (Bank) Naming.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.out.println("Bank is not bound");
            return;
        } catch (final MalformedURLException e) {
            System.out.println("Bank URL is invalid");
            return;
        }


        bank.createPerson(args[0], args[1], Integer.parseInt(args[2]));
        final String id = args[3];

        Account account = bank.getAccount(id);
        if (account == null) {
            System.out.println("Creating account");
            account = bank.createAccount(id);
        } else {
            System.out.println("Account already exists");
        }
        System.out.println("Account id: " + account.getId());
        System.out.println("Money: " + account.getAmount());
        System.out.println("Adding money");
        account.setAmount(account.getAmount() + Integer.parseInt(args[4]));
        System.out.println("Money: " + account.getAmount());
    }
}
