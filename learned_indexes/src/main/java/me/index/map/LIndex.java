package me.index.map;

import me.index.Holder;
import me.index.algo.LRM;
import me.index.algo.Regression;
import me.index.algo.TConsumer;

import java.util.*;

public class LIndex implements Storage {

    long[][] keys_placeholder;
    Object[][] values_placeholder;
    long[] keys;
    LIndex[] children;

    LIndex rootIndex;

    double k;
    double b;
    
    long maxKey;
    int maxErr;
    
    int bound;
    int sz;

    static int maxBucketSize = 128;

    public LIndex() {
        this(new Long[]{1L}, null, null, null, null, 0);
    }


    public LIndex(Long[] keys, Object[] values, LIndex[] children, LIndex rootIndex, LRM lrm, int maxErr) {
        this.keys = Arrays.stream(keys).mapToLong(Long::longValue).toArray();
        this.children = children;
        this.rootIndex = rootIndex;
        if (children == null && values != null) {
            this.keys_placeholder = new long[keys.length+1][1];
            this.values_placeholder = new Object[keys.length+1][1];
            
            
            this.keys_placeholder[keys.length] = new long[0];
            this.values_placeholder[keys.length] = new Object[0];
            for (int i = 0; i < keys.length; ++i) {
                this.keys_placeholder[i][0] = keys[i];
                this.values_placeholder[i][0] = values[i];
            }
        }
        this.maxErr = maxErr;
        this.maxKey = keys[keys.length - 1];
        this.bound = values == null ? keys.length : keys.length + 1;
        if (lrm != null) {
            this.b = lrm.b();
            this.k = lrm.k();
        }
    }

    public void assignFrom(LIndex other) {
        this.keys = other.keys;
        this.keys_placeholder = other.keys_placeholder;
        this.values_placeholder = other.values_placeholder;
        this.rootIndex = other.rootIndex;
        this.children = other.children;
        this.k = other.k;
        this.b = other.b;
        this.maxErr = other.maxErr;
        this.bound = other.bound;
        this.maxKey = other.maxKey;
    }

    
    
    
    private int getPosition(long key) {
        int pos = (int)(key * k + b);
        
        
        
        
        
        
        int lb = Integer.max(0, pos - maxErr);
        int mx = bound - 1;
        int ub = Integer.min(mx, Integer.max(lb, pos + maxErr + 1));
        int realPos = ub;
        for (int i = lb; i < ub; ++i) {
            if (keys[i] >= key) {
                realPos = i;
                break;
            }
        }
        return realPos;
    }


    @Override
    public void init(List<Long> keys, List<Object> values, int maxErr) {
        this.maxErr = maxErr;
        reconstruct(true, keys, values);
    }

    @Override
    public int find(long key, Holder<Object> result) {
        if (children == null) {
            int realPos = getPosition(key);
            long[] bucket = keys_placeholder[realPos];
            for (int i = 0; i < bucket.length; ++i) {
                if (bucket[i] >= key) {
                    if (bucket[i] == key) {
                        result.v = values_placeholder[realPos][i];
                        return OK;
                    } else {
                        return FAIL;
                    }
                }
            }
            return FAIL;
        }
        return children[getPosition(key)].find(key, result);
    }

    @Override
    public int insert(long key, Object value) {
        if (children == null) {
            int realPos = getPosition(key);
            long[] bucket = keys_placeholder[realPos];
            for (int i = 0; i < bucket.length; ++i) {
                if (bucket[i] >= key) {
                    if (bucket[i] == key) {
                        return FAIL;
                    } else {
                        
                        bucket = Arrays.copyOf(bucket, bucket.length + 1);
                        values_placeholder[realPos] = Arrays.copyOf(values_placeholder[realPos], values_placeholder[realPos].length + 1);
                        System.arraycopy(bucket, i, bucket, i + 1, bucket.length - i - 1);
                        System.arraycopy(values_placeholder[realPos], i, values_placeholder[realPos], i + 1, values_placeholder[realPos].length - i - 1);
                        keys_placeholder[realPos] = bucket;
                        keys_placeholder[realPos][i] = key;
                        values_placeholder[realPos][i] = value;
                        if (bucket.length >= maxBucketSize) {
                            rootIndex.reconstruct();
                        }
                        rootIndex.sz++;
                        return OK;
                    }
                }
            }
            
            bucket = Arrays.copyOf(bucket, bucket.length + 1);
            values_placeholder[realPos] = Arrays.copyOf(values_placeholder[realPos], values_placeholder[realPos].length + 1);

            keys_placeholder[realPos] = bucket;
            keys_placeholder[realPos][bucket.length - 1] = key;
            values_placeholder[realPos][bucket.length - 1] = value;
            if (bucket.length >= maxBucketSize) {
                rootIndex.reconstruct();
            }
            rootIndex.sz++;
            return OK;
        }

        return children[getPosition(key)].insert(key, value);
    }

