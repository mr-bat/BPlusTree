package bplustree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import utility.CircularFifoQueue;
import utility.SimpleCloneable;
import utility.SimpleComparableCloneable;

import java.lang.reflect.Field;
import java.text.MessageFormat;

import static java.lang.Math.min;

class BplusTreeTest {
    private static final int MAXN = 30 * 1000;
    private BplusTree<Integer, Integer> bplusTree;


    @org.junit.jupiter.api.BeforeEach
    void setUp() throws BTreeException {
        bplusTree = new BplusTree<>();
        for (int i = 1; i < MAXN; i++) {
            bplusTree.add(i, 2 * i);
        }
        bplusTree.add(0, 0);
    }

    @org.junit.jupiter.api.Test
    void shouldBeInRange() throws NoSuchFieldException, IllegalAccessException, BTreeException {
        Field _root = BplusTree.class.getDeclaredField("_root");
        _root.setAccessible(true);
        Field keys = BplusTreeNode.class.getDeclaredField("keys");
        keys.setAccessible(true);
        Field children = BplusTreeBranchNode.class.getDeclaredField("children");
        children.setAccessible(true);
        BplusTreeBranchNode node = (BplusTreeBranchNode) ((CircularFifoQueue<BplusTreeNode>) children.get(_root.get(bplusTree))).peekFront();
        CircularFifoQueue<Integer> queue = (CircularFifoQueue) keys.get(node);

        Assertions.assertTrue(node.isInRange(queue.peekFront()));
        Assertions.assertTrue(node.isInRange(queue.peekBack()));
        Assertions.assertFalse(node.isInRange(queue.peekFront() - 1));
        Assertions.assertFalse(node.isInRange(queue.peekBack() + 1));
    }

    @org.junit.jupiter.api.Test
    void shouldBeEmpty() throws BTreeException {
        bplusTree.removeFrom(0);
        Assertions.assertTrue(bplusTree.isEmpty());
    }

    @org.junit.jupiter.api.Test
    void shouldBeFull() {
        Assertions.assertFalse(bplusTree.isEmpty());
    }


