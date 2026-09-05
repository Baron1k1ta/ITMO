package info.kgeorgiy.ja.baronov.bank.tests;

import info.kgeorgiy.ja.baronov.bank.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;


public class Tests {
    private static Bank bank;
    private static Registry testRegistry;
    private static final int PORT = 8080;


    @BeforeAll
    static void beforeAll() throws RemoteException {
        testRegistry = LocateRegistry.createRegistry(PORT);
    }

    @BeforeEach
    void beforeEach() throws RemoteException {
        bank = new RemoteBank(PORT);
        testRegistry.rebind(
                "testBank",
                UnicastRemoteObject.exportObject(bank, PORT));
    }

    @Test
    void testCreatePerson() throws RemoteException {
        String name = "John";
        String surname = "Doe";
        int passportId = 12345;

        Person person = bank.createPerson(name, surname, passportId);

        Assertions.assertNotNull(person);
        Assertions.assertEquals(name, person.name());
        Assertions.assertEquals(surname, person.surname());
        Assertions.assertEquals(passportId, person.passportID());
    }

    @Test
    void testCreateAccount() throws RemoteException {
        String id = "12345:123";

        bank.createPerson("Bob", "Brown", 12345);

        Account account = bank.createAccount(id);

        Assertions.assertNotNull(account);
        Assertions.assertEquals(id, account.getId());
        Assertions.assertEquals(0, account.getAmount());
    }

    @Test
    void testAddMoneyToAccount() throws RemoteException {
        String id = "12345:123";

        bank.createPerson("Bob", "Brown", 12345);
        Account account = bank.createAccount(id);

        int initialAmount = account.getAmount();
        int amountToAdd = 500;
        account.setAmount(initialAmount + amountToAdd);

        Assertions.assertEquals(initialAmount + amountToAdd, account.getAmount());
    }

    @Test
    void testGetAccount() throws RemoteException {
        String id = "12345:123";

        bank.createPerson("Bob", "Brown", 12345);
        bank.createAccount(id);

        Account retrievedAccount = bank.getAccount(id);

        Assertions.assertNotNull(retrievedAccount);
        Assertions.assertEquals(id, retrievedAccount.getId());
    }

    @Test
    void testAccountExists() throws RemoteException {
        String id = "12345:123";

        bank.createPerson("Bob", "Brown", 12345);
        bank.createAccount(id);

        Account account = bank.createAccount(id);

        Assertions.assertNotNull(account);
        Assertions.assertEquals(id, account.getId());
    }

