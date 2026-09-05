import java.util.concurrent.atomic.*

/**
 * Implementation of the Michael-Scott queue algorithm.
 *
 * @author :TODO: Baronov Nikita
 */
class MichaelScottQueue<E> {
    private val head: AtomicReference<Node<E>>
    private val tail: AtomicReference<Node<E>>

    init {
        val dummy = Node<E>(null)
        head = AtomicReference(dummy)
        tail = AtomicReference(dummy)
    }

    fun enqueue(element: E) {
        val node = Node(element)
        while(true){
            val last = tail.get()
            val next = last.next.get()
            if (last == tail.get()){
                if (next == null){
                    if (last.next.compareAndSet(next, node)){
                        tail.compareAndSet(last, node)
                        return
                    }
                }else{
                    tail.compareAndSet(last, next)
                }
            }
        }
    }

    fun dequeue(): E? {
        while (true) {
            val first = head.get()
            val last = tail.get()
            val next = first.next.get()
            if (first == head.get()) {
                if (next == null) {
                    return null
                }
                if (first == last) {
                    tail.compareAndSet(last, next)
                } else {
                    val value = next.element
                    if (head.compareAndSet(first, next)) {
                        next.element = null
                        return value
                    }
                }
            }
        }
    }

    // FOR TEST PURPOSE, DO NOT CHANGE IT.
    fun validate() {
        check(tail.get().next.get() == null) {
            "At the end of the execution, `tail.next` must be `null`"
        }
        check(head.get().element == null) {
            "At the end of the execution, the dummy node shouldn't store an element"
        }
    }

    private class Node<E>(
        var element: E?
    ) {
        val next = AtomicReference<Node<E>?>(null)
    }
}
