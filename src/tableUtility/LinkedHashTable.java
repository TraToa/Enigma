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

    final boolean accessOrder;

    LinkedHashTable() {
        super();
        accessOrder = false;
    }

    LinkedHashTable(Table<? extends D, ? extends W, ? extends S> t) {
        super();
        accessOrder = false;
        putTableEntries(t, false);
    }
}
