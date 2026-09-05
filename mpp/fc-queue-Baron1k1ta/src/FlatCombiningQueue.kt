import java.util.concurrent.*
import java.util.concurrent.atomic.*

/**
 * @author Baronov Nikita
 */

class FlatCombiningQueue<E> : Queue<E> {
    private val queue = ArrayDeque<E>() // sequential queue
    private val combinerLock = AtomicBoolean(false) // unlocked initially
    private val tasksForCombiner = AtomicReferenceArray<Any?>(TASKS_FOR_COMBINER_SIZE)

    @Suppress("UNCHECKED_CAST")
    override fun enqueue(element: E) {
        var announced = false
        val cell = randomCellIndex()

        while (true) {
            if (combinerLock.compareAndSet(false, true)) {
                try {
                    if (announced) {
                        val task = tasksForCombiner.getAndSet(cell, null)
                        if (task is Result<*>) return
                    }
                    queue.addLast(element)
                    for (i in 0 until TASKS_FOR_COMBINER_SIZE) {
                        when (val op = tasksForCombiner.get(i)) {
                            is Dequeue -> {
                                val res = queue.removeFirstOrNull()
                                tasksForCombiner.set(i, Result(res))
                            }

                            null -> {}
                            else -> {
                                if (op !is Result<*>) {
                                    queue.addLast(op as E)
                                    tasksForCombiner.set(i, Result(op))
                                }
                            }
                        }
                    }
                    return
                } finally {
                    combinerLock.set(false)
                }
            }

            if (!announced && tasksForCombiner.compareAndSet(cell, null, element)) {
                announced = true
            }
        }
    }


    @Suppress("UNCHECKED_CAST")
    override fun dequeue(): E? {
        var announced = false
        val cell = randomCellIndex()

        while (true) {
            if (combinerLock.compareAndSet(false, true)) {
                try {
                    if (announced) {
                        val op = tasksForCombiner.getAndSet(cell, null)
                        if (op is Result<*>) {
                            return op.value as E?
                        }
                    }
                    return queue.removeFirstOrNull()
                } finally {
                    combinerLock.set(false)
                }
            }
            if (!announced && tasksForCombiner.compareAndSet(cell, null, Dequeue)) {
                announced = true
            }
        }
    }


    private fun randomCellIndex(): Int =
        ThreadLocalRandom.current().nextInt(tasksForCombiner.length())
}

private const val TASKS_FOR_COMBINER_SIZE = 3 // Do not change this constant!

private object Dequeue

private class Result<V>(
    val value: V
)