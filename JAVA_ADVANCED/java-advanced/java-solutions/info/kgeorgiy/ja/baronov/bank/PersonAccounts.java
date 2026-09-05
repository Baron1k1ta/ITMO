package info.kgeorgiy.ja.baronov.bank;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The {@code PersonAccounts} class represents a container for managing a person's accounts.
 */
public class PersonAccounts {

    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();

    /**
     * Retrieves the account with the specified ID.
     * @param id the unique identifier of the account
     */
    public Account get(String id) {
        return accounts.get(id);
    }

    /**
     * Adds the specified account to the collection of accounts.
     * @param account the account to be added
     */
    public void add(Account account) {
        try {
            accounts.put(account.getId(), account);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a {@link ConcurrentMap} of all accounts associated with this person.
     */
    public ConcurrentMap<String, Account> getAll() {
        return accounts;
    }
}

