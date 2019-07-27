package utility;

public class SimpleCloneable implements Cloneable {
    public SimpleCloneable clone() throws CloneNotSupportedException {
        return (SimpleCloneable) super.clone();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SimpleCloneable;
    }
}