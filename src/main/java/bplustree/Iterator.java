package bplustree;

public interface Iterator<K extends Comparable<K>, V> {
    public boolean hasNext();
    public void goToNext();
    public K getKey();
    public V getValue();
}