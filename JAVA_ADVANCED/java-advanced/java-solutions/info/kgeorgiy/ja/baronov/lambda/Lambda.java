package info.kgeorgiy.ja.baronov.lambda;

import info.kgeorgiy.java.advanced.lambda.EasyLambda;
import info.kgeorgiy.java.advanced.lambda.Trees;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Lambda implements EasyLambda {

    @Override
    public <T> Spliterator<T> binaryTreeSpliterator(Trees.Binary<T> tree) {
        return new Lambda.BinaryTreeSpliterator<>(tree);
    }

    @Override
    public <T> Spliterator<T> sizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree) {
        return new Lambda.SizedBinaryTreeSpliterator<>(tree);
    }

    @Override
    public <T> Spliterator<T> naryTreeSpliterator(Trees.Nary<T> tree) {
        return new Lambda.NaryTreeSpliterator<>(tree);
    }

    private static abstract class AbstractSpliterator<T, TREE> implements Spliterator<T> {
        protected final Deque<TREE> stack = new ArrayDeque<>();

        public AbstractSpliterator(TREE tree) {
            stack.push(tree);
        }

        protected long est;
        protected int characteristics;

        protected abstract boolean isLeaf(TREE node);
        protected abstract T getValue(TREE node);
        protected void updateSize() {}
        protected abstract Spliterator<T> createSpliterator(TREE tree);
        protected abstract void pushChildren(TREE node);


        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            while (!stack.isEmpty()) {
                TREE node = stack.pop();
                if (isLeaf(node)) {
                    action.accept(getValue(node));
                    updateSize();
                    return true;
                } else {
                    pushChildren(node);
                }
            }
            return false;
        }

        @Override
        public Spliterator<T> trySplit() {
            if (stack.isEmpty()) {
                return null;
            }else if (stack.size() < 2) {
                TREE node = stack.pop();
                if (!isLeaf(node)) {
                    pushChildren(node);
                } else {
                    stack.push(node);
                    return null;
                }
            }
            TREE splitNode = stack.pop();

            return createSpliterator(splitNode);
        }

        @Override
        public long estimateSize() {
            return est;
        }

        @Override
        public int characteristics() {
            return characteristics;
        }
    }



    private static class BinaryTreeSpliterator<T>
            extends AbstractSpliterator<T, Trees.Binary<T>> {

        public BinaryTreeSpliterator(Trees.Binary<T> tree) {
            super(tree);
            if(isLeaf(tree)){
                est = 1;
                characteristics = ORDERED | IMMUTABLE | SIZED | SUBSIZED;
            }else{
                est = Long.MAX_VALUE;
                characteristics = ORDERED | IMMUTABLE;
            }
        }

        @Override
        protected boolean isLeaf(Trees.Binary<T> node) {
            return node instanceof Trees.Leaf<T>;
        }

        @Override
        protected T getValue(Trees.Binary<T> node) {
            return ((Trees.Leaf<T>) node).value();
        }


        @Override
        protected void pushChildren(Trees.Binary<T> node){
            if(node instanceof Trees.Binary.Branch<T> branch){
                stack.push(branch.right());
                stack.push(branch.left());
            }
        }

        @Override
        protected Spliterator<T> createSpliterator(Trees.Binary<T> tree) {
            return new BinaryTreeSpliterator<>(tree);
        }

    }


    private static class SizedBinaryTreeSpliterator<T>
            extends AbstractSpliterator<T, Trees.SizedBinary<T>> {

        public SizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree) {
            super(tree);
            est = tree.size();
            characteristics = ORDERED | SIZED | IMMUTABLE | SUBSIZED;
        }

        @Override
        protected boolean isLeaf(Trees.SizedBinary<T> node) {
            return node instanceof Trees.Leaf<T>;
        }

        @Override
        protected T getValue(Trees.SizedBinary<T> node) {
            return ((Trees.Leaf<T>) node).value();
        }


        @Override
        protected void pushChildren(Trees.SizedBinary<T> node){
            if(node instanceof Trees.SizedBinary.Branch<T> branch){
                stack.push(branch.right());
                stack.push(branch.left());
            }
        }

        @Override
        protected Spliterator<T> createSpliterator(Trees.SizedBinary<T> tree) {
            return new SizedBinaryTreeSpliterator<>(tree);
        }

        @Override
        protected void updateSize() {
            est--;
        }

    }


    private static class NaryTreeSpliterator<T>
            extends AbstractSpliterator<T, Trees.Nary<T>> {


        public NaryTreeSpliterator(Trees.Nary<T> tree) {
            super(tree);
            if(isLeaf(tree)){
                est = 1;
                characteristics = ORDERED | IMMUTABLE | SIZED | SUBSIZED;
            }else{
                est = Long.MAX_VALUE;
                characteristics = ORDERED | IMMUTABLE;
            }
        }

        @Override
        protected boolean isLeaf(Trees.Nary<T> node) {
            return node instanceof Trees.Leaf<T>;
        }

        @Override
        protected T getValue(Trees.Nary<T> node) {
            return ((Trees.Leaf<T>) node).value();
        }


        @Override
        protected void pushChildren(Trees.Nary<T> node){
            if(node instanceof Trees.Nary.Node<T> branch){
                List<Trees.Nary<T>> children = branch.children();
                for (int i = children.size() - 1; i >= 0; i--) {
                    stack.push(children.get(i));
                }
            }
        }

        @Override
        protected Spliterator<T> createSpliterator(Trees.Nary<T> tree) {
            return new NaryTreeSpliterator<>(tree);
        }

    }


    @Override
    public <T> Collector<T, ?, Optional<T>> first() {
        return Collectors.reducing((a, b) -> a);
    }


    @Override
    public <T> Collector<T, ?, Optional<T>> last() {
        return Collectors.reducing((a, b) -> b);
    }


    @Override
    public <T> Collector<T, ?, Optional<T>> middle() {
        return Collector.of(
                (Supplier<ArrayList<T>>) ArrayList::new,
                ArrayList::add,
                (a, b) -> { a.addAll(b); return a; },
                list -> list.isEmpty() ? Optional.empty() : Optional.of(list.get(list.size() / 2))
        );
    }

    private static class Pair<F, S> {
        F first;
        S second;
        Pair(F first, S second) {
            this.first = first;
            this.second = second;
        }
    }


    private void updateCommon(StringBuilder sb, CharSequence sequence, boolean flag) {
        int minLength = Math.min(sb.length(), sequence.length());
        int i = 0;
        if (flag) {
            while (i < minLength && sb.charAt(i) == sequence.charAt(i)) {
                i++;
            }
            sb.setLength(i);
        } else {
            while (i < minLength && sb.charAt(sb.length() - 1 - i) == sequence.charAt(sequence.length() - 1 - i)) {
                i++;
            }
            String common = i == 0 ? "" : sb.substring(sb.length() - i);
            sb.setLength(0);
            sb.append(common);
        }
    }


    private Collector<CharSequence, ?, String> commonCollector(boolean flag) {
        return Collector.of(
                () -> new Pair<>(false, new StringBuilder()),
                (acc, seq) -> {
                    if (!acc.first) {
                        acc.second.append(seq);
                        acc.first = true;
                    } else {
                        updateCommon(acc.second, seq, flag);
                    }
                },
                (acc1, acc2) -> {
                    if (!acc1.first) return acc2;
                    if (!acc2.first) return acc1;
                    updateCommon(acc1.second, acc2.second, flag);
                    return acc1;
                },
                acc -> acc.first ? acc.second.toString() : ""
        );
    }

    @Override
    public Collector<CharSequence, ?, String> commonPrefix() {
        return commonCollector(true);
    }

    @Override
    public Collector<CharSequence, ?, String> commonSuffix() {
        return commonCollector(false);
    }

}
