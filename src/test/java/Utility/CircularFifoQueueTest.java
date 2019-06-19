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
            queue.pushBack(i);
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
            Assertions.assertEquals(4 - i, queue.size());
            queue.popFront();
        }

        Assertions.assertNull(queue.peekFront());
        Assertions.assertEquals(0, queue.size());
    }

    @Test
    void popFront() {
        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(4 - i, queue.size());
            Assertions.assertEquals(Integer.valueOf(i), queue.popFront());
        }

        Assertions.assertEquals(0, queue.size());
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.popFront());
    }

    @Test
    void remove() {
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.remove(-1));
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.remove(4));

        for (int i = 0; i < 3; i++) {
            Assertions.assertEquals(4 - i, queue.size());
            queue.remove(1);
            Assertions.assertEquals(Integer.valueOf(0), queue.get(0));

            for (int j = 1; j < 3 - i; j++) {
                Assertions.assertEquals(Integer.valueOf(j+i+1), queue.get(j));
            }
        }

        queue.remove(0);
        Assertions.assertEquals(0, queue.size());
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.remove(0));
    }

    @Test
    void removeFrom() {
        queue.removeFrom(2);

        Assertions.assertEquals(2, queue.size());
        Assertions.assertEquals(Integer.valueOf(0), queue.get(0));
        Assertions.assertEquals(Integer.valueOf(1), queue.get(1));
    }

    @Test
    void removeFromBeforeBeginning() {
        queue.removeFrom(-1);
        Assertions.assertTrue(queue.isEmpty());
        Assertions.assertEquals(0, queue.size());
    }

    @Test
    void removeFromAfterEnd() {
        queue.removeFrom(4);
        Assertions.assertEquals(4, queue.size());
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
            Assertions.assertEquals(3 - i, queue.size());
            queue.insert(i, 0);

            for (int j = 0; j < queue.size(); j++) {
                Assertions.assertEquals(Integer.valueOf(i + j), queue.get(j));
            }
        }
        Assertions.assertEquals(4, queue.size());
    }

    @Test
    void get() {
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.get(-1));
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.get(4));

        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(4, queue.size());
            Assertions.assertEquals(Integer.valueOf(i), queue.get(i));
        }
    }

    @Test
    void set() {
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.get(-1));
        Assertions.assertThrows(NoSuchElementException.class, () -> queue.get(4));

        for (int i = 0; i < 4; i++) {
            queue.set(i, 5);
            Assertions.assertEquals(4, queue.size());
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
    void splitEvenSizeCircular() {
        queue.popFront();
        queue.pushBack(4);
        CircularFifoQueue<Integer> secondHalf = queue.split();

        Assertions.assertEquals(2, queue.size());
        for (int i = 0; i < 2; i++)
            Assertions.assertEquals(Integer.valueOf(i + 1), queue.get(i));

        Assertions.assertEquals(2, secondHalf.size());
        for (int i = 0; i < 2; i++)
            Assertions.assertEquals(Integer.valueOf(i + 3), secondHalf.get(i));

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

    @Test
    void splitOddSizeCircular() {
        queue = new CircularFifoQueue<>(5);
        for (int i = 0; i < 5; i++)
            queue.pushBack(i);
        queue.popFront();
        queue.pushBack(5);

        CircularFifoQueue<Integer> secondHalf = queue.split();

        Assertions.assertEquals(2, queue.size());
        for (int i = 0; i < 2; i++)
            Assertions.assertEquals(Integer.valueOf(i + 1), queue.get(i));

        Assertions.assertEquals(3, secondHalf.size());
        for (int i = 0; i < 3; i++)
            Assertions.assertEquals(Integer.valueOf(i + 3), secondHalf.get(i));

        Assertions.assertThrows(IllegalStateException.class, () -> queue.split());
    }

    @Test
    void isEmpty() {
        Assertions.assertFalse(queue.isEmpty());
        Assertions.assertTrue(new CircularFifoQueue().isEmpty());
    }

    @Test
    void isAtFullCapacity() {
        Assertions.assertTrue(queue.isAtFullCapacity());
        Assertions.assertFalse(new CircularFifoQueue().isAtFullCapacity());
    }

    @Test
    void maxSize() {
        Assertions.assertEquals(4, queue.maxSize());
    }

    @Test
    void pushBack() {
        Assertions.assertThrows(IllegalStateException.class, () -> queue.pushBack(5));

        queue.popFront();
        queue.pushBack(4);
        Assertions.assertEquals(4, queue.size());
        for (int i = 0; i < 4; i++)
            Assertions.assertEquals(Integer.valueOf(i + 1), queue.get(i));
    }

    @Test
    void peekFrontForcedFromBegining() {
        for (int i = 0; i < 4; i++) {
            Assertions.assertEquals(Integer.valueOf(i), queue.peekFrontForced());
            queue.popFront();
        }
        Assertions.assertEquals(Integer.valueOf(0), queue.peekFrontForced());
    }

    @Test
    void peekFrontForcedFromEnd() {
        Assertions.assertEquals(Integer.valueOf(0), queue.peekFrontForced());
        queue.removeFrom(0);
        Assertions.assertEquals(Integer.valueOf(0), queue.peekFrontForced());
    }
}