package info.kgeorgiy.ja.baronov.arrayset;
import java.util.*;

public class ArraySet<E> extends AbstractSet<E> implements SortedSet<E> {

    public final List<E> list;
    private final Comparator<? super E> comparator;
    // :NOTE: static
    private static final IllegalArgumentException COMPARE_EXCEPTION = new IllegalArgumentException("fromElement cannot be greater than toElement");

    private final Comparator<Object> DEFAULT_ORDER = (o1, o2) -> Collections.reverseOrder().reversed().compare(o1, o2);

    public ArraySet(Collection<? extends E> collection,Comparator<? super E> comparator) {
        TreeSet<E> set = new TreeSet<>(comparator);
        set.addAll(collection);
        list = List.copyOf(set); // :NOTE: unmodifiable
        this.comparator = comparator;
    }


    public ArraySet() {
        this(Collections.emptyList(), null);
    }


    public ArraySet(Collection<? extends E> collection) {
        this(collection, null);
    }

    private ArraySet(List<E> list, Comparator<? super E> comparator) {
        this.list = list;
        this.comparator = comparator;
    }

    private int findElement (E element){
        int index = Collections.binarySearch(list, element, comparator);

        if (index < 0) {
            index = -index - 1;
        }

        return index;
    }


    @Override
    public Comparator<? super E> comparator() {
        return comparator;
    }


//     :NOTE:

    // :NOTE: getOrDefaultComparator
    private Comparator<? super E> getOrDefaultComparator() {
        return comparator == null ? DEFAULT_ORDER : comparator;
    }

    // :NOTE use head and tail

    private SortedSet<E> subSetCreator(int from, int to) {
        if (list.isEmpty()) {
            return new ArraySet<>(Collections.emptyList(), comparator);
        }
        return new ArraySet<>(list.subList(from, to), comparator);
    }

    @Override
    public SortedSet<E> subSet(E fromElement, E toElement) {
        if (getOrDefaultComparator().compare(fromElement, toElement) > 0) {
            throw COMPARE_EXCEPTION;
        }
        return subSetCreator(findElement(fromElement), findElement(toElement));
    }

    @Override
    public SortedSet<E> headSet(E toElement) {
        return subSetCreator(0, findElement(toElement));
    }

    @Override
    public SortedSet<E> tailSet(E fromElement) {
        return subSetCreator(findElement(fromElement), size());
    }

    @Override
    public E first() {
        return list.getFirst();
    }

    @Override
    public E last() {
        return list.getLast();
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();

    }

    @Override
    public int size() {
        return list.size();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return Collections.binarySearch(list, (E) o, comparator) >= 0;
    }
}
