package bplustree;

import com.google.common.annotations.Beta;

public class BplusTree<Key extends Comparable<Key>, Value> {
    private BplusTreeNode<Key, Value> _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);
    private BplusTreeLeafNode recentlyUsed;
    private int hit = 0, miss = 0;
    private boolean cacheDisabled = false;

    public BplusTree() {
        this(false);
    }

    public BplusTree(boolean cacheDisabled) {
        this.cacheDisabled = cacheDisabled;
    }

    public boolean cacheEnabled() {
        return !cacheDisabled;
    }

    public BplusTreeBranchNode getRecentNode() {
        if (cacheDisabled)
            return null;
        if (recentlyUsed != null && recentlyUsed.getParent() != null)
            return recentlyUsed.getParent().getParent();
        return null;
    }
    public int getHit() {
        return hit;
    }

    public int getMiss() {
        return miss;
    }

    @Beta
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
        if (getRecentNode() != null && getRecentNode().isInRange(key)) {
            getRecentNode().remove(key);
            ++hit;
        } else {
            _root.remove(key);
            ++miss;
        }

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
        if (getRecentNode() != null && getRecentNode().isInRange(key)) {
            ++hit;
            return (Value) getRecentNode().find(key);
        } else {
            ++miss;
            return _root.find(key);
        }
    }
    public BplusTreeLeafNode.BplusTreeIterator peekLast() {
        return _root.peekLast();
    }
    public Value peekValue() {
        return _root.peekValue();
    }
    public Key peekKey() {
        return _root.peekKey();
    }
    public Value poll() throws BTreeException {
        Value poppedVal = _root.pop();
        if (_root.isEmpty())
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);

        return poppedVal;
    }

}
