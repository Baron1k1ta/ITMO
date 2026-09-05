import java.util.concurrent.atomic.AtomicReference
// Baronov Nikita

class Solution(val env: Environment) : Lock<Solution.Node> {
    // todo: необходимые поля (val, используем AtomicReference)
    private val tail = AtomicReference<Node?>(null)

    override fun lock(): Node {
        val my = Node()
        val pred = tail.getAndSet(my)
        if (pred != null) {
            pred.next.value = my
            while (my.locked.value) {
                env.park()
            }
        }
        return my
    }

    override fun unlock(node: Node) {
        if (node.next.value == null) {
            if (tail.compareAndSet(node, null)) {
                return
            }
            while (node.next.value == null) {}
        }
        val next = node.next.value!!
        next.locked.value = false
        env.unpark(next.thread)
    }

    class Node {
        val thread: Thread = Thread.currentThread()
        val locked = AtomicReference(true)
        val next = AtomicReference<Node?>(null)
    }
}
