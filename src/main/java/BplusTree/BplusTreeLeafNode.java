package BplusTree;

import Utility.CircularFifoQueue;
import Utility.Utils;

import static Utility.Utils.searchRightmostKey;

class BplusTreeLeafNode<Key extends Comparable<Key>, Value> extends BplusTreeNode<Key, Value> {
    private CircularFifoQueue<Key> keys;
    private CircularFifoQueue<Value> leaves;
    private BplusTreeLeafNode next, prev;

    public BplusTreeLeafNode(BplusTreeLeafNode next, BplusTreeLeafNode prev) {
        this.next = next;
        this.prev = prev;

        this.keys = new CircularFifoQueue<>(CAPACITY);
        this.leaves = new CircularFifoQueue<>(CAPACITY + 1);
    }

    public BplusTreeLeafNode getNext() {
        return next;
    }

    public BplusTreeLeafNode getPrev() {
        return prev;
    }

    public void setNext(BplusTreeLeafNode next) {
        this.next = next;
    }

    public void setPrev(BplusTreeLeafNode prev) {
        this.prev = prev;
    }

    @Override
    public boolean isEmpty() {
        return leaves.isEmpty();
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

    public void add(Key searchKey, Value value) throws BTreeException {
        if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }

        int idx = Utils.searchRightmostKey(keys, searchKey, keys.size());

        if (idx >= 0) {
            throw new BTreeException("cannot add currently present key");
        }
        else {
            idx = -(idx + 1);
            keys.insert(searchKey, idx);
            leaves.insert(value, idx);

            if ( overoccupied() )
                split();
        }
    }
    public void remove(Key searchKey) throws BTreeException {
        if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }

        int idx = Utils.searchRightmostKey(keys, searchKey, keys.size());

        if (idx >= 0) {
            keys.remove(idx);
            leaves.remove(idx);

            if( underoccupied() )
                rebalance();
        }
    }
    public void removeFrom(Key searchKey) throws BTreeException {
        if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }

        int idx = searchRightmostKey(keys, searchKey, keys.size());
        idx = idx < 0 ? -(idx + 1) : idx;

        keys.removeFrom(idx);
        leaves.removeFrom(idx);
    }

    @Override
    public Value find(Key searchKey) throws BTreeException {
        if (searchKey == null) {
            throw new BTreeException("Can't search on null Value");
        }
        int idx = searchRightmostKey(keys, searchKey, keys.size());
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
    public Value pop() {
        Value result = leaves.peekFront();

        keys.popFront();
        leaves.popFront();

        return result;
    }
}
