package me.index.map;

import me.index.Holder;
import java.util.Arrays;
import java.util.List;


public class BTree implements Storage {
    private Node root;
    private static final int default_t = 32;
    private int treeSize = 0;


    public BTree() {
        this.root = new Node(default_t, this);
    }

    @Override
    public void init(List<Long> keys, List<Object> values, int maxErr) {
        root = new Node(default_t, this);
        for (int i = 0; i < keys.size(); i++)
            this.insert(keys.get(i), values.get(i));
    }

    @Override
    public int find(long key, Holder<Object> result) {
        return root.find(key, result);
    }

    @Override
    public int insert(long key, Object value) {
        root.assureInsert();
        return root.insert(key, value);
    }

    @Override
    public int remove(long key) {
        if (root.size == 1 && root.children[0] != null && root.children[0].size == default_t - 1 && root.children[1].size == default_t - 1) {
            root.children[0].assureRemove();
            root = root.children[0];
            root.parent = null;
        }
        return root.remove(key);
    }

    @Override
    public void resort(List<Long> keys, List<Object> vals) {
        root.resort(keys, vals);
    }

    @Override
    public int size() {
        return treeSize;
    }

    private static class Node {
        private final int t;
        private final BTree tree;
        private Node parent;
        private int size;

        private final long[] keys;
        private final Object[] values;
        private final Node[] children;

        public Node(int t, BTree tree){
            this.t = t;
            this.tree = tree;
            parent = null;
            size = 0;

            keys = new long[2 * t];
            values = new Object[2 * t];
            children = new Node[2 * t];
        }

        public int find(long key, Holder<Object> result) {
            int position = lowerBound(keys, size, key);
            if (position < size && keys[position] == key) {
                result.v = values[position];
                return OK;
            }
            return (children[position] == null) ? FAIL : children[position].find(key, result);
        }

        public int insert(long key, Object value) {

            if (children[0] == null) {
                int position = lowerBound(keys, size, key);
                if (position < size && keys[position] == key) {
                    return FAIL;
                }
                insertKey(keys, size, position, key);
                insertValue(values, size, position, value);
                size++;
                tree.treeSize++;
                return OK;
            }

            int position = lowerBound(keys, size, key);
            
            if (children[position].assureInsert() && keys[position] < key) {
                ++position;
            }
            if (position < size && keys[position] == key) {
                return FAIL;
            }

            return children[position].insert(key, value);
        }

        private boolean assureInsert() {
            if (size == 2 * t - 1) {
                split();
                return true;
            }
            return false;
        }

        private void split() {
            this.size = t - 1;

            Node brother = new Node(t, tree);
            brother.size = t - 1;

            System.arraycopy(keys, t, brother.keys, 0, t - 1);
            System.arraycopy(values, t, brother.values, 0, t - 1);
            if (children[0] != null) {
                System.arraycopy(children, t, brother.children, 0, t);
                for (int i = t; i < 2 * t; ++i) {
                    children[i].parent = brother;
                }
            }
            Arrays.fill(keys, t, 2 * t, Long.MAX_VALUE);

            long pivot_key = keys[t - 1];
            Object pivot_value = values[t - 1];

            if (parent == null) {

                parent = new Node(t, tree);
                parent.keys[0] = pivot_key;
                parent.values[0] = pivot_value;
                parent.children[0] = this;
                parent.children[1] = brother;
                parent.size = 1;
                tree.root = parent;

            } else {
                int pivot_position = lowerBound(parent.keys, parent.size, pivot_key);
                insertKey(parent.keys, parent.size, pivot_position, pivot_key);
                insertValue(parent.values, parent.size, pivot_position, pivot_value);
                insertValue(parent.children, parent.size + 1, pivot_position + 1, brother);
                parent.size++;
                parent.children[pivot_position] = this;
            }
            brother.parent = parent;
        }

        public int remove(long key) {

            if (children[0] == null) {
                int position = lowerBound(keys, size, key);
                if (position < size && keys[position] == key) {
                    removeKey(keys, size, position);
                    removeValue(values, size, position);
                    size--;
                    tree.treeSize--;
                    return OK;
                }
                return FAIL;
            }

            int position = lowerBound(keys, size, key);
            children[position].assureRemove();
            position = lowerBound(keys, size, key);
            
            if (position < size && keys[position] == key) {
                Node targetNode = this.children[position];
                targetNode.assureRemove();
                while (targetNode.children[0] != null) {
                    Node newNode = targetNode.children[targetNode.size];
                    newNode.assureRemove();
                    newNode = targetNode.children[targetNode.size];
                    targetNode = newNode;
                }
                targetNode.size--;
                keys[position] = targetNode.keys[targetNode.size];
                values[position] = targetNode.values[targetNode.size];
                targetNode.keys[targetNode.size] = Long.MAX_VALUE;
                return OK;
            }
            return children[position].remove(key);
        }