    @Test
    void testCreateAccountWithInvalidId() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> bank.createAccount(""));
    }

    @Test
    void testCreatePersonWithInvalidData() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> bank.createPerson("", "Brown", 12345));
    }

    @Test
    void testCreateAndGetRemotePerson() throws RemoteException {
        String name = "Adam";
        String surname = "Smith";
        int passportId = 12346;

        bank.createPerson(name, surname, passportId);

        Person remotePerson = bank.getRemotePerson(passportId);
        Assertions.assertNotNull(remotePerson);
        Assertions.assertEquals(name, remotePerson.name());
        Assertions.assertEquals(surname, remotePerson.surname());
        Assertions.assertEquals(passportId, remotePerson.passportID());
    }

    @Test
    void testCreateAndGetLocalPerson() throws RemoteException {
        String name = "Jerry";
        String surname = "Jones";
        int passportId = 12347;

        bank.createPerson(name, surname, passportId);
        String accountId = passportId + ":001";
        bank.createAccount(accountId);

        LocalPerson localPerson = bank.getLocalPerson(passportId);
        Assertions.assertNotNull(localPerson);
        Assertions.assertEquals(name, localPerson.name());
        Assertions.assertEquals(surname, localPerson.surname());
        Assertions.assertEquals(passportId, localPerson.passportID());

        Assertions.assertNotNull(localPerson);
        Assertions.assertTrue(localPerson.accounts().containsKey(accountId));
    }

    @Test
    void testSimultaneousRemoteAndLocalPerson() throws RemoteException {
        String name = "f";
        String surname = "a";
        int passportId = 12348;

        bank.createPerson(name, surname, passportId);

        String accountId = passportId + ":001";
        bank.createAccount(accountId);

        Person remotePerson = bank.getRemotePerson(passportId);
        LocalPerson localPerson = bank.getLocalPerson(passportId);

        Assertions.assertNotNull(remotePerson);
        Assertions.assertEquals(name, remotePerson.name());
        Assertions.assertEquals(surname, remotePerson.surname());
        Assertions.assertEquals(passportId, remotePerson.passportID());

        Assertions.assertNotNull(localPerson);
        Assertions.assertEquals(name, localPerson.name());
        Assertions.assertEquals(surname, localPerson.surname());
        Assertions.assertEquals(passportId, localPerson.passportID());

        Assertions.assertTrue(localPerson.accounts().containsKey(accountId));
    }


    @Test
    void testConcurrentAccountCreation() throws Exception {
        final String id = "99999:100";
        bank.createPerson("Multi", "Thread", 99999);

        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        Set<Account> created = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    Account acc = bank.createAccount(id);
                    created.add(acc);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        start.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Assertions.assertEquals(1, created.size(), "Account must be created only once");
    }

    @Test
    void testConcurrentDeposits() throws Exception {
        final String id = "88888:200";
        bank.createPerson("Concurrent", "User", 88888);
        Account account = bank.createAccount(id);

        int threads = 10;
        int depositsPerThread = 100;
        int amountPerDeposit = 5;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    for (int j = 0; j < depositsPerThread; j++) {
                        account.addAmount(amountPerDeposit);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        start.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        int expected = threads * depositsPerThread * amountPerDeposit;
        Assertions.assertEquals(expected, account.getAmount(),
                "Final amount should be " + expected);
    }

    @Test
    void testConcurrentPersonCreation() throws Exception {
        final int passportId = 77777;
        final String name = "Thread";
        final String surname = "Safe";

        int threads = 20;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        Set<Person> created = ConcurrentHashMap.newKeySet();

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    Person p = bank.createPerson(name, surname, passportId);
                    created.add(p);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        start.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Assertions.assertEquals(1, created.size(), "Person must be created only once");
        Person person = created.iterator().next();
        Assertions.assertEquals(name, person.name());
        Assertions.assertEquals(surname, person.surname());
        Assertions.assertEquals(passportId, person.passportID());
    }

    @Test
    void testConcurrentGetPersonsAndAccounts() throws Exception {
        final int passportId = 66666;
        bank.createPerson("Mix", "AndMatch", passportId);

        String id1 = passportId + ":A";
        String id2 = passportId + ":B";
        bank.createAccount(id1);
        bank.createAccount(id2);

        int threads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch start = new CountDownLatch(1);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    start.await();
                    if (ThreadLocalRandom.current().nextBoolean()) {
                        Person rp = bank.getRemotePerson(passportId);
                        Assertions.assertNotNull(rp);
                        Assertions.assertEquals(passportId, rp.passportID());
                    } else {
                        LocalPerson lp = bank.getLocalPerson(passportId);
                        Assertions.assertNotNull(lp);
                        Assertions.assertTrue(lp.accounts().containsKey(id1));
                        Assertions.assertTrue(lp.accounts().containsKey(id2));
                        LocalAccount la = lp.accounts().get(id1);
                        la.setAmount(999);
                        int remoteAmount = bank.getAccount(id1).getAmount();
                        Assertions.assertNotEquals(999, remoteAmount);
                    }
                } catch (Exception e) {
                    errors.add(e);
                }
            });
        }

        start.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        Assertions.assertTrue(errors.isEmpty(), "Errors during concurrent getPersons: " + errors);
    }

    @Test
    void testIsolationBetweenDifferentPersons() throws Exception {
        int persons = 5;
        int threadsPerPerson = 5;
        ExecutorService executor = Executors.newFixedThreadPool(persons * threadsPerPerson);
        CountDownLatch start = new CountDownLatch(1);
        List<Exception> errors = Collections.synchronizedList(new ArrayList<>());

        for (int pid = 1; pid <= persons; pid++) {
            bank.createPerson("User" + pid, "Test", pid);
            String accId = pid + ":0";
            bank.createAccount(accId);

            for (int t = 0; t < threadsPerPerson; t++) {
                int finalPid = pid;
                executor.submit(() -> {
                    try {
                        start.await();
                        Account acc = bank.getAccount(accId);
                        acc.addAmount(finalPid);
                    } catch (Exception e) {
                        errors.add(e);
                    }
                });
            }
        }

        start.countDown();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        for (int pid = 1; pid <= persons; pid++) {
            String accId = pid + ":0";
            int expected = pid * threadsPerPerson;
            int actual = bank.getAccount(accId).getAmount();
            Assertions.assertEquals(expected, actual,
                    "Person " + pid + " final amount mismatch");
        }
        Assertions.assertTrue(errors.isEmpty(), "Errors during isolation test: " + errors);
    }

}

// note -- нужны многопоточные тесты