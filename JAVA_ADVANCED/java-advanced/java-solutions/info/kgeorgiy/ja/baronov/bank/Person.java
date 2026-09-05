package info.kgeorgiy.ja.baronov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The {@code Person} interface represents a person in the banking system.
 */
public interface Person extends Remote {

    /**
     * Returns the first name of the person.
     */
    String name() throws RemoteException;

    /**
     * Returns the last name of the person.
     */
    String surname() throws RemoteException;

    /**
     * Returns the unique passport ID of the person.
     */
    int passportID() throws RemoteException;

}