        protected void assureRemove() {
            if (size == t - 1) {
                int my_index = lowerBound(parent.keys, parent.size, keys[0]);
                Node brother;
                boolean isBrotherRight;
                if (my_index == parent.size) {
                    brother = parent.children[my_index - 1];
                    isBrotherRight = false;
                } else {
                    brother = parent.children[my_index + 1];
                    isBrotherRight = true;
                }
                if (brother.size == t - 1) {
                    if (isBrotherRight) {
                        merge(brother, my_index);
                    } else {
                        brother.merge(this, my_index - 1);
                    }
                } else if (brother.size > t - 1) {
                    
                    
                    
                    
                    
                    
                    
                    if (isBrotherRight) {
                        insertKey(keys, size, size, parent.keys[my_index]);
                        insertValue(values, size, size, parent.values[my_index]);
                        parent.keys[my_index] = removeKey(brother.keys, brother.size, 0);
                        parent.values[my_index] = removeValue(brother.values, brother.size, 0);
                        insertValue(children, size + 1, size + 1, removeValue(brother.children, brother.size + 1, 0));
                        if (children[size + 1] != null) {
                            children[size + 1].parent = this;
                        }
                    } else {
                        insertKey(keys, size, 0, parent.keys[my_index - 1]);
                        insertValue(values, size, 0, parent.values[my_index - 1]);
                        parent.keys[my_index - 1] = removeKey(brother.keys, brother.size, brother.size - 1);
                        parent.values[my_index - 1] = removeValue(brother.values, brother.size, brother.size - 1);
                        insertValue(children, size + 1, 0, removeValue(brother.children, brother.size + 1, brother.size));
                        if (children[0] != null) {
                            children[0].parent = this;
                        }
                    }
                    brother.size--;
                    size++;
                }
            }
        }

        
        private void merge(Node brother, int my_idx) {

            long del_key;
            Object del_value;
            
            System.arraycopy(brother.keys, 0, keys, t, t - 1);
            System.arraycopy(brother.values, 0, values, t, t - 1);
            System.arraycopy(brother.children, 0, children, t, t);

            if (children[0] != null) {
                for (int i = t; i < 2 * t; ++i) {
                    children[i].parent = this;
                }
            }
            
            removeValue(parent.children, parent.size + 1, my_idx + 1);
            del_key = removeKey(parent.keys, parent.size, my_idx);
            del_value = removeValue(parent.values, parent.size, my_idx);

            size = 2 * t - 1;
            keys[t - 1] = del_key;
            values[t - 1] = del_value;
            parent.size--;
        }

        public void resort(List<Long> allKeys, List<Object> allValues) {
            for (int i = 0; i < size; ++i) {
                if (children[i] != null) {
                    children[i].resort(allKeys, allValues);
                }
                allKeys.add(keys[i]);
                allValues.add(values[i]);

            }
            if (children[size] != null) {
                children[size].resort(allKeys, allValues);
            }
        }
    }



    private static int lowerBound(long[] arr, int size, long key) {
        int left = 0;
        int right = size;

        while (left < right) {
            int mid = left + (right - left) / 2;
            if (arr[mid] < key) {
                left = mid + 1;
            } else {
                right = mid;
            }
        }
        return left;
    }



    public static void insertKey(long[] arr, int size, int pos, long value) {
        System.arraycopy(arr, pos, arr, pos+1, size - pos);
        arr[pos] = value;
    }

    public static <T> void insertValue(T[] arr, int size, int pos, T value) {
        System.arraycopy(arr, pos, arr, pos+1, size - pos);
        arr[pos] = value;
    }

    public static void insertValue(int[] arr, int size, int pos, int value) {
        System.arraycopy(arr, pos, arr, pos+1, size - pos);
        arr[pos] = value;
    }

    public static long removeKey(long[] arr, int size, int pos) {
        long result = arr[pos];
        System.arraycopy(arr, pos+1, arr, pos, size - pos - 1);
        return result;
    }

    public static <T> T removeValue(T[] arr, int size, int pos) {
        T result = arr[pos];
        System.arraycopy(arr, pos+1, arr, pos, size - pos - 1);
        return result;
    }

    public static int removeValue(int[] arr, int size, int pos) {
        int result = arr[pos];
        System.arraycopy(arr, pos+1, arr, pos, size - pos - 1);
        return result;
    }
}
