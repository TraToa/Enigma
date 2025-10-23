package tableUtility;
import java.util.Objects;

final class Holder<D,W,S> implements Table.Entry<D,W,S> {
    final D designation;
    final W wiring;
    final S stepping;

    Holder(D designation, W wiring, S stepping) {
        this.designation = Objects.requireNonNull(designation);
        this.wiring = Objects.requireNonNull(wiring);
        this.stepping = Objects.requireNonNull(stepping);
    }

    @Override
    public D getDesignation() {
        return this.designation;
    }

    @Override
    public W getWiring() {
        return this.wiring;
    }

    @Override
    public S getStepping() {
        return this.stepping;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Table.Entry<?, ?, ?> e
                && designation.equals(e.getDesignation())
                && wiring.equals(e.getWiring())
                && stepping.equals(e.getStepping());
    }

    @Override
    public int hashCode() {
        return designation.hashCode() ^ wiring.hashCode() ^ stepping.hashCode();
    }

    @Override
    public String toString() {
        return designation + ": " + wiring + " - " + stepping;
    }
}