    @org.junit.jupiter.api.Test
    void shouldAdd() throws BTreeException {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.add(null, 0));
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.add(0, 0));
        System.out.println(MessageFormat.format("test finished with {0} hits and {1} misses", bplusTree.getHit(), bplusTree.getMiss()));
        shouldFind();
    }

    @org.junit.jupiter.api.Test
    void shouldRemove() throws BTreeException {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.remove(null));
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.remove(-1));

        for (int i = 0; i < MAXN; i += 2) {
            Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
            bplusTree.remove(i);
            Assertions.assertNull(bplusTree.find(i));
        }
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.remove(2));

    }

    @org.junit.jupiter.api.Test
    void shouldRemoveRange() throws BTreeException {
        for (int i = MAXN / 30; i < MAXN * 29 / 30; i += 2) {
            Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
            bplusTree.remove(i);
            Assertions.assertNull(bplusTree.find(i));
        }
        for (int i = 0; i < MAXN / 30; i++)
            Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
        for (int i = MAXN * 29 / 30; i < MAXN; i++)
            Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
    }

    /**
     * @implNote forward > backward should hold
     */
    @org.junit.jupiter.api.Test
    void shouldAddRemoveAlternatively() throws BTreeException {
        final int forward = 10;
        final int backward = 2;
        assert forward > backward;

        for (int i = 0; i < MAXN; i += forward - backward) {
            for (int j = i; j < min(i + forward, MAXN); j++) {
                Assertions.assertEquals(Integer.valueOf(2 * j), bplusTree.find(j));
                bplusTree.remove(j);
                Assertions.assertNull(bplusTree.find(j));
            }
            for (int j = 1; j <= backward; j++) {
                int k = i + forward - j;
                bplusTree.add(k, 2 * k);
                Assertions.assertEquals(Integer.valueOf(2 * k), bplusTree.find(k));
            }
        }
    }

    @org.junit.jupiter.api.Test
    void shouldRemoveAndAddHalfRepeatedly() throws BTreeException {
        int range =  MAXN;
        while (range > 1) {
            Assertions.assertThrows(BTreeException.class, () -> bplusTree.remove(null));
            Assertions.assertThrows(BTreeException.class, () -> bplusTree.remove(-1));

            for (int i = 0; i < range; i++) {
                Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
                bplusTree.remove(i);
                Assertions.assertNull(bplusTree.find(i));
            }

            Assertions.assertThrows(BTreeException.class, () -> bplusTree.remove(0));

            range /= 2;
            for (int i = 0; i < range; i++) {
                bplusTree.add(i, 2 * i);
                Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
            }

            Assertions.assertThrows(BTreeException.class, () -> bplusTree.add(null, 0));
            Assertions.assertThrows(BTreeException.class, () -> bplusTree.add(0, 0));
            Assertions.assertThrows(BTreeException.class, () -> bplusTree.find(null));
        }
    }

    @org.junit.jupiter.api.Test
    void shouldRemoveFromNull() {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.removeFrom(null));
    }

    @org.junit.jupiter.api.Test
    void shouldRemoveFromMiddle() throws BTreeException {
        bplusTree.removeFrom(MAXN/2);
        for (int i = 0; i < MAXN; i++) {
            if ( i < MAXN / 2 )
                Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
            else
                Assertions.assertNull(bplusTree.find(i));
        }
    }

    @org.junit.jupiter.api.Test
    void shouldRemoveFromBeginning() throws BTreeException {
        bplusTree.removeFrom(0);
        for (int i = 0; i < MAXN; i++)
            Assertions.assertNull(bplusTree.find(i));
    }

    @org.junit.jupiter.api.Test
    void shouldRemoveFromBeforeBeginning() throws BTreeException {
        bplusTree.removeFrom(-1);
        for (int i = 0; i < MAXN; i++)
            Assertions.assertNull(bplusTree.find(i));
    }

    @org.junit.jupiter.api.Test
    void shouldRemoveFromAfterBeginning() throws BTreeException {
        bplusTree.removeFrom(MAXN);
        for (int i = 0; i < MAXN; i++)
            Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
    }

    @org.junit.jupiter.api.Test
    void shouldRemoveFromNullLeaf() throws BTreeException {
        bplusTree.removeFrom(0);
        bplusTree.add(0, 0);
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.removeFrom(null));
    }

    @org.junit.jupiter.api.Test
    void shouldFind() throws BTreeException {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.find(null));

        for (int i = 0; i < MAXN; i++) {
            Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
        }
        Assertions.assertNull(bplusTree.find(-1));
        Assertions.assertNull(bplusTree.find(MAXN));
    }

    @org.junit.jupiter.api.Test
    void shouldPeekValue() throws BTreeException {
        for (int i = 0; i < MAXN; i++) {
            Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.peekValue());
            bplusTree.remove(i);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldPeekKey() throws BTreeException {
        for (int i = 0; i < MAXN; i++) {
            Assertions.assertEquals(Integer.valueOf(i), bplusTree.peekKey());
            bplusTree.remove(i);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldPop() throws BTreeException {
        for (int i = 0; i < MAXN; i++) {
            Assertions.assertEquals(Integer.valueOf(2 * i), bplusTree.peekValue());
            Assertions.assertEquals(Integer.valueOf(i), bplusTree.peekKey());
            bplusTree.pop();
            Assertions.assertNotEquals(2 * i, bplusTree.peekValue());
            Assertions.assertNotEquals(i, bplusTree.peekKey());
        }
    }

    @org.junit.jupiter.api.Test
    void shouldDisableCache() throws BTreeException {
        bplusTree = new BplusTree<>(true);
        Assertions.assertFalse(bplusTree.cacheEnabled());
        bplusTree.find(0);
        int initialHit = bplusTree.getHit();
        int initialMiss = bplusTree.getMiss();

        bplusTree.find(0);
        Assertions.assertEquals(0, bplusTree.getHit() - initialHit);
        Assertions.assertEquals(1, bplusTree.getMiss() - initialMiss);
    }

    @org.junit.jupiter.api.Test
    void shouldCacheHitFind() throws BTreeException {
        if (bplusTree.cacheEnabled()) {
            bplusTree.find(0);
            int initialHit = bplusTree.getHit();
            int initialMiss = bplusTree.getMiss();

            bplusTree.find(0);
            Assertions.assertEquals(1, bplusTree.getHit() - initialHit);
            Assertions.assertEquals(0, bplusTree.getMiss() - initialMiss);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldCacheMissFind() throws BTreeException {
        if (bplusTree.cacheEnabled()) {
            bplusTree.find(0);
            int initialHit = bplusTree.getHit();
            int initialMiss = bplusTree.getMiss();

            bplusTree.find(MAXN - 1);
            Assertions.assertEquals(0, bplusTree.getHit() - initialHit);
            Assertions.assertEquals(1, bplusTree.getMiss() - initialMiss);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldCacheHitRemove() throws BTreeException {
        if (bplusTree.cacheEnabled()) {
            bplusTree.remove(1);
            int initialHit = bplusTree.getHit();
            int initialMiss = bplusTree.getMiss();

            bplusTree.remove(0);
            Assertions.assertEquals(1, bplusTree.getHit() - initialHit);
            Assertions.assertEquals(0, bplusTree.getMiss() - initialMiss);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldCacheMissRemove() throws BTreeException {
        if (bplusTree.cacheEnabled()) {
            bplusTree.remove(1);
            int initialHit = bplusTree.getHit();
            int initialMiss = bplusTree.getMiss();

            bplusTree.remove(MAXN - 1);
            Assertions.assertEquals(0, bplusTree.getHit() - initialHit);
            Assertions.assertEquals(1, bplusTree.getMiss() - initialMiss);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldCacheHitAdd() throws BTreeException {
        if (bplusTree.cacheEnabled()) {
            bplusTree.add(-2, -2);
            int initialHit = bplusTree.getHit();
            int initialMiss = bplusTree.getMiss();

            bplusTree.add(-1, -1);
            Assertions.assertEquals(1, bplusTree.getHit() - initialHit);
            Assertions.assertEquals(0, bplusTree.getMiss() - initialMiss);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldCacheMissAdd() throws BTreeException {
        if (bplusTree.cacheEnabled()) {
            bplusTree.add(MAXN, MAXN);
            int initialHit = bplusTree.getHit();
            int initialMiss = bplusTree.getMiss();

            bplusTree.add(-1, -1);
            Assertions.assertEquals(0, bplusTree.getHit() - initialHit);
            Assertions.assertEquals(1, bplusTree.getMiss() - initialMiss);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldCheckSampleDepth() {
        Assertions.assertEquals(3, bplusTree.getSampleDepth());
        bplusTree = new BplusTree<>();
        Assertions.assertEquals(-1, bplusTree.getSampleDepth());
    }

    @Test
    void shouldIterateBackward() {
        Assertions.assertNull(new BplusTree<Integer, Integer>().peekLast());

        BplusTreeLeafNode.BplusTreeIterator iterator = bplusTree.peekLast();

        for (int i = MAXN - 1; i > 0; i--) {
            Assertions.assertEquals(i, iterator.getKey());
            Assertions.assertEquals(2 * i, iterator.getValue());
            Assertions.assertTrue(iterator.hasNext());
            iterator.goToNext();
        }

        Assertions.assertEquals(0, iterator.getKey());
        Assertions.assertEquals(0, iterator.getValue());
        Assertions.assertFalse(iterator.hasNext());
    }

    @Test
    void shouldCheckEquals() throws CloneNotSupportedException, BTreeException {
        Assertions.assertNotEquals(bplusTree, new BplusTree<Integer, Integer>());
        Assertions.assertEquals(bplusTree, bplusTree);
        Assertions.assertNotEquals(bplusTree, null);

        BplusTree<Integer, Integer> secondaryTree = bplusTree.clone();
        Assertions.assertEquals(bplusTree, secondaryTree);

        secondaryTree.add(-1, -1);
        Assertions.assertNull(bplusTree.find(-1));
        Assertions.assertNotEquals(bplusTree, secondaryTree);

        secondaryTree.remove(-1);
        Assertions.assertEquals(bplusTree, secondaryTree);

        bplusTree.add(-1, -1);
        secondaryTree.add(-1, -1);
        Assertions.assertEquals(bplusTree, secondaryTree);
    }

    @Test
    void shouldClone() throws CloneNotSupportedException, BTreeException {
        BplusTree<SimpleComparableCloneable, SimpleCloneable> bplusTree = new BplusTree<>();
        Assertions.assertNotSame(bplusTree, bplusTree.clone());

        BplusTree<SimpleComparableCloneable, SimpleCloneable> mutatedBplusTree = new BplusTree();
        mutatedBplusTree.add(new SimpleComparableCloneable(), new SimpleCloneable());
        Assertions.assertNotEquals(bplusTree, mutatedBplusTree);
        Assertions.assertEquals(mutatedBplusTree, mutatedBplusTree.clone());
    }
}