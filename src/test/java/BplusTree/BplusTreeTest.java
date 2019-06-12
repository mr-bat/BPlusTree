package BplusTree;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BplusTreeTest {
    private static final int MAXN = 30 * 1000;
    private BplusTree<Integer, Integer> bplusTree;

    @BeforeEach
    void setUp() throws BTreeException {
        bplusTree = new BplusTree<Integer, Integer>();
        for (int i = 1; i < MAXN; i++) {
            bplusTree.add(i, 2 * i);
        }
        bplusTree.add(0, 0);
    }

    @Test
    void shouldAdd() throws BTreeException {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.add(null, 0));
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.add(0, 0));
        shouldFind();
    }

    @Test
    void shouldRemove() throws BTreeException {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.remove(null));
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.remove(-1));

        for (int i = 0; i < MAXN; i += 2) {
            Assert.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
            bplusTree.remove(i);
            Assert.assertNull(bplusTree.find(i));
        }
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.remove(2));

    }

    @Test
    void shouldRemoveFrom() throws BTreeException {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.removeFrom(null));

        bplusTree.removeFrom(MAXN/2);
        for (int i = 0; i < MAXN; i++) {
            if ( i < MAXN / 2 )
                Assert.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
            else
                Assert.assertNull(bplusTree.find(i));
        }
    }

    @Test
    void shouldFind() throws BTreeException {
        Assertions.assertThrows(BTreeException.class, () -> bplusTree.find(null));

        for (int i = 0; i < MAXN; i++) {
            System.out.println(i);
            Assert.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
        }
        Assert.assertNull(bplusTree.find(-1));
        Assert.assertNull(bplusTree.find(MAXN));
    }

    @Test
    void shouldPeekValue() throws BTreeException {
        for (int i = 0; i < MAXN; i++) {
            Assert.assertEquals(Integer.valueOf(2 * i), bplusTree.peekValue());
            bplusTree.remove(i);
        }
    }

    @Test
    void shouldPeekKey() throws BTreeException {
        for (int i = 0; i < MAXN; i++) {
            Assert.assertEquals(Integer.valueOf(i), bplusTree.peekKey());
            bplusTree.remove(i);
        }
    }

    @Test
    void shouldPop() throws BTreeException {
        for (int i = 0; i < MAXN; i++) {
            Assert.assertEquals(Integer.valueOf(2 * i), bplusTree.peekValue());
            Assert.assertEquals(Integer.valueOf(i), bplusTree.peekKey());
            bplusTree.pop();
            Assert.assertNotEquals(Integer.valueOf(2 * i), bplusTree.peekValue());
            Assert.assertNotEquals(Integer.valueOf(i), bplusTree.peekKey());
        }
    }

    @AfterEach
    void tearDown() {
    }
}