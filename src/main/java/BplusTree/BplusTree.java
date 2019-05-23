package BplusTree;

import Utility.ArrayUtils;

import static Utility.Utils.searchRightmostKey;

public class BplusTree<Value extends Comparable<Value>> {
    private BplusTreeNode _root;
    private BplusTreeNode getNode(long pointer) {return null;}

    public void add(Value v, long pointer) {}
    public void remove(Value v) {}
    public void removeFrom(Value v) {}
    public void find(Value v) {}
    public void peek() {}
    public void pop() {}

    class BplusTreeNode {
        public static final int KEY_NOT_FOUND = -1;
        public static final int BRANCH_TYPE = 2;
        public static final int LEAF_TYPE = 1;

        private int nodeType;
        private Value[] keys;
        private long[] ptrs;
        private long parent = -1;
        private long _next = -1;
        private long _prev = -1;

        private boolean isEmpty() { return this.ptrs.length == 0; }
        private boolean overoccupied() { return false; }
        private boolean underoccupied() { return false; }
        private void split() {}
        private void rebalance() {}

        public void add(Value searchKey, long pointer) throws BTreeException {
            if (searchKey == null) {
                throw new BTreeException("Can't search on null Value");
            }
            int idx = searchRightmostKey(keys, searchKey, keys.length);
            switch (nodeType) {
                case BRANCH_TYPE:
                    idx = idx < 0 ? -(idx + 1) : idx + 1;
                    getNode(ptrs[idx]).add(searchKey, pointer);
                    break;
                case LEAF_TYPE:
                    if (idx >= 0) {
                        set(ArrayUtils.<Value>insert(keys, idx, searchKey),
                                ArrayUtils.insert(ptrs, idx, pointer));
                        if ( overoccupied() )
                            split();
                    }
                    break;
                default:
                    throw new BTreeException(
                            "Invalid node type '" + nodeType);
            }
        }
        public void remove(Value searchKey) throws BTreeException {
            if (searchKey == null) {
                throw new BTreeException("Can't search on null Value");
            }
            int idx = searchRightmostKey(keys, searchKey, keys.length);
            switch (nodeType) {
                case BRANCH_TYPE:
                    idx = idx < 0 ? -(idx + 1) : idx + 1;
                    getNode(ptrs[idx]).remove(searchKey);
                    break;
                case LEAF_TYPE:
                    if (idx >= 0) {
                        set(ArrayUtils.remove(keys, 0), ArrayUtils.remove(ptrs, 0));
                        if ( underoccupied() )
                            rebalance();
                    }
                    break;
                default:
                    throw new BTreeException(
                            "Invalid node type '" + nodeType);
            }
        }
        public void removeFrom(Value searchKey) throws BTreeException {
            if (searchKey == null) {
                throw new BTreeException("Can't search on null Value");
            }
            int idx = searchRightmostKey(keys, searchKey, keys.length);
            switch (nodeType) {
                case BRANCH_TYPE:
                    idx = idx < 0 ? -(idx + 1) : idx + 1;
                    getNode(ptrs[idx]).removeFrom(searchKey);

                    if (idx < keys.length && idx + 1 < ptrs.length)
                        set(ArrayUtils.removeFrom(keys, idx), ArrayUtils.removeFrom(ptrs, idx + 1));
                    break;
                case LEAF_TYPE:
                    idx = (idx < 0) ? -(idx + 1) : idx;
                    if (idx < keys.length && idx < ptrs.length)
                        set(ArrayUtils.removeFrom(keys, idx), ArrayUtils.removeFrom(ptrs, idx));
                    this._next = -1; // TODO: remove the following nodes
                    if ( underoccupied() )
                        rebalance();
                    break;
                default:
                    throw new BTreeException(
                            "Invalid node type '" + nodeType);
            }
        }
        long find(Value searchKey) throws BTreeException {
            if (searchKey == null) {
                throw new BTreeException("Can't search on null Value");
            }
            int idx = searchRightmostKey(keys, searchKey, keys.length);
            switch (nodeType) {
                case BRANCH_TYPE:
                    idx = idx < 0 ? -(idx + 1) : idx + 1;
                    return getNode(ptrs[idx]).find(searchKey);
                case LEAF_TYPE:
                    if (idx < 0) {
                        return KEY_NOT_FOUND;
                    } else {
                        return ptrs[idx];
                    }
                default:
                    throw new BTreeException(
                            "Invalid node type '" + nodeType);
            }
        }
        public BTreeKey peek() {
            if (this.keys.length == 0)
                return new BTreeKey(null, null);
            return new BTreeKey(keys[0], ptrs[0]);
        }
        public BTreeKey pop() {
            BTreeKey result = new BTreeKey(keys[0], ptrs[0]);
            set(ArrayUtils.remove(keys, 0), ArrayUtils.remove(ptrs, 0));
            if (this.isEmpty())
                rebalance();
            return result;
        }

        private void set(final Value[] values, final long[] ptrs) {
            this.keys = values;
            this.ptrs = ptrs;
        }

    }
}
