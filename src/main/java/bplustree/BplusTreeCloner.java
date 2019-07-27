package bplustree;

import utility.CircularFifoQueue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class BplusTreeCloner<K extends Comparable, V> {
    private BplusTree tree;
    private Map<BplusTreeNode, BplusTreeNode> nodeMap = new HashMap<>();

    BplusTreeCloner(BplusTree tree) {
        this.tree = tree;
    }

    public BplusTreeNode clone(BplusTreeNode node) throws CloneNotSupportedException {
        if (node == null)
            return null;
        if (node instanceof BplusTreeLeafNode)
            return clone((BplusTreeLeafNode) node);
        if (node instanceof BplusTreeBranchNode)
            return clone((BplusTreeBranchNode) node);

        throw new IllegalArgumentException("node is in illegal state");
    }

    private BplusTreeLeafNode clone(BplusTreeLeafNode node) throws CloneNotSupportedException {
        if (node == null)
            return null;
        if (nodeMap.containsKey(node))
            return (BplusTreeLeafNode) nodeMap.get(node);

        BplusTreeLeafNode cloned = new BplusTreeLeafNode(null, null, null, tree);
        nodeMap.put(node, cloned);

        cloneBplusTreeNodeInternals(node, cloned);
        cloned.leaves = node.leaves.clone();


        if (node.next != null)
            cloned.next = clone(node.next);
        if (node.prev != null)
            cloned.prev = clone(node.prev);

        return cloned;
    }

    private BplusTreeBranchNode clone(BplusTreeBranchNode node) throws CloneNotSupportedException {
        if (node == null)
            return null;
        if (nodeMap.containsKey(node))
            return (BplusTreeBranchNode) nodeMap.get(node);

        BplusTreeBranchNode cloned = new BplusTreeBranchNode(null);
        nodeMap.put(node, cloned);

        cloneBplusTreeNodeInternals(node, cloned);
        cloned.children = node.children.clone(this);

        return cloned;
    }

    private void cloneBplusTreeNodeInternals(BplusTreeNode base, BplusTreeNode cloned) throws CloneNotSupportedException {
        cloned.parent = clone(base.parent);
        cloned.keys = base.keys.clone();
        if (base.LeftRangeKey instanceof Cloneable)
            try {
                cloned.LeftRangeKey = (Comparable) base.LeftRangeKey.getClass().getMethod("clone").invoke(base.LeftRangeKey);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                cloned.LeftRangeKey = base.LeftRangeKey;
                throw new CloneNotSupportedException("Key is not cloneable");
            }
        else
            cloned.LeftRangeKey = base.LeftRangeKey;
    }

    private Object getFieldOfCircularFifoQueue(CircularFifoQueue queue, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = queue.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(queue);
    }

    private Object[] getArrayFieldOfCircularFifoQueue(CircularFifoQueue queue) throws NoSuchFieldException, IllegalAccessException {
        Field field = queue.getClass().getDeclaredField("elements");
        field.setAccessible(true);
        return (Object[]) field.get(queue);
    }

    private<T> void setFieldOfCircularFifoQueue(CircularFifoQueue queue, String fieldName, T value) throws NoSuchFieldException, IllegalAccessException {
        Field field = queue.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(queue, value);
    }

    private<T> void copyCircularFifoQueueField(CircularFifoQueue from, CircularFifoQueue to, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        T value = (T)getFieldOfCircularFifoQueue(from, fieldName);
        Field field = to.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(to, value);
    }
}