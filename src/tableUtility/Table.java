package tableUtility;
import java.util.Collection;
import java.util.Set;

public interface Table<D,W,S> {
    int size();
    boolean isEmpty();
    boolean containsDesignation(Object designation);
    W getWiring(Object designation);
    S getStepping(Object designation);
    Set<D> designationSet();
    Collection<W> wiringSet();
    Collection<S> steppingSet();
    Set<Table.Entry<D,W,S>> entrySet();

    interface Entry<D,W,S> {
        D getDesignation();
        W getWiring();
        S getStepping();
        int hashCode();
    }

    @SafeVarargs
    @SuppressWarnings("varargs")
    static <D,W,S> Table<D,W,S> ofEntries(Entry<? extends D, ? extends W, ? extends S>... entries) {
        if (entries.length == 0) {
            @SuppressWarnings("unchecked")
            var table = (Table<D,W,S>) ImmuTable.EMPTY_TABLE;
            return table;
        } else {
            Object[] objects = new Object[entries.length * 3];
            int a = 0;
            for (Entry<? extends D, ? extends W, ? extends S> entry : entries) {
                objects[a++] = entry.getDesignation();
                objects[a++] = entry.getWiring();
                objects[a++] = entry.getWiring();
            }
            return new ImmuTable.TableN<>(objects);
        }
    }

    static <D,W,S> Entry<D,W,S> entry(D designation, W wiring, S stepping) {
        return new Holder<>(designation, wiring, stepping);
    }
}
