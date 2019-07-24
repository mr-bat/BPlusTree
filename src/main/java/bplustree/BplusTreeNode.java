package bplustree;

import utility.CircularFifoQueue;

public abstract class BplusTreeNode<Key extends Comparable, Value> {
    protected static final int CAPACITY = 127;
    protected BplusTreeBranchNode parent;
    protected CircularFifoQueue<Key> keys;
    protected Key LeftRangeKey;

    void setParent(BplusTreeBranchNode parent) {
        this.parent = parent;
    }
    BplusTreeBranchNode getParent() {
        return parent;
    }
    public abstract boolean isEmpty();
    protected abstract boolean fullyOccupied();
    protected abstract boolean underOccupied();
    protected abstract void split() throws BTreeException;
    protected abstract void rebalance() throws BTreeException;

    public boolean isInRange(Key key) throws BTreeException {
        if (key == null)
            throw new BTreeException("Can't work with null key");
        if (LeftRangeKey == null)
            return true;
        return key.compareTo(LeftRangeKey) > -1 && key.compareTo(keys.peekBack()) < 1;
    }

    public abstract void add(Key searchKey, Value value) throws BTreeException;
    public abstract Value remove(Key searchKey) throws BTreeException;

    void bottomUpRemoveFrom(Key thresholdKey) throws BTreeException {
        if (parent == null || isInRange(thresholdKey))
            removeFrom(thresholdKey);
        else
            parent.bottomUpRemoveFrom(thresholdKey);
    }

    public abstract void removeFrom(Key searchKey) throws BTreeException;
    public abstract Value find(Key searchKey) throws BTreeException;
    public abstract BplusTreeLeafNode.BplusTreeIterator peekLast();
    public abstract BplusTreeLeafNode peekLastNode();
    public abstract Key peekKey();
    public abstract Value peekValue();
    public abstract Value pop() throws BTreeException;
    public abstract Value popBack() throws BTreeException;

}
