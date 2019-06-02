package BplusTree;

public class BplusTree<Key extends Comparable<Key>, Value> {
    private BplusTreeNode<Key, Value> _root = new BplusTreeLeafNode<Key, Value>();

    public void add(Key key, Value value) throws BTreeException {
        _root.add(key, value);
    }
    public void remove(Key key) throws BTreeException {
        _root.remove(key);
    }
    public void removeFrom(Key key) throws BTreeException {
        _root.removeFrom(key);
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
    public Value pop() {
        return _root.pop();
    }

}
