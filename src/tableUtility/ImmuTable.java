package tableUtility;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

class ImmuTable {
    private static final long SALT32L;
    private static final boolean REVERSE;
    static {
        SALT32L = (int) ((0x243F_6A88_85A3_08D3L * System.nanoTime()) >> 16) & 0xFFFF_FFFFL;
        REVERSE = (SALT32L & 1) == 0;
    }

    static final TableN<?,?,?> EMPTY_TABLE = new TableN<>();

    abstract static class AbstractImmuTable<D,W,S> extends AbstractTable<D,W,S> implements Serializable {

    }

    static final class TableN<D,W,S> extends AbstractImmuTable<D,W,S> {
        // @Stable
        final Object[] table;

        // @Stable
        final int size;

        TableN(Object... input) {
            if (input.length % 3 != 0) {
                throw new InternalError("Invalid length");
            }
            size = input.length * 3;

            int len = size;
            table = new Object[len];
            for (int i = 0; i < input.length; i++) {
                @SuppressWarnings("unchecked")
                    D designation = Objects.requireNonNull((D) input[i]);
                @SuppressWarnings("unchecked")
                    W wiring = Objects.requireNonNull((W) input[i + 1]);
                @SuppressWarnings("unchecked")
                    S stepping = Objects.requireNonNull((S) input[i + 2]);
                int index = probe(designation);
                if (index >= 0) {
                    throw new IllegalArgumentException();
                } else {
                    int destination = -(index + 1);
                    table[destination] = designation;
                    table[destination + 1] = wiring;
                    table[destination + 2] = stepping;
                }
            }
        }

        @Override
        public boolean containsDesignation(Object designation) {
            Objects.requireNonNull(designation);
            return size > 0 && probe(designation) >= 0;
        }

        @Override
        public int hashCode() {
            int hash = 0;
            for (int i = 0; i < table.length; i += 3) {
                Object designation = table[i];
                if (designation != null) {
                    hash += designation.hashCode() ^ table[i + 1].hashCode() ^ table[i + 2].hashCode();
                }
            }
            return hash;
        }

        @Override
        @SuppressWarnings("unchecked")
        public W getWiring(Object designation) {
            if (size == 0) {
                Objects.requireNonNull(designation);
                return null;
            }
            int i = probe(designation);
            if (i >= 0) {
                return (W) table[i + 1];
            } else {
                return null;
            }
        }

        @Override
        @SuppressWarnings("unchecked")
        public S getStepping(Object designation) {
            if (size == 0) {
                Objects.requireNonNull(designation);
                return null;
            }
            int i = probe(designation);
            if (i >= 0) {
                return (S) table[i + 2];
            } else {
                return null;
            }
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return size == 0;
        }

        class TableNIterator implements Iterator<Table.Entry<D,W,S>> {
            private int remaining;
            private int index;

            TableNIterator() {
                remaining = size;
                index = (int) ((SALT32L * (table.length / 3)) >>> 32) * 3;
            }

            @Override
            public boolean hasNext() {
                return remaining > 0;
            }

            private int nextIndex() {
                int index = this.index;
                if (REVERSE) {
                    if ((index += 3) >= table.length) {
                        index = 0;
                    }
                } else {
                    if ((index -= 3) < 0) {
                        index = table.length - 3;
                    }
                }
                return this.index = index;
            }

            @Override
            public Table.Entry<D,W,S> next() {
                if (remaining > 0) {
                    int index;
                    while (table[index = nextIndex()] == null) {}
                    @SuppressWarnings("unchecked")
                    Table.Entry<D,W,S> e = new Holder<>((D) table[index], (W) table[index + 1], (S) table[index + 2]);
                    remaining--;
                    return e;
                } else {
                    throw new NoSuchElementException();
                }
            }
        }
    
        @Override
        public Set<Table.Entry<D,W,S>> entrySet() {
            return new AbstractSet<>() {
                @Override
                public int size() {
                    return TableN.this.size;
                }

                @Override
                public Iterator<Table.Entry<D,W,S>> iterator() {
                    return new TableNIterator();
                }
            };
        }

        private int probe(Object pk) {
            int idx = Math.floorMod(pk.hashCode(), table.length / 3) * 3;
            while (true) {
                @SuppressWarnings("unchecked")
                D ek = (D) table[idx];
                if (ek == null) {
                    return -idx - 1;
                } else if (pk.equals(ek)) {
                    return idx;
                } else if ((idx += 3) == table.length) {
                    idx = 0;
                }
            }
        }
    }
}
