package bplustree;

public class BplusTree<Key extends Comparable<Key>, Value> {
    private BplusTreeNode<Key, Value> _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);
    private BplusTreeLeafNode recentlyUsed;
    private int hit = 0, miss = 0;

    public BplusTreeBranchNode getRecentNode() {
        if (recentlyUsed != null && recentlyUsed.getParent() != null && recentlyUsed.getParent().getParent() != null)
            return recentlyUsed.getParent().getParent();
        return null;
    }
    public int getHit() {
        return hit;
    }

    public int getMiss() {
        return miss;
    }

    public int getSampleDepth() {
        return recentlyUsed == null ? -1 : recentlyUsed.getDepth();
    }

    protected void setRecentlyUsed(BplusTreeLeafNode recentlyUsed) {
        this.recentlyUsed = recentlyUsed;
    }

    public boolean isEmpty() {
        return _root.isEmpty();
    }

    public void add(Key key, Value value) throws BTreeException {
        if (getRecentNode() != null && getRecentNode().isInRange(key)) {
            ++hit;
            getRecentNode().add(key, value);
        } else {
            _root.add(key, value);
            ++miss;
        }

        if (_root.getParent() != null)
            _root = _root.getParent();
    }
    public void remove(Key key) throws BTreeException {
        if (getRecentNode() != null && getRecentNode().isInRange(key))
            getRecentNode().remove(key);
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
        if (getRecentNode() != null && getRecentNode().isInRange(key))
            return (Value) getRecentNode().find(key);
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
