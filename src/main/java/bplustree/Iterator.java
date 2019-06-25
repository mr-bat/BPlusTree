package bplustree;

public interface Iterator<Key extends Comparable<Key>, Value> {
    public boolean hasNext();
    public void goToNext();
    public Key getKey();
    public Value getValue();
}