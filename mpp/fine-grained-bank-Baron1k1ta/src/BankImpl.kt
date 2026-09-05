/**
 * Bank implementation.
 *
 * :TODO: This implementation has to be made thread-safe.
 *
 * @author :Baronov Nikita
 */

import java.util.concurrent.locks.ReentrantLock

class BankImpl(n: Int) : Bank {
    private val accounts: Array<Account> = Array(n) { Account() }

    override val accountsCount: Int
        get() = accounts.size

    /**
     * :TODO: This method has to be made thread-safe.
     */
    override fun amount(id: Int): Long {
        val acc = accounts[id]
        acc.lock.lock()
        return try {
            acc.amount
        } finally {
            acc.lock.unlock()
        }
    }

    /**
     * :TODO: This method has to be made thread-safe.
     */
    override val totalAmount: Long
        get() {
            for (i in accounts.indices) accounts[i].lock.lock()
            return try {
                var sum = 0L
                for (acc in accounts) sum += acc.amount
                sum
            } finally {
                for (i in accounts.indices.reversed()) accounts[i].lock.unlock()
            }
        }

    /**
     * :TODO: This method has to be made thread-safe.
     */
    override fun deposit(id: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val account = accounts[id]
        account.lock.lock()
        return try {
            check(!(amount > Bank.MAX_AMOUNT || account.amount + amount > Bank.MAX_AMOUNT)) { "Overflow" }
            account.amount += amount
            account.amount
        } finally {
            account.lock.unlock()
        }
    }

    /**
     * :TODO: This method has to be made thread-safe.
     */
    override fun withdraw(id: Int, amount: Long): Long {
        require(amount > 0) { "Invalid amount: $amount" }
        val account = accounts[id]
        account.lock.lock()
        return try{
            check(account.amount - amount >= 0) { "Underflow" }
            account.amount -= amount
            account.amount
        }finally{
            account.lock.unlock()
        }

    }

    /**
     * :TODO: This method has to be made thread-safe.
     */
    override fun transfer(fromId: Int, toId: Int, amount: Long) {
        require(amount > 0) { "Invalid amount: $amount" }
        require(fromId != toId) { "fromId == toId" }
        val from = accounts[fromId]
        val to = accounts[toId]
        val firstLock = if (fromId < toId) from.lock else to.lock
        val secondLock = if (fromId < toId) to.lock else from.lock
        try{
            firstLock.lock()
            secondLock.lock()
            check(amount <= from.amount) { "Underflow" }
            check(!(amount > Bank.MAX_AMOUNT || to.amount + amount > Bank.MAX_AMOUNT)) { "Overflow" }
            from.amount -= amount
            to.amount += amount
        }finally {
            firstLock.unlock()
            secondLock.unlock()
        }

    }

    /**
     * Private account data structure.
     */
    class Account {
        /**
         * Amount of funds in this account.
         */
        val lock: ReentrantLock = ReentrantLock()
        var amount: Long = 0
    }
}