package BplusTree;

public class BplusTree<Key extends Comparable<Key>, Value> {
    private BplusTreeNode<Key, Value> _root = new BplusTreeLeafNode<Key, Value>(null, null, null);

    public void add(Key key, Value value) throws BTreeException {
        _root.add(key, value);

        if (_root.getParent() != null)
            _root = _root.getParent();
    }
    public void remove(Key key) throws BTreeException {
        _root.remove(key);

        if (_root.isEmpty())
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null);
    }
    public void removeFrom(Key key) throws BTreeException {
        _root.removeFrom(key);

        if (_root.isEmpty())
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null);
    }
    public Value find(Key key) throws BTreeException {
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
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null);

        return poppedVal;
    }

}
