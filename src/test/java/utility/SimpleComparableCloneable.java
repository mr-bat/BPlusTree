package utility;

public class SimpleComparableCloneable implements Comparable<SimpleComparableCloneable>, Cloneable {
    @Override
    public SimpleComparableCloneable clone() throws CloneNotSupportedException {
        return (SimpleComparableCloneable) super.clone();
    }

    @Override
    public int compareTo(SimpleComparableCloneable o) {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimpleComparableCloneable;
    }
}
