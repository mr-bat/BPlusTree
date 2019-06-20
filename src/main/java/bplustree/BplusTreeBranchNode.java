package bplustree;

import utility.CircularFifoQueue;

import static utility.Utils.searchLeftmostKey;
import static utility.Utils.searchRightmostKey;

class BplusTreeBranchNode<Key extends Comparable, Value> extends BplusTreeNode<Key, Value>{
    private CircularFifoQueue<Key> keys;
    private CircularFifoQueue<BplusTreeNode<Key, Value>> children;
    private Key LeftRangeKey;

    public BplusTreeBranchNode(BplusTreeBranchNode parent) {
        this(new CircularFifoQueue<>(CAPACITY), new CircularFifoQueue<>(CAPACITY), parent);
    }

    public BplusTreeBranchNode(CircularFifoQueue<Key> keys, CircularFifoQueue<BplusTreeNode<Key, Value>> children, BplusTreeBranchNode parent) {
        this.keys = keys;
        this.children = children;
        this.parent = parent;
        this.LeftRangeKey = keys.peekFront();
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
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
        CircularFifoQueue<BplusTreeNode<Key, Value>> restOfChildren = children.split();

        BplusTreeBranchNode rest = new BplusTreeBranchNode(restOfKeys, restOfChildren, parent);
        for (BplusTreeNode node :
                restOfChildren) {
            node.setParent(rest);
        }

        parent.addNode(rest, rest.peekKey());
    }

    @Override
    protected void rebalance() throws BTreeException {
        if (parent != null)
            parent.removeNode(LeftRangeKey);
    }

    void addNode(BplusTreeNode child, Key key) throws BTreeException {
        int idx = searchLeftmostKey(keys, key, keys.size());
        if (idx >= 0)
            throw new BTreeException("Can't add node when it exists");

        idx = -(idx + 1);
        keys.insert(key, idx);
        children.insert(child, idx);

        if (idx == 0)
            LeftRangeKey = key;
        if (fullyOccupied())
            split();
    }

    void removeNode(Key key) throws BTreeException {
        int idx = searchRightmostKey(keys, key, keys.size());
        idx = idx < 0 ? -(idx + 1) : idx;

        keys.remove(idx);
        children.remove(idx);

        if (underOccupied())
            rebalance();
    }

    void updateKeyOfNode(Key newKey, Key currKey) throws BTreeException {
        int idx = searchLeftmostKey(keys, currKey, keys.size());
        if (idx < 0)
            throw new BTreeException("Key does not exist to be updated");

        keys.set(idx, newKey);

        if (idx == 0) {
            if (parent != null)
                parent.updateKeyOfNode(newKey, LeftRangeKey);

            LeftRangeKey = newKey;
        }
    }

    @Override
    public void add(Key key, Value value) throws BTreeException {
        if (key == null) {
            throw new BTreeException("Can't add null keys");
        }
        int idx = searchRightmostKey(keys, key, keys.size());

        idx = idx < 0 ? -(idx + 1) : idx;
        children.get(idx).add(key, value);
    }

    @Override
    public void remove(Key key) throws BTreeException {
        if (key == null) {
            throw new BTreeException("Can't search on null Value");
        }
        int idx = searchRightmostKey(keys, key, keys.size());

        idx = idx < 0 ? -(idx + 1) : idx;
        children.get(idx).remove(key);
    }

    @Override
    public void removeFrom(Key thresholdKey) throws BTreeException {
        if (thresholdKey == null) {
            throw new BTreeException("Can't search on null Value");
        }
        int idx = searchRightmostKey(keys, thresholdKey, keys.size());

        if ( idx < 0 ) {
            idx = -(idx + 1);
            keys.removeFrom(idx + 1);
            children.removeFrom(idx + 1);
            children.get(idx).removeFrom(thresholdKey);
        }
        else {
            keys.removeFrom(idx);
            children.removeFrom(idx);
        }
    }

    @Override
    public Value find(Key searchKey) throws BTreeException {
        if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }
        int idx = searchRightmostKey(keys, searchKey, keys.size());

        idx = idx < 0 ? -(idx + 1) : idx;
        return children.get(idx).find(searchKey);
    }

    @Override
    public Key peekKey() {
        return children.get(0).peekKey();
    }

    @Override
    public Value peekValue() {
        return children.get(0).peekValue();
    }

    @Override
    public Value pop() throws BTreeException {
        return children.get(0).pop();
    }
}
