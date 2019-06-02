package BplusTree;

import Utility.CircularFifoQueue;
import Utility.Utils;

import static Utility.Utils.searchRightmostKey;

class BplusTreeBranchNode<Key extends Comparable, Value> extends BplusTreeNode<Key, Value>{
    private CircularFifoQueue<Key> keys;
    private CircularFifoQueue<BplusTreeNode<Key, Value>> children;

    public BplusTreeBranchNode() {
        keys = new CircularFifoQueue<>(CAPACITY);
        children = new CircularFifoQueue<>(CAPACITY + 1);
    }

    @Override
    public boolean isEmpty() {
        return children.isEmpty();
    }

    @Override
    protected boolean overoccupied() {
        return false;
    }

    @Override
    protected boolean underoccupied() {
        return false;
    }

    @Override
    protected void split() {

    }

    @Override
    protected void rebalance() {

    }

    @Override
    public void add(Key searchKey, Value value) throws BTreeException {
                if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }
        int idx = Utils.searchRightmostKey(keys, searchKey, keys.size());
        idx = idx < 0 ? -(idx + 1) : idx + 1;
        children.get(idx).add(searchKey, value);
    }

    @Override
    public void remove(Key searchKey) throws BTreeException {
        if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }
        int idx = searchRightmostKey(keys, searchKey, keys.size());
        idx = idx < 0 ? -(idx + 1) : idx + 1;
        children.get(idx).remove(searchKey);
    }

    @Override
    public void removeFrom(Key searchKey) throws BTreeException {
        if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }
        int idx = searchRightmostKey(keys, searchKey, keys.size());
        if ( idx < 0 ) {
            keys.removeFrom(idx);
            children.removeFrom(idx + 1);
        }
        else {
            if (idx + 1 < keys.size()) {
                keys.removeFrom(idx + 1);
                children.removeFrom(idx + 1);
            }
            children.get(idx).removeFrom(searchKey);
        }
    }

    @Override
    public Value find(Key searchKey) throws BTreeException {
        if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }
        int idx = searchRightmostKey(keys, searchKey, keys.size());
        idx = idx < 0 ? -(idx + 1) : idx + 1;
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

    public Value pop() {
        return children.get(0).pop();
    }
}
