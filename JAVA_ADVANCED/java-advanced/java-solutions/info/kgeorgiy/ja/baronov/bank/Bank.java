package info.kgeorgiy.ja.baronov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Bank extends Remote {
    /**
     * Creates a new account with specified identifier if it does not already exist.
     * @param id account id
     * @return created or existing account.
     */
    Account createAccount(String id) throws RemoteException;

    /**
     * Returns account by identifier.
     * @param id account id
     * @return account with specified identifier or {@code null} if such account does not exist.
     */
    Account getAccount(String id) throws RemoteException;

    /**
     * Creates a new person in the bank
     * @param name person's first name
     * @param surname person's last name
     * @param passportId person's unique passport identifier
     * @return created or existing person
     */
    Person createPerson(String name, String surname, int passportId) throws RemoteException;

    /**
     * Finds person by passportID
     * @param passportId person's unique passport identifier
     * @return remote person object with the specified passport ID
     */
    Person getRemotePerson(int passportId) throws RemoteException;

    /**
     * Finds person by passportID
     * @param passportId person's unique passport identifier
     * @return local person object with the specified passport ID
     */
    LocalPerson getLocalPerson(int passportId) throws RemoteException;
}
