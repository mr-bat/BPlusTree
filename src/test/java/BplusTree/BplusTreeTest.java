package BplusTree;

import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;

class BplusTreeTest {
    private static final int MAXN = 20 * 1000;
    private BplusTree<Integer, Integer> bplusTree;


    @org.junit.jupiter.api.BeforeEach
    void setUp() throws BTreeException {
        bplusTree = new BplusTree<Integer, Integer>();
        for (int i = 0; i < MAXN; i++) {
            bplusTree.add(i, 2 * i);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldAdd() throws BTreeException {
        shouldFind();
    }

    @org.junit.jupiter.api.Test
    void shouldRemove() throws BTreeException {
        for (int i = 0; i < MAXN; i += 2) {
            Assert.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
            bplusTree.remove(i);
            Assert.assertNull(bplusTree.find(i));
        }
    }

    @org.junit.jupiter.api.Test
    void shouldRemoveFrom() throws BTreeException {
        bplusTree.removeFrom(MAXN/2);
        for (int i = 0; i < MAXN; i++) {
            if ( i < MAXN / 2 )
                Assert.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
            else
                Assert.assertNull(bplusTree.find(i));
        }
    }

    @org.junit.jupiter.api.Test
    void shouldFind() throws BTreeException {
        for (int i = 0; i < MAXN; i++) {
            Assert.assertEquals(Integer.valueOf(2 * i), bplusTree.find(i));
        }
        Assert.assertNull(bplusTree.find(-1));
        Assert.assertNull(bplusTree.find(MAXN));
    }

    @org.junit.jupiter.api.Test
    void shouldPeekValue() throws BTreeException {
        for (int i = 0; i < MAXN; i++) {
            Assert.assertEquals(Integer.valueOf(2 * i), bplusTree.peekValue());
            bplusTree.remove(i);
        }
    }

    @org.junit.jupiter.api.Test
    void shouldPeekKey() throws BTreeException {
        for (int i = 0; i < MAXN; i++) {
            Assert.assertEquals(Integer.valueOf(i), bplusTree.peekKey());
            bplusTree.remove(i);
        }
    }

    @org.junit.jupiter.api.Test
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