package bplustree;

abstract class BplusTreeNode<Key extends Comparable, Value> {
    protected static final int CAPACITY = 127;
    protected BplusTreeBranchNode parent;

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

    public abstract void add(Key searchKey, Value value) throws BTreeException;
    public abstract void remove(Key searchKey) throws BTreeException;
    public abstract void removeFrom(Key searchKey) throws BTreeException;
    public abstract Value find(Key searchKey) throws BTreeException;
    public abstract Key peekKey();
    public abstract Value peekValue();
    public abstract Value pop() throws BTreeException;

}
