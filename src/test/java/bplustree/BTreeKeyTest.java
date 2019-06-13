package bplustree;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BTreeKeyTest {
    BTreeKey<Integer> key;

    @BeforeEach
    void setUp() {
        key = new BTreeKey<>(1, (long) 1);
    }

    @Test
    void getKey() {
        Assertions.assertEquals(Integer.valueOf(1), key.getKey());
    }

    @Test
    void getPointer() {
        Assertions.assertEquals(Long.valueOf(1), key.getPointer());
    }
}