    public void reconstruct() {
        reconstruct(false, null, null);
    }

    public void reconstruct(boolean fromReady, List<Long> rKeys, List<Object> rValues) {
        List<Long> ks = new ArrayList<>();
        List<Object> vs = new ArrayList<>();
        if (fromReady) {
            ks = rKeys;
            vs = rValues;
        } else {
            resort(ks, vs);
        }
        sz = ks.size();
        
        
        
        List<LIndex> newModels = createCompactModels(ks, vs, null, maxErr);
        for (LIndex newModel : newModels) {
            newModel.rootIndex = this;
        }

        while (newModels.size() > 1) {
            List<Long> layerKs = new ArrayList<>();
            
            for (LIndex newModel : newModels) {
                layerKs.add(newModel.maxKey);
            }

            newModels = createCompactModels(layerKs, null, newModels, maxErr);
        }
        rootIndex = newModels.getFirst();
        rootIndex.propagateRoot(this);
        assignFrom(rootIndex);
        this.rootIndex = this;
    }

    private void propagateRoot(LIndex root) {
        this.rootIndex = root;
        if (children != null) {
            for (LIndex child : children) {
                child.propagateRoot(root);
            }
        }
    }



    @Override
    public int remove(long key) {
        if (children == null) {
            int realPos = getPosition(key);
            long[] bucket = keys_placeholder[realPos];
            for (int i = 0; i < bucket.length; ++i) {
                if (bucket[i] >= key) {
                    if (bucket[i] == key) {
                        System.arraycopy(bucket, i + 1, bucket, i, bucket.length - i - 1);
                        System.arraycopy(values_placeholder[realPos], i + 1, values_placeholder[realPos], i, values_placeholder[realPos].length - i - 1);
                        keys_placeholder[realPos] = Arrays.copyOf(bucket, bucket.length - 1);
                        values_placeholder[realPos] = Arrays.copyOf(values_placeholder[realPos], values_placeholder[realPos].length - 1);
                        rootIndex.sz--;
                        return OK;
                    } else {
                        return FAIL;
                    }
                }
            }
            return FAIL;
        }

        return children[getPosition(key)].remove(key);
    }
    
    
    
    @Override
    public void resort(List<Long> ks, List<Object> vs) {
        if (children == null) {
            for (int i = 0; i < keys_placeholder.length; ++i) {
                for (int j = 0; j < keys_placeholder[i].length; ++j) {
                    if (values_placeholder[i][j] != null) {
                        ks.add(keys_placeholder[i][j]);
                        vs.add(values_placeholder[i][j]);
                    }
                }
            }
            return;
        }

        
        for (LIndex child : children) {
            child.resort(ks, vs);
        }
    }

    @Override
    public int size() {
        return sz;
    }

    

    
    
    private static class LRMConsumer implements TConsumer<Integer, Integer, LRM> {
        List<Long> keys;
        List<Object> values;
        List<LIndex> children;
        int eps;

        List<LIndex> segments = new ArrayList<>();

        public LRMConsumer(List<Long> keys, List<Object> values, List<LIndex> children, int eps) {
            this.keys = keys;
            this.values = values;
            this.children = children;
            this.eps = eps;
        }

        @Override
        public void accept(Integer integer, Integer integer2, LRM lrm) {
            Object[] val = null;
            if (values != null) {
                val = values.subList(integer, integer2).toArray(new Object[0]);
            }
            LIndex[] child = null;
            if (children != null) {
                child = children.subList(integer, integer2).toArray(new LIndex[0]);
            }

            LIndex segment = new LIndex((keys.subList(integer, integer2)).toArray(new Long[0]),
                    val,
                    child,
                    null, lrm, eps);
            segments.add(segment);
        }

        public List<LIndex> getSegments() {
            return segments;
        }
    }
    

    List<LIndex> createCompactModels(List<Long> keys, List<Object> values, List<LIndex> children, int eps) {
        LRMConsumer consumer = new LRMConsumer(keys, values, children, eps);
        Regression regression = new Regression();
        regression.split(keys, eps - 1, consumer);
        return consumer.getSegments();
    }
}
