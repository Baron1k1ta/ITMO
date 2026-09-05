import java.util.concurrent.atomic.AtomicIntegerArray
import java.util.concurrent.atomic.AtomicReference

/**
 * Int-to-Int hash map with open addressing and linear probes.
 */
class IntIntHashMap {
    private val core: AtomicReference<Core> = AtomicReference(Core(INITIAL_CAPACITY))

    /**
     * Returns value for the corresponding key or zero if this key is not present.
     *
     * @param key a positive key.
     * @return value for the corresponding or zero if this key is not present.
     * @throws IllegalArgumentException if key is not positive.
     */
    operator fun get(key: Int): Int {
        require(key > 0) { "Key must be positive: $key" }
        return toValue(core.get().getInternal(key))
    }

    /**
     * Changes value for the corresponding key and returns old value or zero if key was not present.
     *
     * @param key   a positive key.
     * @param value a positive value.
     * @return old value or zero if this key was not present.
     * @throws IllegalArgumentException if key or value are not positive, or value is equal to
     * [Integer.MAX_VALUE] which is reserved.
     */
    fun put(key: Int, value: Int): Int {
        require(key > 0) { "Key must be positive: $key" }
        require(isValue(value)) { "Invalid value: $value" }
        return toValue(putAndRehashWhileNeeded(key, value))
    }

    /**
     * Removes value for the corresponding key and returns old value or zero if key was not present.
     *
     * @param key a positive key.
     * @return old value or zero if this key was not present.
     * @throws IllegalArgumentException if key is not positive.
     */
    fun remove(key: Int): Int {
        require(key > 0) { "Key must be positive: $key" }
        return toValue(putAndRehashWhileNeeded(key, REMOVED_VALUE))
    }

    private fun putAndRehashWhileNeeded(key: Int, value: Int): Int {
        while (true) {
            val curCore = core.get()
            val oldValue = curCore.putInternal(key, value)
            if (oldValue != NEEDS_REHASH) return oldValue

            core.compareAndSet(curCore, curCore.rehash())
        }
    }

    private class Core(capacity: Int) {
        // Pairs of <key, value> here, the actual
        // size of the map is twice as big.
        private val map: AtomicIntegerArray = AtomicIntegerArray(2 * capacity)
        private val shift: Int
        private val next: AtomicReference<Core> = AtomicReference(this)

        init {
            val mask = capacity - 1
            assert(mask > 0 && mask and capacity == 0) { "Capacity must be power of 2: $capacity" }
            shift = 32 - Integer.bitCount(mask)
        }

        fun getInternal(key: Int): Int {
            var index = index(key)
            var probes = 0

            while (true) {
                val currentKey = map.get(index)
                when {
                    currentKey == key -> break
                    currentKey == NULL_KEY -> return NULL_VALUE
                    ++probes >= MAX_PROBES -> return NULL_VALUE
                    else -> index = nextIndex(index)
                }
            }

            val storedValue = map.get(index + 1)
            if (storedValue == DEL_VALUE) {
                return next.get().getInternal(key)
            }

            return if (isNeedToBeInverted(storedValue)) invert(storedValue) else storedValue
        }

        fun putInternal(key: Int, value: Int): Int {
            var index = index(key)
            var probes = 0

            while (true) {
                map.compareAndSet(index, NULL_KEY, key)
                if (map.get(index) == key) break

                if (++probes >= MAX_PROBES) return NEEDS_REHASH
                index = nextIndex(index)
            }

            while (true) {
                when (val oldValue = map.get(index + 1)) {
                    DEL_VALUE -> return next.get().putInternal(key, value)
                    else -> {
                        if (isNeedToBeInverted(oldValue)) {
                            next.get().helpMove(key, invert(oldValue))
                            map.compareAndSet(index + 1, oldValue, DEL_VALUE)
                            continue
                        }

                        if (map.compareAndSet(index + 1, oldValue, value)) {
                            return oldValue
                        }
                    }
                }
            }
        }

        fun rehash(): Core {
            if (next.get() == this) {
                next.compareAndSet(this, Core(map.length()))
            }

            for (index in 0 until map.length() step 2) {
                while (true) {
                    when (val oldValue = map.get(index + 1)) {
                        DEL_VALUE -> break
                        NULL_VALUE, REMOVED_VALUE -> {
                            if (map.compareAndSet(index + 1, oldValue, DEL_VALUE)) break
                        }
                        else -> {
                            if (isNeedToBeInverted(oldValue)) {
                                next.get().helpMove(map.get(index), invert(oldValue))
                                map.compareAndSet(index + 1, oldValue, DEL_VALUE)
                                continue
                            }
                            map.compareAndSet(index + 1, oldValue, invert(oldValue))
                        }
                    }
                }
            }

            return next.get()
        }

        private fun helpMove(key: Int, value: Int) {
            var index = index(key)

            while (true) {
                map.compareAndSet(index, NULL_KEY, key)
                if (map.get(index) == key) {
                    map.compareAndSet(index + 1, NULL_VALUE, value)
                    return
                }

                index = nextIndex(index)
            }
        }

        private fun nextIndex(index: Int): Int = (if (index == 0) map.length() else index) - 2

        /**
         * Returns an initial index in map to look for a given key.
         */
        private fun index(key: Int): Int = (key * MAGIC ushr shift) * 2
    }
}

private const val MAGIC = -0x61c88647 // golden ratio
private const val INITIAL_CAPACITY = 2 // !!! DO NOT CHANGE INITIAL CAPACITY !!!
private const val MAX_PROBES = 8 // max number of probes to find an item
private const val NULL_KEY = 0 // missing key (initial value)
private const val NULL_VALUE = 0 // missing value (initial value)
private const val REMOVED_VALUE = Int.MIN_VALUE + 1
private const val DEL_VALUE = Int.MAX_VALUE // mark for removed value
private const val NEEDS_REHASH = Int.MIN_VALUE // returned by `putInternal` to indicate that rehash is needed

// Checks is the value is in the range of allowed values
private fun isValue(value: Int): Boolean = value in (1 until DEL_VALUE)

// Converts internal value to the public results of the methods
private fun toValue(value: Int): Int = if (isValue(value)) value else 0

private fun invert(value: Int): Int = -value

private fun isNeedToBeInverted(value: Int): Boolean = -Int.MAX_VALUE < value && value < 0