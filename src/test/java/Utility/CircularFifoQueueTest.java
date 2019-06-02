package Utility;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

class CircularFifoQueueTest {
    private CircularFifoQueue<Integer> queue;

    @BeforeEach
    void setUp() {
        queue = new CircularFifoQueue<>(4);

        for (int i = 0; i < 4; i++)
            queue.add(i);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void size() {
        for (int i = 4; i > 0; i--) {
            Assertions.assertEquals(i, queue.size());
            queue.popFront();
        }

        Assertions.assertEquals(0, queue.size());
    }

    @Test
    void peekFront() {
        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(Integer.valueOf(i), queue.peekFront());
            queue.popFront();
        }

        Assertions.assertNull(queue.peekFront());
    }

    @Test
    void popFront() {
        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(Integer.valueOf(i), queue.popFront());
        }

        Assertions.assertThrows(NoSuchElementException.class, () -> queue.popFront());
    }

    @Test
    void remove() {
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.remove(-1));
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.remove(4));

        for (int i = 0; i < 3; i++) {
            queue.remove(1);
            Assertions.assertEquals(Integer.valueOf(0), queue.get(0));

            for (int j = 1; j < 3 - i; j++) {
                Assertions.assertEquals(Integer.valueOf(j+i+1), queue.get(j));
            }
        }

        queue.remove(0);
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.remove(0));
    }

    @Test
    void removeFrom() {
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.removeFrom(-1));
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.removeFrom(4));

        queue.removeFrom(2);

        Assertions.assertEquals(Integer.valueOf(0), queue.get(0));
        Assertions.assertEquals(Integer.valueOf(1), queue.get(1));
    }

    @Test
    void insert() {
        Assertions.assertThrows(IllegalStateException.class, () -> queue.insert(1, 1));

        queue.popFront();
        queue.popFront();

        Assertions.assertThrows(NullPointerException.class, () -> queue.insert(null, 1));
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.insert(-1, -1));
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.insert(4, 4));


        for (int i = 1; i > -1; i--) {
            queue.insert(i, 0);

            for (int j = 0; j < queue.size(); j++) {
                Assertions.assertEquals(Integer.valueOf(i + j), queue.get(j));
            }
        }
    }

    @Test
    void get() {
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.get(-1));
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.get(4));

        for (int i = 0; i < 4; i++)
            Assertions.assertEquals(Integer.valueOf(i), queue.get(i));
    }

    @Test
    void set() {
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.get(-1));
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.get(4));

        for (int i = 0; i < 4; i++) {
            queue.set(i, 5);
            Assertions.assertEquals(Integer.valueOf(5), queue.get(i));
        }
    }

    @Test
    void splitEvenSize() {
        CircularFifoQueue<Integer> secondHalf = queue.split();

        Assertions.assertEquals(2, queue.size());
        for (int i = 0; i < 2; i++)
            Assertions.assertEquals(Integer.valueOf(i), queue.get(i));

        Assertions.assertEquals(2, secondHalf.size());
        for (int i = 0; i < 2; i++)
            Assertions.assertEquals(Integer.valueOf(i + 2), secondHalf.get(i));

        Assertions.assertThrows(IllegalStateException.class, () -> queue.split());
    }

    @Test
    void splitOddSize() {
        queue = new CircularFifoQueue<>(5);
        for (int i = 0; i < 5; i++)
            queue.add(i);

        CircularFifoQueue<Integer> secondHalf = queue.split();

        Assertions.assertEquals(2, queue.size());
        for (int i = 0; i < 2; i++)
            Assertions.assertEquals(Integer.valueOf(i), queue.get(i));

        Assertions.assertEquals(3, secondHalf.size());
        for (int i = 0; i < 3; i++)
            Assertions.assertEquals(Integer.valueOf(i + 2), secondHalf.get(i));

        Assertions.assertThrows(IllegalStateException.class, () -> queue.split());
    }
}