package bplustree;

public class BplusTree<Key extends Comparable<Key>, Value> {
    private BplusTreeNode<Key, Value> _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);
    private BplusTreeLeafNode recentlyUsed;

    protected void setRecentlyUsed(BplusTreeLeafNode recentlyUsed) {
        this.recentlyUsed = recentlyUsed;
    }

    public boolean isEmpty() {
        return _root.isEmpty();
    }

    public void add(Key key, Value value) throws BTreeException {
        if (recentlyUsed != null && recentlyUsed.getParent() != null && recentlyUsed.getParent().isInRange(key))
            recentlyUsed.getParent().add(key, value);
        else
            _root.add(key, value);

        if (_root.getParent() != null)
            _root = _root.getParent();
    }
    public void remove(Key key) throws BTreeException {
        if (recentlyUsed != null && recentlyUsed.getParent() != null && recentlyUsed.getParent().isInRange(key))
            recentlyUsed.getParent().remove(key);
        else
            _root.remove(key);

        if (_root.isEmpty())
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);
    }
    public void removeFrom(Key key) throws BTreeException {
        _root.removeFrom(key);

        if (_root.isEmpty()) {
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);
            recentlyUsed = null;
        }
    }
    public Value find(Key key) throws BTreeException {
        if (recentlyUsed != null && recentlyUsed.getParent() != null && recentlyUsed.getParent().isInRange(key))
            return (Value) recentlyUsed.getParent().find(key);
        else
            return _root.find(key);
    }
    public Value peekValue() {
        return _root.peekValue();
    }
    public Key peekKey() {
        return _root.peekKey();
    }
    public Value pop() throws BTreeException {
        Value poppedVal = _root.pop();
        if (_root.isEmpty())
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);

        return poppedVal;
    }

}
