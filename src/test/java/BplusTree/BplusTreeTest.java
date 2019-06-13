package BplusTree;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;

import static java.lang.Math.min;

class BplusTreeTest {
    private static final int MAXN = 30 * 1000;
    private BplusTree<Integer, Integer> bplusTree;


    @org.junit.jupiter.api.BeforeEach
    void setUp() throws BTreeException {
        bplusTree = new BplusTree<Integer, Integer>();
        for (int i = 1; i < MAXN; i++) {
            bplusTree.add(i, 2 * i);
        }
        bplusTree.add(0, 0);
    }

    @org.junit.jupiter.api.Test
    void shouldAdd() throws BTreeException {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.add(null, 0));
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.add(0, 0));
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

    /**
     * @implNote forward > backward should hold
     */
    @org.junit.jupiter.api.Test
    void shouldAddRemoveAlternatively() throws BTreeException {
        final int forward = 10, backward = 2;

        for (int i = 0; i < MAXN; i += forward - backward) {
            for (int j = i; j < min(i + forward, MAXN); j++) {
                Assertions.assertEquals(Integer.valueOf(2 * j), bplusTree.find(j));
                bplusTree.remove(j);
                Assertions.assertNull(bplusTree.find(j));
            }
            for (int j = 0, k = i + forward - 1; j < backward; j++, k--) {
                bplusTree.add(k, 2 * k);
                Assertions.assertEquals(Integer.valueOf(2 * k), bplusTree.find(k));
            }
        }
    }

    @org.junit.jupiter.api.Test
    void shouldRemoveFromMiddle() throws BTreeException {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.removeFrom(null));

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

    @AfterEach
    void tearDown() {
    }
}