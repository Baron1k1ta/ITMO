package info.kgeorgiy.ja.baronov.bank;

import java.io.Serializable;
/**
 * The {@code LocalAccount} class represents a local implementation of an account
 */
public class LocalAccount implements Account, Serializable {
    private final String id;
    private int amount;

    /**
     * Constructs a new {@code LocalAccount} with the specified account ID.
     * @param id the unique identifier of the account
     */
    public LocalAccount(final String id) {
        this.id = id;
        amount = 0;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public synchronized int getAmount() {
        System.out.println("Getting amount of money for account " + id);
        return amount;
    }

    @Override
    public synchronized void setAmount(final int amount) {
        System.out.println("Setting amount of money for account " + id);
        this.amount = amount;
    }

    @Override
    public synchronized void addAmount(final int delta) {
        amount += delta;
    }
}
