package tableUtility;

class LinkedHashTable<D,W,S> extends HashTable<D,W,S>{
    static class Entry<D,W,S> extends HashTable.Node<D,W,S> {
        Entry<D,W,S> before;
        Entry<D,W,S> after;
        Entry(int hash, D designation, W wiring, S stepping, Node<D,W,S> next) {
            super(hash, designation, wiring, stepping, next);
        }
    }

    transient LinkedHashTable.Entry<D,W,S> head;
    transient LinkedHashTable.Entry<D,W,S> tail;

    private final boolean accessOrder;

    private void linkNodeAtEnd(LinkedHashTable.Entry<D,W,S> p) {
        if (putMode == PUT_NORM) {
            LinkedHashTable.Entry<D,W,S> first = head;
            head = p;
            if (first == null) {
                tail = p;
            } else {
                p.after = first;
                first.before = p;
            }
        } else {
            LinkedHashTable.Entry<D,W,S> last = tail;
            tail = p;
            if (last == null) {
                head = p;
            } else {
                p.after = last;
                last.after = p;
            }
        }
    }

    LinkedHashTable() {
        super();
        accessOrder = false;
    }

    LinkedHashTable(Table<? extends D, ? extends W, ? extends S> t) {
        super();
        accessOrder = false;
        putTableEntries(t, false);
    }

    private static final int PUT_NORM = 0;
    private static final int PUT_FIRST = 1;
    private static final int PUT_LAST = 2;
    private transient int putMode = PUT_NORM;
}
