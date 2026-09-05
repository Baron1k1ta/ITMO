//package info.kgeorgiy.ja.baronov.walk;
//
//import info.kgeorgiy.java.advanced.lambda.EasyLambda;
//import info.kgeorgiy.java.advanced.lambda.Trees;
//import info.kgeorgiy.java.advanced.walk.RecursiveWalkTest;
//
//import java.util.*;
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//import java.util.stream.Collector;
//import java.util.stream.Collectors;
//
//public class RecursiveWalk implements EasyLambda{
//
//    @Override
//    public <T> Spliterator<T> binaryTreeSpliterator(Trees.Binary<T> tree) {
//        return new BinaryTreeSpliterator<>(tree);
//    }
//
//    @Override
//    public <T> Spliterator<T> sizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree) {
//        return new SizedBinaryTreeSpliterator<>(tree);
//    }
//
//    @Override
//    public <T> Spliterator<T> naryTreeSpliterator(Trees.Nary<T> tree) {
//        return new NaryTreeSpliterator<>(tree);
//    }
//
//    @Override
//    public <T> Collector<T, ?, Optional<T>> first() {
//        return Collectors.reducing((a, b) -> a);
//    }
//
//    @Override
//    public <T> Collector<T, ?, Optional<T>> last() {
//        return Collectors.reducing((a, b) -> b);
//    }
//
//    @Override
//    public <T> Collector<T, ?, Optional<T>> middle() {
//        return Collector.of(
//                (Supplier<ArrayList<T>>) ArrayList::new,
//                ArrayList::add,
//                (a, b) -> { a.addAll(b); return a; },
//                list -> list.isEmpty() ? Optional.empty() : Optional.of(list.get(list.size() / 2))
//        );
//    }
//
//    @Override
//    public Collector<CharSequence, ?, String> commonPrefix() {
//        return Collector.of(
//                StringBuilder::new,
//                (acc, sequence) -> {
//                    int length = Math.min(acc.length(), sequence.length());
//                    int i = 0;
//                    while (i < length && acc.charAt(i) == sequence.charAt(i)) {
//                        i++;
//                    }
//                    acc.setLength(i);
//                },
//                (acc1, acc2) -> {
//                    int length = Math.min(acc1.length(), acc2.length());
//                    int i = 0;
//                    while (i < length && acc1.charAt(i) == acc2.charAt(i)) {
//                        i++;
//                    }
//                    acc1.setLength(i);
//                    return acc1;
//                },
//                StringBuilder::toString
//        );
//    }
//
//    @Override
//    public Collector<CharSequence, ?, String> commonSuffix() {
//        return null;
//    }
//
//    // Собственный spliterator для бинарного дерева (без известного размера)
//    private static class BinaryTreeSpliterator<T> implements Spliterator<T> {
//        private final Deque<Trees.Binary<T>> stack = new ArrayDeque<>();
//
//        public BinaryTreeSpliterator(Trees.Binary<T> tree) {
//            stack.push(tree);
//        }
//
//        @Override
//        public boolean tryAdvance(Consumer<? super T> action) {
//            while (!stack.isEmpty()) {
//                Trees.Binary<T> node = stack.pop();
//                if (node instanceof Trees.Leaf<T> leaf) {
//                    action.accept(leaf.value());
//                    return true;
//                } else if (node instanceof Trees.Binary.Branch<T> branch) {
//                    // Сначала помещаем правое поддерево, затем левое – левое будет обработано первым
//                    stack.push(branch.right());
//                    stack.push(branch.left());
//                }
//            }
//            return false;
//        }
//
//        @Override
//        public Spliterator<T> trySplit() {
//            // Если в стеке менее 2-х узлов – делить нечего
//            if (stack.size() <= 1) {
//                return null;
//            }
//            // Извлекаем элемент с конца стека – это поддерево, которое точно будет обработано позже
//            Trees.Binary<T> splitNode = stack.pollLast();
//            if (splitNode == null) {
//                return null;
//            }
//            return new BinaryTreeSpliterator<>(splitNode);
//        }
//
//        @Override
//        public long estimateSize() {
//            return Long.MAX_VALUE;
//        }
//
//        @Override
//        public int characteristics() {
//            return ORDERED;
//        }
//    }
//
//    // Собственный spliterator для бинарного дерева с известным размером
//    private static class SizedBinaryTreeSpliterator<T> implements Spliterator<T> {
//        private final Deque<Trees.SizedBinary<T>> stack = new ArrayDeque<>();
//        private long est;
//
//        public SizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree) {
//            stack.push(tree);
//            this.est = tree.size();
//        }
//
//        @Override
//        public boolean tryAdvance(Consumer<? super T> action) {
//            while (!stack.isEmpty()) {
//                Trees.SizedBinary<T> node = stack.pop();
//                if (node instanceof Trees.Leaf<T> leaf) {
//                    action.accept(leaf.value());
//                    est--;
//                    return true;
//                } else if (node instanceof Trees.SizedBinary.Branch<T> branch) {
//                    stack.push(branch.right());
//                    stack.push(branch.left());
//                }
//            }
//            return false;
//        }
//
//        @Override
//        public Spliterator<T> trySplit() {
//            if (stack.size() <= 1) {
//                return null;
//            }
//            Trees.SizedBinary<T> splitNode = stack.pollLast();
//            if (splitNode == null) {
//                return null;
//            }
//            long splitSize = splitNode.size();
//            est -= splitSize;
//            return new SizedBinaryTreeSpliterator<>(splitNode);
//        }
//
//        @Override
//        public long estimateSize() {
//            return est;
//        }
//
//        @Override
//        public int characteristics() {
//            return ORDERED | SIZED;
//        }
//    }
//
//    // Собственный spliterator для n-арного дерева
//    private static class NaryTreeSpliterator<T> implements Spliterator<T> {
//        private final Deque<Trees.Nary<T>> stack = new ArrayDeque<>();
//
//        public NaryTreeSpliterator(Trees.Nary<T> tree) {
//            stack.push(tree);
//        }
//
//        @Override
//        public boolean tryAdvance(Consumer<? super T> action) {
//            while (!stack.isEmpty()) {
//                Trees.Nary<T> node = stack.pop();
//                if (node instanceof Trees.Leaf<T> leaf) {
//                    action.accept(leaf.value());
//                    return true;
//                } else if (node instanceof Trees.Nary.Node<T> n) {
//                    List<Trees.Nary<T>> children = n.children();
//                    for (int i = children.size() - 1; i >= 0; i--) {
//                        stack.push(children.get(i));
//                    }
//                }
//            }
//            return false;
//        }
//
//        @Override
//        public Spliterator<T> trySplit() {
//            if (stack.size() <= 1) {
//                return null;
//            }
//            Trees.Nary<T> splitNode = stack.pollLast();
//            if (splitNode == null) {
//                return null;
//            }
//            return new NaryTreeSpliterator<>(splitNode);
//        }
//
//        @Override
//        public long estimateSize() {
//            return Long.MAX_VALUE;
//        }
//
//        @Override
//        public int characteristics() {
//            return ORDERED;
//        }
//    }
//}


















