import jdk.internal.joptsimple.internal.Strings.repeat
import java.util.concurrent.atomic.*
import java.util.Random

/**
 * @author Baronov Nikita
 */
open class TreiberStackWithElimination<E> : Stack<E> {
    private val stack = TreiberStack<E>()


    private val rendezvousSlots = AtomicReferenceArray<Any?>(ELIMINATION_ARRAY_SIZE)

    override fun push(element: E) {
        if (tryPushWithElimination(element)) return
        stack.push(element)
    }

    protected open fun tryPushWithElimination(element: E): Boolean {

        val randomIndex = Random().nextInt(ELIMINATION_ARRAY_SIZE)
        if (rendezvousSlots.compareAndSet(randomIndex, CELL_STATE_EMPTY, element)) {
            for (i in 0..ELIMINATION_WAIT_CYCLES) {
            }
            return rendezvousSlots.getAndSet(randomIndex, CELL_STATE_EMPTY) == CELL_STATE_RETRIEVED
        }
        return false
    }

    override fun pop(): E? = tryPopWithElimination() ?: stack.pop()

    private fun tryPopWithElimination(): E? {
        val randomIndex = Random().nextInt(ELIMINATION_ARRAY_SIZE)
        val element = rendezvousSlots.get(randomIndex)
        if (element == CELL_STATE_EMPTY || element == CELL_STATE_RETRIEVED) {
            return null
        }
        return if (rendezvousSlots.compareAndSet(randomIndex, element, CELL_STATE_RETRIEVED)) {
            element as E?
        } else {
            null
        }
    }

    companion object {
        private const val ELIMINATION_ARRAY_SIZE = 3 // Do not change!
        private const val ELIMINATION_WAIT_CYCLES = 1 // Do not change!

        // Initially, all cells are in EMPTY state.
        private val CELL_STATE_EMPTY = null

        // `tryPopElimination()` moves the cell state
        // to `RETRIEVED` if the cell contains an element.
        private val CELL_STATE_RETRIEVED = Any()
    }
}
