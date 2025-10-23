package tableUtility;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

abstract class AbstractTable<D,W,S> implements Table<D,W,S> {
    public int size() {
        return entrySet().size();
    }

    public boolean isEmpty() {
        return (size() == 0);
    }

    public boolean containsDesignation(Object designation) {
        Iterator<Entry<D,W,S>> i = entrySet().iterator();
        if (designation == null) {
            while (i.hasNext()) {
                Entry<D,W,S> e = i.next();
                if (e.getDesignation() == null) {
                    return true;
                }
            }
        } else {
            while (i.hasNext()) {
                Entry<D,W,S> e = i.next();
                if (designation.equals(e.getDesignation())) {
                    return true;
                }
            }
        }
        return false;
    }

    public W getWiring(Object designation) {
        Iterator<Entry<D,W,S>> i = entrySet().iterator();
        if (designation == null) {
            while (i.hasNext()) {
                Entry<D,W,S> e = i.next();
                if (e.getDesignation()==null)
                    return e.getWiring();
            }
        } else {
            while (i.hasNext()) {
                Entry<D,W,S> e = i.next();
                if (designation.equals(e.getDesignation()))
                    return e.getWiring();
            }
        }
        return null;
    }

    public S getStepping(Object designation) {
        Iterator<Entry<D,W,S>> i = entrySet().iterator();
        if (designation == null) {
            while (i.hasNext()) {
                Entry<D,W,S> e = i.next();
                if (e.getDesignation()==null)
                    return e.getStepping();
            }
        } else {
            while (i.hasNext()) {
                Entry<D,W,S> e = i.next();
                if (designation.equals(e.getDesignation()))
                    return e.getStepping();
            }
        }
        return null;
    }

    transient Set<D> designationSet;
    transient Collection<W> wiringSet;
    transient Collection<S> steppingSet;

    public Set<D> designationSet() {
        Set<D> ds = designationSet;
        if (ds == null) {
            ds = new AbstractSet<>() {
                public Iterator<D> iterator() {
                    return new DesignationIterator();
                }

                public int size() {
                    return AbstractTable.this.size();
                }

                public boolean isEmpty() {
                    return AbstractTable.this.isEmpty();
                }

                public boolean contains(Object designation) {
                    return AbstractTable.this.containsDesignation(designation);
                }
            };
            designationSet = ds;
        }
        return ds;
    }

    public Collection<W> wiringSet() {
        Collection<W> ws = wiringSet;
        if (ws == null) {
            ws = new AbstractSet<>() {
                public Iterator<W> iterator() {
                    return new WiringIterator();
                }

                public int size() {
                    return AbstractTable.this.size();
                }

                public boolean isEmpty() {
                    return AbstractTable.this.isEmpty();
                }

                public boolean contains(Object wiring) {
                    return AbstractTable.this.containsDesignation(wiring);
                }
            };
            wiringSet = ws;
        }
        return ws;
    }

    public Collection<S> steppingSet() {
        Collection<S> ss = steppingSet;
        if (ss == null) {
            ss = new AbstractSet<>() {
                public Iterator<S> iterator() {
                    return new SteppingIterator();
                }

                public int size() {
                    return AbstractTable.this.size();
                }

                public boolean isEmpty() {
                    return AbstractTable.this.isEmpty();
                }

                public boolean contains(Object stepping) {
                    return AbstractTable.this.containsDesignation(stepping);
                }
            };
            steppingSet = ss;
        }
        return ss;
    }

    abstract Set<Entry<D,W,S>> entrySet();

    final class DesignationIterator implements Iterator<D> {
        private final Iterator<Entry<D,W,S>> i = entrySet().iterator();

        public boolean hasNext() {
            return i.hasNext();
        }

        public void remove() {
            i.remove();
        }

        public D next() {
            return i.next().getDesignation();
        }
    }

    final class WiringIterator implements Iterator<W> {
        private final Iterator<Entry<D,W,S>> i = entrySet().iterator();

        public boolean hasNext() {
            return i.hasNext();
        }

        public void remove() {
            i.remove();
        }

        public W next() {
            return i.next().getWiring();
        }
    }

    final class SteppingIterator implements Iterator<S> {
        private final Iterator<Entry<D,W,S>> i = entrySet().iterator();

        public boolean hasNext() {
            return i.hasNext();
        }

        public void remove() {
            i.remove();
        }

        public S next() {
            return i.next().getStepping();
        }
    }
}
