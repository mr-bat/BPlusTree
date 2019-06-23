package bplustree;

import utility.CircularFifoQueue;

import static utility.Utils.searchLeftmostKey;

class BplusTreeLeafNode<Key extends Comparable<Key>, Value> extends BplusTreeNode<Key, Value> {
    private CircularFifoQueue<Key> keys;
    private CircularFifoQueue<Value> leaves;
    private BplusTreeLeafNode next, prev;
    private Key LeftRangeKey;

    public BplusTreeLeafNode(BplusTreeLeafNode next, BplusTreeLeafNode prev, BplusTreeBranchNode parent) {
        this(new CircularFifoQueue<>(CAPACITY), new CircularFifoQueue<>(CAPACITY), next, prev, parent);
    }

    public BplusTreeLeafNode(CircularFifoQueue<Key> keys, CircularFifoQueue<Value> leaves, BplusTreeLeafNode next, BplusTreeLeafNode prev, BplusTreeBranchNode parent) {
        this.keys = keys;
        this.leaves = leaves;
        this.next = next;
        this.prev = prev;
        this.parent = parent;
        this.LeftRangeKey = keys.peekFront();
    }

    @Override
    public boolean isEmpty() {
        return leaves.isEmpty();
    }

    @Override
    protected boolean fullyOccupied() {
        return keys.isAtFullCapacity();
    }

    @Override
    protected boolean underOccupied() {
        return isEmpty();
    }

    @Override
    protected void split() throws BTreeException {
        if (parent == null) {
            parent = new BplusTreeBranchNode(null);
            parent.addNode(this, peekKey());
        }

        CircularFifoQueue<Key> restOfKeys = keys.split();
        CircularFifoQueue<Value> restOfLeaves = leaves.split();

        BplusTreeLeafNode rest = new BplusTreeLeafNode(restOfKeys, restOfLeaves, next, this, parent);
        this.next = rest;

        parent.addNode(rest, rest.peekKey());
    }

    /**
     * Currently rebalance is called only when the node is emptied
     */
    @Override
    protected void rebalance() throws BTreeException {
        if (parent != null)
            parent.removeNode(LeftRangeKey);
    }

    @Override
    public void add(Key key, Value value) throws BTreeException {
        if (key == null) {
            throw new BTreeException("Can't add null keys");
        }

        int idx = searchLeftmostKey(keys, key, keys.size());
        if (idx >= 0)
            throw new BTreeException("Can't add currently present key " + key.toString());

        idx = -(idx + 1);
        if (idx == 0) {
            if (parent != null)
                parent.updateKeyOfNode(key, LeftRangeKey);
            LeftRangeKey = key;
        }

        keys.insert(key, idx);
        leaves.insert(value, idx);

        if (fullyOccupied())
            split();
    }

    @Override
    public void remove(Key key) throws BTreeException {
        if (key == null) {
            throw new BTreeException("Can't search on null Value");
        }

        int idx = searchLeftmostKey(keys, key, keys.size());
        if (idx < 0)
            throw new BTreeException("Can't delete non-existent key " + key.toString());

        keys.remove(idx);
        leaves.remove(idx);

        if(underOccupied()) {
            rebalance();
        }
    }

    @Override
    public void removeFrom(Key thresholdKey) throws BTreeException {
        if (thresholdKey == null) {
            throw new BTreeException("Can't search on null Value");
        }

        int idx = searchLeftmostKey(keys, thresholdKey, keys.size());
        idx = idx < 0 ? -(idx + 1) : idx;

        keys.removeFrom(idx);
        leaves.removeFrom(idx);

        if(underOccupied())
            rebalance();
    }

    @Override
    public Value find(Key searchKey) throws BTreeException {
        if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }
        int idx = searchLeftmostKey(keys, searchKey, keys.size());
        if ( idx < 0 ) {
            return null;
        }
        else {
            return leaves.get(idx);
        }
    }


    @Override
    public Key peekKey() {
        return keys.peekFront();
    }

    @Override
    public Value peekValue() {
        return leaves.peekFront();
    }

    @Override
    public Value pop() throws BTreeException {
        Value result = leaves.peekFront();

        leaves.popFront();
        keys.popFront();

        if(underOccupied())
            rebalance();
        return result;
    }
}
