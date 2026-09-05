import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.withLock

/**
 * Bank implementation.
 *
 * Thread-safe implementation using fine-grained read-write locks.
 *
 * @author Baronov Nikita
 */
class BankImpl(n: Int) : Bank {
    private val accounts: Array<Account> = Array(n) { Account() }

    override val accountsCount: Int
        get() = accounts.size

    /**
     * Thread-safe implementation using read lock.
     */
    override fun amount(id: Int): Long {
        val acc = accounts[id]
        return acc.lock.readLock().withLock {
            acc.amount
        }
    }

    /**
     * Thread-safe implementation using read locks for all accounts.
     */
    override val totalAmount: Long
        get() {
            val snapshot = accounts.indices.map { accounts[it] }
            for (acc in snapshot) {
                acc.lock.readLock().lock()
            }
            try {
                return accounts.sumOf { it.amount }
            } finally {
                for (acc in snapshot.asReversed()) {
                    acc.lock.readLock().unlock()
                }
            }
        }

    /**
     * Thread-safe implementation using write lock.
     */
    override fun deposit(id: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }

        val acc = accounts[id]
        return acc.lock.writeLock().withLock {
            check(amount <= Bank.MAX_AMOUNT && acc.amount + amount <= Bank.MAX_AMOUNT) { "Overflow" }
            acc.amount += amount
            acc.amount
        }
    }

    /**
     * Thread-safe implementation using write lock.
     */
    override fun withdraw(id: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }

        val acc = accounts[id]
        return acc.lock.writeLock().withLock {
            check(acc.amount - amount >= 0) { "Underflow" }
            acc.amount -= amount
            acc.amount
        }
    }

    /**
     * Thread-safe implementation using write locks for two accounts.
     */
    override fun transfer(fromId: Int, toId: Int, amount: Long) {
        require(amount > 0) { "Invalid amount: $amount" }
        require(fromId != toId) { "fromId == toId" }

        val lowId = minOf(fromId, toId)
        val highId = maxOf(fromId, toId)

        val lowAcc = accounts[lowId]
        val highAcc = accounts[highId]

        lowAcc.lock.writeLock().lock()
        highAcc.lock.writeLock().lock()

        try {
            val fromAcc = accounts[fromId]
            val toAcc = accounts[toId]
            check(amount <= fromAcc.amount) { "Underflow" }
            check(amount <= Bank.MAX_AMOUNT && toAcc.amount + amount <= Bank.MAX_AMOUNT) { "Overflow" }
            fromAcc.amount -= amount
            toAcc.amount += amount
        } finally {
            highAcc.lock.writeLock().unlock()
            lowAcc.lock.writeLock().unlock()
        }
    }

    /**
     * Thread-safe implementation using write locks for multiple accounts.
     */
    override fun consolidate(fromIds: List<Int>, toId: Int) {
        require(fromIds.isNotEmpty()) { "empty fromIds" }
        require(fromIds.distinct() == fromIds) { "duplicates in fromIds" }
        require(toId !in fromIds) { "toId in fromIds" }

        val lockedIds = (fromIds + toId).sorted()
        for (id in lockedIds) {
            accounts[id].lock.writeLock().lock()
        }

        try {
            val sourceAccounts = fromIds.map { accounts[it] }
            val target = accounts[toId]
            val total = sourceAccounts.sumOf { it.amount }
            check(target.amount + total <= Bank.MAX_AMOUNT) { "Overflow" }

            for (src in sourceAccounts) {
                src.amount = 0
            }
            target.amount += total
        } finally {
            for (id in lockedIds.asReversed()) {
                accounts[id].lock.writeLock().unlock()
            }
        }
    }

    /**
     * Private account data structure with read-write lock.
     */
    class Account {
        /**
         * Amount of funds in this account.
         */
        var amount: Long = 0

        /**
         * Read-write lock for fine-grained synchronization.
         */
        var lock: ReentrantReadWriteLock = ReentrantReadWriteLock()
    }
}

