import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi

//@author Baronov Nikita

@Suppress("UNCHECKED_CAST")
@OptIn(ExperimentalAtomicApi::class)
class AtomicArray<E : Any>(size: Int, initialValue: E) {
    private val a: Array<Ref<E>> = Array(size) { Ref(initialValue) }

    class Ref<E : Any>(initial: E) {
        val v: AtomicReference<Any> = AtomicReference(initial)

        fun get(): E {
            while (true) {
                when (val cur = v.load()) {
                    is Descriptor -> {
                        cur.complete()
                    }
                    else -> return cur as E
                }
            }
        }

        fun casRaw(expect: Any, update: Any): Boolean {
            while (true) {
                when (val cur = v.load()) {
                    expect -> {
                        if (v.compareAndSet(expect, update)) return true
                    }
                    is Descriptor -> cur.complete()
                    else -> return false
                }
            }
        }
    }

    enum class OUTCOME { UNDECIDED, FAILED, SUCCESS }

    abstract class Descriptor {
        abstract fun complete(): Boolean
    }

    class RDCSSDescriptor<E : Any>(
        val a: Ref<E>,
        val expectA: Any,
        val updateA: CASNDescriptor<E>
    ) : Descriptor() {

        val outcome: AtomicReference<OUTCOME> = AtomicReference(OUTCOME.UNDECIDED)

        override fun complete(): Boolean {
            if (updateA.outcome.load() == OUTCOME.UNDECIDED) {
                outcome.compareAndSet(OUTCOME.UNDECIDED, OUTCOME.SUCCESS)
            } else {
                outcome.compareAndSet(OUTCOME.UNDECIDED, OUTCOME.FAILED)
            }

            return when (outcome.load()) {
                OUTCOME.SUCCESS -> {
                    a.v.compareAndSet(this, updateA)
                    true
                }
                OUTCOME.FAILED -> {
                    a.v.compareAndSet(this, expectA)
                    false
                }
                else -> false
            }
        }
    }

    class CASNDescriptor<E : Any>(
        val a1: Ref<E>, val expect1: E, val update1: E,
        val a2: Ref<E>, val expect2: E, val update2: E
    ) : Descriptor() {

        val outcome: AtomicReference<OUTCOME> = AtomicReference(OUTCOME.UNDECIDED)

        override fun complete(): Boolean {
            if (a2.v.load() != this) {
                val desc2 = RDCSSDescriptor(a2, expect2 as Any, this)
                if (a2.casRaw(expect2 as Any, desc2)) {
                    desc2.complete()
                }
            }

            val secondOutcome = when (val secondVal = a2.v.load()) {
                this -> OUTCOME.SUCCESS
                is RDCSSDescriptor<*> -> {
                    (secondVal as RDCSSDescriptor<E>).complete()
                    if (a2.v.load() == this) OUTCOME.SUCCESS else OUTCOME.FAILED
                }
                else -> OUTCOME.FAILED
            }

            if (secondOutcome == OUTCOME.SUCCESS) {
                outcome.compareAndSet(OUTCOME.UNDECIDED, OUTCOME.SUCCESS)
            } else {
                outcome.compareAndSet(OUTCOME.UNDECIDED, OUTCOME.FAILED)
            }

            return when (outcome.load()) {
                OUTCOME.SUCCESS -> {
                    a1.v.compareAndSet(this, update1 as Any)
                    a2.v.compareAndSet(this, update2 as Any)
                    true
                }
                OUTCOME.FAILED -> {
                    a1.v.compareAndSet(this, expect1 as Any)
                    false
                }
                else -> false
            }
        }
    }

    fun get(index: Int): E = a[index].get()

    fun cas(index: Int, expected: E, update: E): Boolean {
        while (true) {
            when (val cur = a[index].v.load()) {
                expected -> {
                    if (a[index].v.compareAndSet(expected, update)) return true
                }
                is Descriptor -> cur.complete()
                else -> return false
            }
        }
    }

    fun cas2(
        index1: Int, expected1: E, update1: E,
        index2: Int, expected2: E, update2: E
    ): Boolean {
        if (index1 == index2) {
            if (expected1 != expected2) return false
            return cas(index1, expected1, update2)
        }

        val (firstIdx, secondIdx) = if (index1 < index2) {
            Pair(index1, index2)
        } else {
            Pair(index2, index1)
        }

        val (firstExp, firstUpd, secondExp, secondUpd) = if (index1 < index2) {
            Quad(expected1, update1, expected2, update2)
        } else {
            Quad(expected2, update2, expected1, update1)
        }

        val firstRef = a[firstIdx]
        val secondRef = a[secondIdx]

        val desc = CASNDescriptor(
            firstRef, firstExp, firstUpd,
            secondRef, secondExp, secondUpd
        )

        return if (firstRef.casRaw(firstExp as Any, desc)) {
            desc.complete()
        } else {
            false
        }
    }

    private data class Quad<A, B, C, D>(
        val first: A,
        val second: B,
        val third: C,
        val fourth: D
    )
}