//    @Override
//    public <T> Spliterator<T> binaryTreeSpliterator(Trees.Binary<T> tree) {
//        return new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
//            private final Deque<Trees.Binary<T>> stack = new ArrayDeque<>();
//            {
//                stack.push(tree);
//            }
//
//            @Override
//            public boolean tryAdvance(Consumer<? super T> action) {
//                while (!stack.isEmpty()) {
//                    Trees.Binary<T> element = stack.pop();
//                    if (element instanceof Trees.Leaf<T> leaf) {
//                        action.accept(leaf.value());
//                        return true;
//                    } else if (element instanceof Trees.Binary.Branch<T> branch) {
//                        stack.push(branch.right());
//                        stack.push(branch.left());
//                    }
//                }
//                return false;
//            }
//        };
//    }


//    @Override
//    public <T> Spliterator<T> sizedBinaryTreeSpliterator(Trees.SizedBinary<T> tree) {
//        return new Spliterators.AbstractSpliterator<T>(tree.size(), Spliterator.SIZED) {
//            private final Deque<Trees.SizedBinary<T>> stack = new ArrayDeque<>();
//            {
//                stack.push(tree);
//            }
//            @Override
//            public boolean tryAdvance(Consumer<? super T> action) {
//                while (!stack.isEmpty()) {
//                    Trees.SizedBinary<T> element = stack.pop();
//                    if (element instanceof Trees.Leaf<T> leaf) {
//                        action.accept(leaf.value());
//                        return true;
//                    } else if (element instanceof Trees.SizedBinary.Branch<T> branch) {
//                        stack.push(branch.right());
//                        stack.push(branch.left());
//                    }
//                }
//                return false;
//            }
//        };
//    }

/**
 * Returns nary tree spliterator.
 * This method runs in O({@code n}) time, where {@code n} is the size of the root.
 *
 * @param tree
 */
//    @Override
//    public <T> Spliterator<T> naryTreeSpliterator(Trees.Nary<T> tree) {
//        return new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, 0) {
//            private final Deque<Trees.Nary<T>> stack = new ArrayDeque<>();
//            {
//                stack.push(tree);
//            }
//            @Override
//            public boolean tryAdvance(Consumer<? super T> action) {
//                while (!stack.isEmpty()) {
//                    Trees.Nary<T> element = stack.pop();
//                    if (element instanceof Trees.Leaf<T> leaf) {
//                        action.accept(leaf.value());
//                        return true;
//                    } else if (element instanceof Trees.Nary.Node<T> node) {
//                        List<Trees.Nary<T>> children = node.children();
//                        for (int i = children.size() - 1; i >= 0; i--) {
//                            stack.push(children.get(i));
//                        }
//                    }
//                }
//                return false;
//            }
//        };
//    }

//    @Override
//    public Collector<CharSequence, ?, String> commonSuffix() {
//        return Collector.of(
//                StringBuilder::new,
//                (acc, sequence) -> {
//                    int length = Math.min(acc.length(), sequence.length());
//                    int i = 0;
//                    while (i < length && acc.charAt(i) == sequence.charAt(i)) {
//                        i++;
//                    }
//
//                    acc.setLength(i);
//                },
//                (acc1, acc2) -> {
//
//                    int length = Math.min(acc1.length(), acc2.length());
//                    int i = 0;
//                    while (i < length && acc1.charAt(i) == acc2.charAt(i)) {
//                        i++;
//                    }
//                    acc1.setLength(i);
//                    return acc1;
//                },
//                StringBuilder::toString
//        );
//    }