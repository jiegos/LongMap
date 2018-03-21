package ua.dp.eremeev;

import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V> {

    //table
    private Node<V>[] table;
    private long size;
    private static final int DEFAULT_INITIAL_CAPACITY = 1<<4;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private static final int MAXIMUM_CAPACITY = 1<<30;
    final float loadFactor;
    int threshold;

    //some text
    public LongMapImpl(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                    initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                    loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    public LongMapImpl(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }



    private static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    static final int hash(long key) {
        return (key == 0) ? 0 : (int) (key ^ (key >>> 32));
    }

    static class Node<V>{

        final int hash;
        final long key;
        V value;
        Node<V> next;

        Node(int hash, long key, V value, Node<V> next){
            this.hash = hash;
            this.key=key;
            this.value=value;
            this.next = next;
        }

        public final String toString() { return key + "=" + value; }

        public final int hashCode() {
            return hash(key) ^ Objects.hashCode(value);
        }

        public final long getKey() {
            return key;
        }


        public final V getValue() {
            return value;
        }

        public final V setValue(V v) {
            V result = value;
            value = v;
            return result;
        }

        public final boolean equals(Object v) {
            if (v == this)
                return true;
            if (v instanceof Node) {
                Node<?> e = (Node<?>) v;
                return (key == e.key) &&
                        Objects.equals(value, e.getValue());
            }
            return false;
        }
    }

    public V put(long key, V value) {
        return putVal(hash(key), key, value, false, true);
    }

    final V putVal(int hash, long key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<V>[] tab; Node<V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<V> e; long k;
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != 0 && key==k)))
                e = p;
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                    }
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != 0 && key==k)))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                return oldValue;
            }
        }
        if (++size > threshold)
            resize();
        return null;
    }


    Node<V> newNode(int hash, long key, V value, Node<V> next) {
        return new Node<>(hash, key, value, next);
    }

    final Node<V>[] resize() {
        Node<V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        int oldThr = threshold;
        int newCap, newThr = 0;
        if (oldCap > 0) {
            if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        Node<V>[] newTab = (Node<V>[])new Node[newCap];
        table = newTab;
        if (oldTab != null) {
            for (int j = 0; j < oldCap; ++j) {
                Node<V> e;
                if ((e = oldTab[j]) != null) {
                    oldTab[j] = null;
                    if (e.next == null)
                        newTab[e.hash & (newCap - 1)] = e;
                    else { // preserve order
                        Node<V> loHead = null, loTail = null;
                        Node<V> hiHead = null, hiTail = null;
                        Node<V> next;
                        do {
                            next = e.next;
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    public V get(long key) {
        Node<V> e;
        return (e = getNode(hash(key), key)) == null ? null : e.value;
    }

    final Node<V> getNode(int hash, long key) {
        Node<V>[] tab; Node<V> first, e; int n; long k;
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && // always check first node
                    ((k = first.key) == key || (key != 0 && key == k)))
                return first;
            if ((e = first.next) != null) {
                do {
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != 0 && key == k)))
                        return e;
                } while ((e = e.next) != null);
            }
        }
        return null;
    }

    public V remove(long key) {
        Node<V> e;
        return (e = removeNode(hash(key), key, null, false, true)) == null ?
                null : e.value;
    }

    final Node<V> removeNode(int hash, long key, Object value,
                               boolean matchValue, boolean movable) {
        Node<V>[] tab; Node<V> p; int n, index;
        if ((tab = table) != null && (n = tab.length) > 0 &&
                (p = tab[index = (n - 1) & hash]) != null) {
            Node<V> node = null, e; long k; V v;
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != 0 && key == k)))
                node = p;
            else if ((e = p.next) != null) {
                    do {
                        if (e.hash == hash &&
                                ((k = e.key) == key ||
                                        (key != 0 && key == k))) {
                            node = e;
                            break;
                        }
                        p = e;
                    } while ((e = e.next) != null);
            }
            if (node != null && (!matchValue || (v = node.value) == value ||
                    (value != null && value.equals(v)))) {
                if (node == p)
                    tab[index] = node.next;
                else
                    p.next = node.next;
                --size;
                return node;
            }
        }
        return null;
    }

    public boolean isEmpty() {
        return size==0;
    }

    public boolean containsKey(long key) {
        return getNode(hash(key), key) != null;
    }

    public boolean containsValue(V value) {
        Node<V>[] tab; V v;
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<V> e = tab[i]; e != null; e = e.next) {
                    if ((v = e.value) == value ||
                            (value != null && value.equals(v)))
                        return true;
                }
            }
        }
        return false;
    }

    public long[] keys() {
        Node<V>[] tab;

        if((tab = table) !=null && tab.length > 0){
            long[] keys = new long[(int)size];

            for (int i = 0, k = 0; i < table.length; i++) {
                for (Node<V> e = tab[i]; e != null; e = e.next) {
                    keys[k++] = e.key;
                }
            }
            return keys;
        }
        return null;
    }

    public V[] values() {
        Node<V>[] tab;
        if ((tab = table) != null && size > 0) {
            V[] v = (V[])new Object[(int)size];
            for (int i = 0, k = 0; i < table.length; i++) {
                for (Node<V> e = tab[i]; e != null; e = e.next) {
                    v[k++] = e.value;
                }
            }
            return v;
        }
        return null;
    }

    public long size() {
        return size;
    }

    public void clear() {
        Node<V>[] tab;
        if ((tab = table) != null && size > 0) {
            size = 0;
            for (int i = 0; i < tab.length; ++i)
                tab[i] = null;
        }
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append('{');

        Node<V>[] tab;
        if ((tab = table) != null && size > 0) {
            for (int i = 0; i < tab.length; ++i) {
                for (Node<V> e = tab[i]; e != null; e = e.next) {

                    sb.append(e.key);
                    sb.append('=');
                    sb.append(e.value);
                    sb.append(',').append(' ');

                }
            }
            sb.delete(sb.lastIndexOf(","), sb.lastIndexOf(" ")+1);
            return sb.append('}').toString();
        }
        return "{}";
    }
}

