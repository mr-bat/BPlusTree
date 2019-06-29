package bplustree;

import utility.CircularFifoQueue;

import static utility.Utils.searchLeftmostKey;

class BplusTreeLeafNode<Key extends Comparable<Key>, Value> extends BplusTreeNode<Key, Value> {
    protected CircularFifoQueue<Value> leaves;
    protected BplusTreeLeafNode next;
    protected BplusTreeLeafNode prev;
    protected BplusTree tree;

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

    public int getDepth() {
        BplusTreeNode node = this;
        int depth = 1;
        while (node.getParent() != null) {
            ++depth;
            node = node.getParent();
        }

        return depth;
    }

    public BplusTreeLeafNode(BplusTreeLeafNode next, BplusTreeLeafNode prev, BplusTreeBranchNode parent, BplusTree tree) {
        this(new CircularFifoQueue<>(CAPACITY), new CircularFifoQueue<>(CAPACITY), next, prev, parent, tree);
    }

    public BplusTreeLeafNode(CircularFifoQueue<Key> keys, CircularFifoQueue<Value> leaves, BplusTreeLeafNode next, BplusTreeLeafNode prev, BplusTreeBranchNode parent, BplusTree tree) {
        this.keys = keys;
        this.leaves = leaves;
        this.next = next;
        this.prev = prev;
        this.parent = parent;
        this.tree = tree;
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

        BplusTreeLeafNode rest = new BplusTreeLeafNode(restOfKeys, restOfLeaves, next, this, parent, tree);
        this.next = rest;

        parent.addNode(rest, rest.peekKey());
    }

    /**
     * Currently rebalance is called only when the node is emptied
     */
    @Override
    protected void rebalance() throws BTreeException {
        tree.setRecentlyUsed(null);

        if (getPrev() != null)
            getPrev().setNext(getNext());
        if (getNext() != null)
            getNext().setPrev(getPrev());

        if (parent != null)
            parent.removeNode(LeftRangeKey);
    }

    @Override
    public void add(Key key, Value value) throws BTreeException {
        if (key == null) {
            throw new BTreeException("Can't work with null key");
        }
        tree.setRecentlyUsed(this);

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
            throw new BTreeException("Can't work with null key");
        }
        tree.setRecentlyUsed(this);

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
            throw new BTreeException("Can't work with null key");
        }
        tree.setRecentlyUsed(this);

        int idx = searchLeftmostKey(keys, searchKey, keys.size());
        if ( idx < 0 ) {
            return null;
        }
        else {
            return leaves.get(idx);
        }
    }

    @Override
    public BplusTreeIterator peekLast() {
        return isEmpty() ? null : new BplusTreeIterator(this, keys.size() - 1);
    }

    @Override
    public BplusTreeLeafNode peekLastNode() {
        return this;
    }


    @Override
    public Key peekKey() {
        return keys.peekFront();
    }

    @Override
    public Value peekValue() {
        return leaves.peekFront();
    }
    public Value peekBackValue() {
        return leaves.peekBack();
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

    @Override
    public Value popBack() throws BTreeException {
        Value result = leaves.peekBack();

        leaves.popBack();
        keys.popBack();

        if(underOccupied())
            rebalance();
        return result;
    }

    public class BplusTreeIterator implements Iterator{
        private BplusTreeLeafNode<Key, Value> node;
        private int index;

        public BplusTreeIterator(BplusTreeLeafNode<Key, Value> node, int index) {
            this.node = node;
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return index > 0 || node.getPrev() != null;
        }

        @Override
        public void goToNext() {
            if (index - 1 >= 0)
                --index;
            else {
                node = node.getPrev();
                index = node.keys.size() - 1;
            }
        }

        @Override
        public Key getKey() {
            return node.keys.get(index);
        }

        @Override
        public Value getValue() {
            return node.leaves.get(index);
        }

    }

}
