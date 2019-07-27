package bplustree;

import com.google.common.annotations.Beta;

public class BplusTree<Key extends Comparable<Key>, Value> {
    private BplusTreeNode<Key, Value> _root = new BplusTreeLeafNode<>(null, null, null, this);
    private BplusTreeLeafNode<Key, Value> recentlyUsed;
    private BplusTreeLeafNode<Key, Value> lastNode = (BplusTreeLeafNode) _root;
    private int hit = 0, miss = 0;
    private boolean cacheDisabled;

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BplusTree))
            return false;

        BplusTreeLeafNode.BplusTreeIterator here = peekLast();
        BplusTreeLeafNode.BplusTreeIterator there = ((BplusTree) obj).peekLast();

        if (here == null || there == null)
            return here == there;

        while (true) {
            if (!here.getKey().equals(there.getKey()))
                return false;
            if (!here.getValue().equals(there.getValue()))
                return false;
            if (here.hasNext() != there.hasNext())
                return false;

            if (here.hasNext()) {
                here.goToNext();
                there.goToNext();
            }
            else
                break;
        }

        return true;
    }

    @Override
    public BplusTree<Key, Value> clone() throws CloneNotSupportedException{
        BplusTree cloned = new BplusTree();
        BplusTreeCloner treeCloner = new BplusTreeCloner<Key, Value>(cloned);

        cloned._root = treeCloner.clone(this._root);
        cloned.recentlyUsed = (BplusTreeLeafNode) treeCloner.clone(recentlyUsed);
        cloned.lastNode = (BplusTreeLeafNode) treeCloner.clone(lastNode);
        cloned.hit = this.hit;
        cloned.miss = this.miss;
        cloned.cacheDisabled = this.cacheDisabled;
        return cloned;
    }

    public BplusTree() {
        this(false);
    }

    public BplusTree(boolean cacheDisabled) {
        this.cacheDisabled = cacheDisabled;
    }

    public boolean cacheEnabled() {
        return !cacheDisabled;
    }

    public BplusTreeBranchNode<Key, Value> getRecentNode() {
        if (cacheDisabled)
            return null;
        if (recentlyUsed != null)
            return recentlyUsed.getParent();
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

    public void addWithCacheLastRecent(Key key, Value value) throws BTreeException {
        if (recentlyUsed != null && recentlyUsed.isInRange(key)) {
            ++hit;
            recentlyUsed.add(key, value);
            if (_root.getParent() != null)
                _root = _root.getParent();
            if (lastNode.getNext() != null)
                lastNode = lastNode.getNext();
        } else
            add(key, value);
    }
    public void add(Key key, Value value) throws BTreeException {
//        if (!cacheDisabled && lastNode.isInRange(key)) {
//            ++hit;
//            lastNode.add(key, value);
//        } else
//        if (getRecentNode() != null && getRecentNode().isInRange(key)) {
//            ++hit;
//            getRecentNode().add(key, value);
//        } else
        {
            _root.add(key, value);
            ++miss;
        }

        if (_root.getParent() != null)
            _root = _root.getParent();
        if (lastNode.getNext() != null)
            lastNode = lastNode.getNext();
    }
    public Value remove(Key key) throws BTreeException {
        Value result;
//        if (!cacheDisabled && lastNode.isInRange(key)) {
//            result = lastNode.remove(key);
//            ++hit;
//        } else
//        if (getRecentNode() != null && getRecentNode().isInRange(key)) {
//            result = getRecentNode().remove(key);
//            ++hit;
//        } else
        {
            result = _root.remove(key);
            ++miss;
        }

        if (_root.isEmpty()) {
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);
            lastNode = (BplusTreeLeafNode) _root;
        }
        else if (lastNode.isEmpty())
            lastNode = lastNode.getPrev();

        return result;
    }
    // TODO: CHECK Last Node
    public void removeFrom(Key key) throws BTreeException {
        if (!cacheDisabled) {
            lastNode.bottomUpRemoveFrom(key);
        } else
            _root.removeFrom(key);

        if (_root.isEmpty()) {
            _root = new BplusTreeLeafNode<>(null, null, null, this);
            lastNode = (BplusTreeLeafNode<Key, Value>) _root;
//            recentlyUsed = null;
        }
        else if (lastNode.isEmpty() || lastNode.peekKey().compareTo(key) == 0) {
            lastNode = lastNode.getPrev();
        } else if (lastNode.peekKey().compareTo(key) > 0)
            lastNode = _root.peekLastNode();

//        if (recentlyUsed != null && recentlyUsed.peekKey().compareTo(key) > -1)
//            recentlyUsed = lastNode;
    }
    public Value find(Key key) throws BTreeException {
//        if (!cacheDisabled && lastNode.isInRange(key)) {
//            ++hit;
//            return lastNode.find(key);
//        } else
//        if (getRecentNode() != null && getRecentNode().isInRange(key)) {
//            ++hit;
//            return getRecentNode().find(key);
//        } else
        {
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
    public Value peekLastValue() {
        if (lastNode == null)
            throw new RuntimeException("lastNode can not be null");
        return lastNode.peekBackValue();
    }
    public Key peekLastKey() {
        if (lastNode == null)
            throw new RuntimeException("lastNode can not be null");
        return lastNode.peekBackKey();
    }
    public Value pop() throws BTreeException {
        Value poppedVal = _root.pop();
        if (_root.isEmpty()) {
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);
            lastNode = (BplusTreeLeafNode) _root;
//            recentlyUsed = lastNode;
        }

        return poppedVal;
    }
    public Value popBack() throws BTreeException {
        if (lastNode == null)
            throw new RuntimeException("lastNode can not be null");

        Value poppedVal = lastNode.popBack();

        if (_root.isEmpty()) {
            _root = new BplusTreeLeafNode<Key, Value>(null, null, null, this);
            lastNode = (BplusTreeLeafNode) _root;
        }
        else if (lastNode.isEmpty())
            lastNode = lastNode.getPrev();

        return poppedVal;
    }
}
