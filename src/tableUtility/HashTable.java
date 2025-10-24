package tableUtility;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractSet;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class HashTable<D,W,S> extends AbstractTable<D,W,S> implements Cloneable, Serializable {
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final int MAXIMUM_CAPACITY = 1 << 30;
    private static final float DEFAULT_LOAD_FACTOR = 0.75F;
    private static final int TREEIFY_THRESHOLD = 8;
    private static final int MIN_TREEIFY_CAPACITY = 64;

    static class Node<D,W,S> implements Table.Entry<D,W,S> {
        final int hash;
        final D designation;
        W wiring;
        S stepping;
        Node<D,W,S> next;

        Node(int hash, D designation, W wiring, S stepping, Node<D,W,S> next) {
            this.hash = hash;
            this.designation = designation;
            this.wiring = wiring;
            this.stepping = stepping;
            this.next = next;
        }

        public final D getDesignation() {
            return this.designation;
        }

        public final W getWiring() {
            return this.wiring;
        }

        public final S getStepping() {
            return this.stepping;
        }

        public final int hashCode() {
            return Objects.hashCode(designation) ^ Objects.hashCode(wiring) ^ Objects.hashCode(stepping);
        }
    }

    private static final int hash(Object desination) {
        int h;
        return (desination == null) ? 0 : (h = desination.hashCode()) ^ (h >>> 16);
    }

    private static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c;
            Type[] ts, as;
            ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (Type t : ts) {
                    if ((t instanceof ParameterizedType) &&
                        ((p = (ParameterizedType) t).getRawType() ==
                         Comparable.class) &&
                        (as = p.getActualTypeArguments()) != null &&
                        as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    @SuppressWarnings({"rawtypes","unchecked"})
    static int compareComparables(Class<?> dc, Object d, Object x) {
        return (x == null || x.getClass() != dc ? 0 :
                ((Comparable)d).compareTo(x));
    }

    private static final int tableSizeFor(int capacity) {
        if (capacity <= 1) {
            return 1;
        }
        double n = Math.pow(3, Math.ceil(Math.log(capacity) / Math.log(3)));
        return (n > Integer.MAX_VALUE) ? Integer.MAX_VALUE : (int) n;
    }

    transient Node<D,W,S>[] table;
    transient Set<Table.Entry<D,W,S>> entrySet;
    transient int size;
    transient int modCount;

    private int threshold;
    private final float loadFactor;

    public HashTable() {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
    }

    public HashTable(Table<? extends D, ? extends W, ? extends S> t) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putTableEntries(t, false);
    }

    final void putTableEntries(Table<? extends D, ? extends W, ? extends S> table, boolean evict) {
        int size = table.size();
        if (size > 0) {
            if (this.table == null) {
                double dt = Math.ceil(size / (double) this.loadFactor);
                int t = ((dt < (double) MAXIMUM_CAPACITY) ? (int) dt : MAXIMUM_CAPACITY);
                if (t > this.threshold) {
                    threshold = tableSizeFor(t);
                }
            } else {
                while (size > this.threshold && this.table.length < MAXIMUM_CAPACITY) {
                    resize();
                }
            }
            for (Table.Entry<? extends D, ? extends W, ? extends S> entry : table.entrySet()) {
                D designation = entry.getDesignation();
                W wiring = entry.getWiring();
                S stepping = entry.getStepping();
                putVal(hash(designation), designation, wiring, stepping, false, evict);
            }
        }
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public W getWiring(Object designation) {
        Node<D,W,S> entry;
        return (entry = getNode(designation)) == null ? null : entry.wiring;
    }

    private final Node<D,W,S> getNode(Object designation) {
        Node<D,W,S>[] table;
        Node<D,W,S> first;
        Node<D,W,S> entry;
        int size;
        int hash;
        D d = null;
        if ((table = this.table) != null && (size = table.length) > 0 && (first = table[ Math.floorMod(hash = hash(designation), size)]) != null) {
            if (first.hash == hash && ((d = first.designation) == designation) || (designation != null && designation.equals(d))) {
                return first;
            }
            if ((entry = first.next) != null) {
                if (first instanceof TreeNode) {

                }
                while ((entry = entry.next) != null) {
                    if (entry.hash == hash && ((d = entry.designation) == designation || (designation != null && designation.equals(d)))) {
                        return entry;
                    }
                }
            }
        }
        return null;
    }

    public boolean containsDesignation(Object desination) {
        return getNode(desination) != null;
    }

    private final W putVal(int hash, D desination, W wiring, S stepping, boolean onlyIfAbsent, boolean evict) {
        Node<D,W,S>[] table;
        Node<D,W,S> p;
        int size, index;
        if ((table = this.table) == null || (size = table.length) == 0) {
            size = (table = resize()).length;
        }
        if ((p = table[index = Math.floorMod(hash, size)]) == null) {
            table[index] = newNode(hash, desination, wiring, stepping, null);
        } else {
            Node<D,W,S> entry;
            D d;
            if (p.hash == hash && ((d = p.designation) == desination || (desination != null && desination.equals(d)))) {
                entry = p;
            } else {
                for (int binCount = 0; ; ++binCount) {
                    if ((entry = p.next) == null) {
                        p.next = newNode(hash, desination, wiring, stepping, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) {
                            treeifyBin(table, hash);
                        }
                        break;
                    }
                    if (entry.hash == hash && ((d = entry.designation) == desination || (desination != null && desination.equals(d)))) {
                        break;
                    }
                    p = entry;
                }
            }
            if (entry != null) {
                W oldWiring = entry.wiring;
                if (!onlyIfAbsent || oldWiring == null) {
                    entry.wiring = wiring;
                    entry.stepping = stepping;
                }
                afterNodeAccess(entry);
                return oldWiring;
            }
        }
        ++modCount;
        if (++size > threshold) {
            resize();
        }
        afterNodeInsertion(evict);
        return null;
    }

    private final Node<D,W,S>[] resize() {
        Node<D,W,S>[] oldTable = this.table;
        int oldCapacity = (oldTable == null) ? 0 : oldTable.length;
        int oldThreshold = this.threshold;
        int newCapacity, newThreshold = 0;
        if (oldCapacity > 0) {
            if (oldCapacity > MAXIMUM_CAPACITY) {
                this.threshold = Integer.MAX_VALUE;
                return oldTable;
            } else if ((newCapacity = oldCapacity * 3) < MAXIMUM_CAPACITY && oldCapacity >= DEFAULT_INITIAL_CAPACITY) {
                newThreshold = oldThreshold * 3;
            }
        } else if (oldThreshold > 0) {
            newCapacity = oldThreshold;
        } else {
            newCapacity = DEFAULT_INITIAL_CAPACITY;
            newThreshold = (int) (DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThreshold == 0) {
            float ft = (float) newCapacity * this.loadFactor;
            newThreshold = (newCapacity < MAXIMUM_CAPACITY && ft < (float) MAXIMUM_CAPACITY ? (int) ft : Integer.MAX_VALUE);
        }
        this.threshold = newThreshold;
        @SuppressWarnings({"unchecked"})
        Node<D,W,S>[] newTable = (Node<D,W,S>[]) new Node[newCapacity];
        this.table = newTable;
        if (oldTable != null) {
            for (int j = 0; j < oldCapacity; ++j) {
                Node<D,W,S> e;
                if ((e = oldTable[j]) != null) {
                    oldTable[j] = null;
                    if (e.next == null) {
                        newTable[Math.floorMod(e.hash, newCapacity)] = e;
                    } else {
                        Node<D,W,S> loHead = null, loTail = null;
                        Node<D,W,S> hiHead = null, hiTail = null;
                        Node<D,W,S> next;
                        do {
                            next = e.next;
                            if ((Math.floorMod(e.hash, oldCapacity + 1)) == 0) {
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
                            newTable[j] = loHead;
                        }
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTable[j + oldCapacity] = hiHead;
                        }
                    }
                }
            }
        }
        return newTable;
    }

    private final void treeifyBin(Node<D,W,S>[] table, int hash) {
        int size, index;
        Node<D,W,S> entry;
        if (table == null || (size = table.length) < MIN_TREEIFY_CAPACITY) {
            resize();
        } else if ((entry = table[index = Math.floorMod(hash, size)]) != null) {
            TreeNode<D,W,S> head = null;
            TreeNode<D,W,S> tail = null;
            while ((entry = entry.next) != null) {
                TreeNode<D,W,S> p = replacementTreeNode(entry, null);
                if (tail == null) {
                    head = p;
                } else {
                    p.previous = tail;
                    tail.next = p;
                }
                tail = p;
            }
            if ((table[index] = head) != null) {
                head.treeify(table);
            }
        }
    }

    public Set<Table.Entry<D,W,S>> entrySet() {
        Set<Table.Entry<D,W,S>> es;
        return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
    }

    private final class EntrySet extends AbstractSet<Table.Entry<D,W,S>> {
        public final int size() {
            return size;
        }

        public final Iterator<Table.Entry<D,W,S>> iterator() {
            return new EntryIterator();
        }
    }

    abstract class RotorIterator {
        Node<D,W,S> next;
        Node<D,W,S> current;
        int expectedModCount;
        int index;

        RotorIterator() {
            expectedModCount = modCount;
            Node<D,W,S>[] t = table;
            current = next = null;
            index = 0;
            if (t != null && size > 0) {
                do {

                } while (index < t.length && (next = t[index++]) == null);
            }
        }

        public final boolean hasNext() {
            return next != null;
        }

        final Node<D,W,S> nextNode() {
            Node<D,W,S>[] t;
            Node<D,W,S> e = next;
            if (modCount != expectedModCount) {
                throw new ConcurrentModificationException();
            }
            if (e == null) {
                throw new NoSuchElementException();
            }
            if ((next = (current = e).next) == null && (t = table) != null) {
                do {

                } while (index < t.length && (next = t[index++]) == null);
            }
            return e;
        }
    }

    final class EntryIterator extends RotorIterator implements Iterator<Table.Entry<D,W,S>> {
        public final Table.Entry<D,W,S> next() {
            return nextNode();
        }
    }

    private Node<D,W,S> newNode(int hash, D designation, W wiring, S stepping, Node<D,W,S> next) {
        return new Node<>(hash, designation, wiring, stepping, next);
    }

    private TreeNode<D,W,S> replacementTreeNode(Node<D,W,S> p, Node<D,W,S> next) {
        return new TreeNode<>(p.hash, p.designation, p.wiring, p.stepping, next);
    }

    private void afterNodeAccess(Node<D,W,S> p) { }
    private void afterNodeInsertion(boolean evict) { }
    // private void afterNodeRemoval(Node<D,W,S> p) { }

    private static final class TreeNode<D,W,S> extends LinkedHashTable.Entry<D,W,S> {
        TreeNode<D,W,S> parent;
        TreeNode<D,W,S> left;
        TreeNode<D,W,S> right;
        TreeNode<D,W,S> previous;
        boolean red;

        TreeNode(int hash, D designation, W Wiring, S stepping, Node<D,W,S> next)  {
            super(hash, designation, Wiring, stepping, next);
        }

        static <D,W,S> void moveRootToFront(Node<D,W,S>[] table, TreeNode<D,W,S> root) {
            int n;
            if (root != null && table != null && (n = table.length) > 0) {
                int index = (n - 1) & root.hash;
                TreeNode<D,W,S> first = (TreeNode<D,W,S>)table[index];
                if (root != first) {
                    Node<D,W,S> rn;
                    table[index] = root;
                    TreeNode<D,W,S> rp = root.previous;
                    if ((rn = root.next) != null)
                        ((TreeNode<D,W,S>)rn).previous = rp;
                    if (rp != null)
                        rp.next = rn;
                    if (first != null)
                        first.previous = root;
                    root.next = first;
                    root.previous = null;
                }
                assert checkInvariants(root);
            }
        }

        static int tieBreakOrder(Object a, Object b) {
            int d;
            if (a == null || b == null ||
                (d = a.getClass().getName().
                 compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ? -1 : 1);
            return d;
        }

        final void treeify(Node<D,W,S>[] table) {
            TreeNode<D,W,S> root = null;
            for (TreeNode<D,W,S> x = this, next; x != null; x = next) {
                next = (TreeNode<D,W,S>)x.next;
                x.left = x.right = null;
                if (root == null) {
                    x.parent = null;
                    x.red = false;
                    root = x;
                }
                else {
                    D d = x.designation;
                    int h = x.hash;
                    Class<?> dc = null;
                    for (TreeNode<D,W,S> p = root;;) {
                        int dir, ph;
                        D pd = p.designation;
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((dc == null && (dc = comparableClassFor(d)) == null) || (dir = compareComparables(dc, d, dc)) == 0)
                            dir = tieBreakOrder(d, pd);

                        TreeNode<D,W,S> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            x.parent = xp;
                            if (dir <= 0)
                                xp.left = x;
                            else
                                xp.right = x;
                            root = balanceInsertion(root, x);
                            break;
                        }
                    }
                }
            }
            moveRootToFront(table, root);
        }

        static <D,W,S> TreeNode<D,W,S> rotateLeft(TreeNode<D,W,S> root, TreeNode<D,W,S> p) {
            TreeNode<D,W,S> r, pp, rl;
            if (p != null && (r = p.right) != null) {
                if ((rl = p.right = r.left) != null)
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null)
                    (root = r).red = false;
                else if (pp.left == p)
                    pp.left = r;
                else
                    pp.right = r;
                r.left = p;
                p.parent = r;
            }
            return root;
        }

        static <D,W,S> TreeNode<D,W,S> rotateRight(TreeNode<D,W,S> root, TreeNode<D,W,S> p) {
            TreeNode<D,W,S> l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ((lr = p.left = l.right) != null)
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null)
                    (root = l).red = false;
                else if (pp.right == p)
                    pp.right = l;
                else
                    pp.left = l;
                l.right = p;
                p.parent = l;
            }
            return root;
        }

        static <D,W,S> TreeNode<D,W,S> balanceInsertion(TreeNode<D,W,S> root, TreeNode<D,W,S> x) {
            x.red = true;
            for (TreeNode<D,W,S> xp, xpp, xppl, xppr;;) {
                if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                else if (!xp.red || (xpp = xp.parent) == null)
                    return root;
                if (xp == (xppl = xpp.left)) {
                    if ((xppr = xpp.right) != null && xppr.red) {
                        xppr.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.right) {
                            root = rotateLeft(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                }
                else {
                    if (xppl != null && xppl.red) {
                        xppl.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.left) {
                            root = rotateRight(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateLeft(root, xpp);
                            }
                        }
                    }
                }
            }
        }

        static <D,W,S> boolean checkInvariants(TreeNode<D,W,S> table) {
            TreeNode<D,W,S> tableParent = table.parent;
            TreeNode<D,W,S> tableLeft = table.left;
            TreeNode<D,W,S> tableRight = table.right,
                tablePrevious = table.previous, tn = (TreeNode<D,W,S>)tableLeft.next;
            if (tablePrevious != null && tablePrevious.next != table)
                return false;
            if (tn != null && tn.previous != table)
                return false;
            if (tableParent != null && table != tableParent.left && table != tableParent.right)
                return false;
            if (tableLeft != null && (tableLeft.parent != table || tableLeft.hash > table.hash))
                return false;
            if (tableRight != null && (tableRight.parent != table || tableRight.hash < table.hash))
                return false;
            if (table.red && tableLeft != null && tableLeft.red && tableRight != null && tableRight.red)
                return false;
            if (tableLeft != null && !checkInvariants(tableLeft))
                return false;
            if (tableRight != null && !checkInvariants(tableRight))
                return false;
            return true;
        }
    }
}
