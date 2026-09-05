import java.util.concurrent.atomic.*

/**
 * @author Baronov Nikita
 */
class TreiberStack<E> : Stack<E> {
    // Initially, the stack is empty.
    private val top = AtomicReference<Node<E>?>(null)

    override fun push(element: E) {
        val newNode = Node(element, null)
        while (true) {
            val curTop = top.get()
            newNode.next = curTop
            if (top.compareAndSet(curTop, newNode)) {
                return
            }
        }
    }

    override fun pop(): E? {
        while (true) {
            val curTop = top.get() ?: return null
            val next = curTop.next
            if (top.compareAndSet(curTop, next)) {
                return curTop.element
            }
        }
    }

    private class Node<E>(
        val element: E,
        var next: Node<E>?
    )
}
