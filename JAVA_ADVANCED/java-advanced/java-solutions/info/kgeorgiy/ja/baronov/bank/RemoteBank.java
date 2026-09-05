package info.kgeorgiy.ja.baronov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The {@code RemoteBank} class implements the {@link Bank} interface and represents
 */
public class RemoteBank implements Bank {
    private final int port;
    private final ConcurrentMap<Integer, PersonAccounts> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Person> people = new ConcurrentHashMap<>();


    public RemoteBank(final int port) {
        this.port = port;
    }

    @Override
    public Account createAccount(String id) throws RemoteException {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        int passportID = Integer.parseInt(id.split(":")[0]);
        System.out.println("Creating or retrieving account " + id);

        PersonAccounts personAccounts =
                accounts.computeIfAbsent(passportID, k -> new PersonAccounts());

        Account account = personAccounts.getAll().computeIfAbsent(id, key -> {
            try {
                RemoteAccount newAccount = new RemoteAccount(key);
                UnicastRemoteObject.exportObject(newAccount, port);
                return newAccount;
            } catch (RemoteException e) {
                throw new RuntimeException("Failed to export account " + key, e);
            }
        });

        return account;
    }

    @Override
    public Account getAccount(final String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        System.out.println("Retrieving account " + id);
        int passportID = Integer.parseInt(id.split(":")[0]);
        return accounts.get(passportID).get(id);
    }

    @Override
    public Person createPerson(String name, String surname, int passportId) throws RemoteException {
        if (name == null || name.isEmpty() || surname == null || surname.isEmpty() || passportId <= 0) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        System.out.println("Creating person " + passportId);
        final Person person = new RemotePerson(name, surname, passportId);
        if (people.putIfAbsent(passportId, person) == null) {
            UnicastRemoteObject.exportObject(person, port);
            return person;
        }
        return getRemotePerson(passportId);
    }

    @Override
    public Person getRemotePerson(int PassportId) throws RemoteException {
        if (PassportId <= 0) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        return people.get(PassportId);
    }

    @Override
    public LocalPerson getLocalPerson(int PassportId) throws RemoteException {
        if (PassportId <= 0) {
            throw new IllegalArgumentException("Account ID cannot be null or empty");
        }
        final Person person = getRemotePerson(PassportId);
        final Map<String, Account> personAccounts = accounts.get(PassportId).getAll();
        final Map<String, LocalAccount> localAccounts = new HashMap<>();


        for (Map.Entry<String, Account> account : personAccounts.entrySet()) {
            final LocalAccount localAccount = new LocalAccount(account.getValue().getId());
            localAccount.setAmount(account.getValue().getAmount());
            localAccounts.put(account.getKey(), localAccount);
        }


        return new LocalPerson(person.name(), person.surname(), person.passportID(), localAccounts);
    }

}
