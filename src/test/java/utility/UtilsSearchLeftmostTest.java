package utility;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

class UtilsSearchLeftmostTest {

    @Test
    void shouldSearchInvalidRange() {
        CircularFifoQueue<Integer> queue = new CircularFifoQueue<>(new Integer[]{1, 2, 3}, 3);
        Assertions.assertEquals(-1, Utils.searchLeftmostKey(queue, 3, 0));
        Assertions.assertThrows(NoSuchElementException.class, () -> Utils.searchLeftmostKey(queue, 5, 4));
    }

    @Test
    void shouldSearchDistinctKey() {
        CircularFifoQueue<Integer> queue = new CircularFifoQueue<>(new Integer[]{1, 2, 3}, 3);
        int idx = Utils.searchLeftmostKey(queue, 1, 3);
        Assertions.assertEquals(0, idx);
    }

    @Test
    void shouldFailAtSearchDistinctKey() {
        CircularFifoQueue<Integer> queue = new CircularFifoQueue<>(new Integer[]{1, 2, 4}, 3);
        int idx = Utils.searchLeftmostKey(queue, 3, 3);
        Assertions.assertEquals(-3, idx);
    }

    @Test
    void shouldSearchMixedKey() {
        CircularFifoQueue<Integer> queue = new CircularFifoQueue<>(new Integer[]{1, 2, 2, 5}, 4);
        int idx = Utils.searchLeftmostKey(queue, 2, 4);
        Assertions.assertEquals(1, idx);
    }

    @Test
    void shouldFailAtSearchMixedKey() {
        CircularFifoQueue<Integer> queue = new CircularFifoQueue<>(new Integer[]{1, 2, 2, 5}, 4);
        int idx = Utils.searchLeftmostKey(queue, 3, 4);
        Assertions.assertEquals(-4, idx);
    }

    @Test
    void shouldSearchEqualKey() {
        CircularFifoQueue<Integer> queue = new CircularFifoQueue<>(new Integer[]{2, 2, 2}, 3);
        int idx = Utils.searchLeftmostKey(queue, 2, 3);
        Assertions.assertEquals(0, idx);
    }

    @Test
    void shouldFailAtSearchEqualKey() {
        CircularFifoQueue<Integer> queue = new CircularFifoQueue<>(new Integer[]{2, 2, 2}, 3);
        int idx = Utils.searchLeftmostKey(queue, 3, 3);
        Assertions.assertEquals(-4, idx);
    }
}