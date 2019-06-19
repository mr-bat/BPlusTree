package Utility;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.common.annotations.Beta;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static java.lang.StrictMath.max;
import static java.lang.StrictMath.min;

/**
 * CircularFifoQueue is a first-in first-out queue with a fixed size
 * This queue prevents null objects from being added.
 *
 * @param <E> the type of elements in this collection
 * @since 4.0
 */
public class CircularFifoQueue<E> extends AbstractCollection<E>
        implements Serializable {

    /** Serialization version. */
    private static final long serialVersionUID = -8423413834657610406L;

    /** Underlying storage array. */
    private transient E[] elements;

    /** Array index of first (oldest) queue element. */
    private transient int start = 0;

    /**
     * Index mod maxElements of the array position following the last queue
     * element.  Queue elements start at elements[start] and "wrap around"
     * elements[maxElements-1], ending at elements[decrement(end)].
     * For example, elements = {c,a,b}, start=1, end=1 corresponds to
     * the queue [a,b,c].
     */
    private transient int end = 0;
    private transient int size = -1;

    /** Flag to indicate if the queue is currently full. */
    private transient boolean full = false;

    /** Capacity of the queue. */
    private final int maxElements;

    /**
     * Constructor that creates a queue with the default size of 32.
     */
    public CircularFifoQueue() {
        this(32);
    }

    /**
     * Constructor that creates a queue with the specified size.
     *
     * @param size  the size of the queue (cannot be changed)
     * @throws IllegalArgumentException  if the size is &lt; 1
     */
    @SuppressWarnings("unchecked")
    public CircularFifoQueue(final int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("The size must be greater than 0");
        }
        elements = (E[]) new Object[size];
        maxElements = elements.length;
    }

    /**
     * Constructor that creates a queue from the specified collection.
     * The collection size also sets the queue size.
     *
     * @param coll  the collection to copy into the queue, may not be null
     * @throws NullPointerException if the collection is null
     */
    public CircularFifoQueue(final Collection<? extends E> coll) {
        this(coll.size());

        for (E element : coll)
            pushBack(element);
    }

    public CircularFifoQueue(final E[] elements, final int size) {
        if (elements.length < size)
            throw new IllegalStateException("initial array is smaller than the specified end index");

        this.elements = elements;
        this.end = size;
        this.maxElements = elements.length;
        this.full = end == maxElements;
    }

    //-----------------------------------------------------------------------
    /**
     * Write the queue out using a custom routine.
     *
     * @param out  the output stream
     * @throws IOException if an I/O error occurs while writing to the output stream
     */
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeInt(size());
        for (final E e : this) {
            out.writeObject(e);
        }
    }

    /**
     * Read the queue in using a custom routine.
     *
     * @param in  the input stream
     * @throws IOException if an I/O error occurs while writing to the output stream
     * @throws ClassNotFoundException if the class of a serialized object can not be found
     */
    @SuppressWarnings("unchecked")
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        elements = (E[]) new Object[maxElements];
        final int size = in.readInt();
        for (int i = 0; i < size; i++) {
            elements[i] = (E) in.readObject();
        }
        start = 0;
        full = size == maxElements;
        if (full) {
            end = 0;
        } else {
            end = size;
        }
    }

    //-----------------------------------------------------------------------
    /**
     * Returns the number of elements stored in the queue.
     *
     * @return this queue's size
     */
    @Override 
    public int size() {
        if (size != -1)
            return size;

        int size = 0;

        if (end < start) {
            size = maxElements - start + end;
        } else if (end == start) {
            size = full ? maxElements : 0;
        } else {
            size = end - start;
        }

        this.size = size;
        return size;
    }

    /**
     * Returns true if this queue is empty; false otherwise.
     *
     * @return true if this queue is empty
     */
    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns {@code true} if the capacity limit of this queue has been reached,
     * i.e. the number of elements stored in the queue equals its maximum size.
     *
     * @return {@code true} if the capacity limit has been reached, {@code false} otherwise
     * @since 4.1
     */
    public boolean isAtFullCapacity() {
        return size() == maxElements;
    }

    /**
     * Gets the maximum size of the collection (the bound).
     *
     * @return the maximum number of elements the collection can hold
     */
    public int maxSize() {
        return maxElements;
    }

    /**
     * Clears this queue.
     */
    @Override
    public void clear() {
        full = false;
        start = 0;
        end = 0;
        size = 0;
//        Arrays.fill(elements, null);
    }

    /**
     * Returns the element at the specified position in this queue.
     *
     * @param index the position of the element in the queue
     * @return the element at position {@code index}
     * @throws NoSuchElementException if the requested position is outside the range [0, size)
     */
    public E get(final int index) {
        final int sz = size();
        if (index < 0 || index >= sz) {
            throw new NoSuchElementException(
                    String.format("The specified index (%1$d) is outside the available range [0, %2$d)",
                            Integer.valueOf(index), Integer.valueOf(sz)));
        }

        final int idx = (start + index) % maxElements;
        return elements[idx];
    }

    /**
     * Sets the element at the specified position in this queue.
     *
     * @param index the position of the element in the queue
     * @throws NoSuchElementException if the requested position is outside the range [0, size)
     */
    public void set(final int index, final E element) {
        final int sz = size();
        if (index < 0 || index >= sz) {
            throw new NoSuchElementException(
                    String.format("The specified index (%1$d) is outside the available range [0, %2$d)",
                            Integer.valueOf(index), Integer.valueOf(sz)));
        }

        final int idx = (start + index) % maxElements;
        elements[idx] = element;
    }

    /**
     * Adds the given element to back of this queue. If the queue is full an exception is thrown.
     *
     * @param element  the element to add
     * @return true, always
     * @throws NullPointerException  if the given element is null
     * @throws IllegalStateException if the queue is full
     */
    public boolean pushBack(final E element) {
        if (null == element) {
            throw new NullPointerException("Attempted to add null object to queue");
        }

        if (isAtFullCapacity()) {
            throw new IllegalStateException("Can not add when queue is full");
        }

        elements[end++] = element;

        if (end >= maxElements) {
            end = 0;
        }

        if (end == start) {
            full = true;
        }

        ++size;
        return true;
    }

    public boolean pushFront(final E element) {
        if (null == element) {
            throw new NullPointerException("Attempted to add null object to queue");
        }

        if (isAtFullCapacity()) {
            throw new IllegalStateException("Can not add when queue is full");
        }

        if (--start < 0) {
            start = maxElements - 1;
        }

        elements[start] = element;

        if (end == start) {
            full = true;
        }

        ++size;
        return true;
    }

    public E peekBack() {
        if (isEmpty()) {
            return null;
        }
        return elements[decrement(end)];
    }

    public E peekFront() {
        if (isEmpty()) {
            return null;
        }
        return elements[start];
    }

    /**
     * If queue is empty returns the element at the current start else peeks front 
     */
    @Beta
    public E peekFrontForced() {
        return elements[start];
    }

    public E popFront() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }

        final E element = elements[start];
//        elements[start] = null;
        start++;

        if (start >= maxElements) {
            start = 0;
        }

        full = false;

        --size;
        return element;
    }

    public E popBack() {
        if (isEmpty()) {
            throw new NoSuchElementException("queue is empty");
        }

        if (--end < 0)
            end = maxElements - 1;

        final E element = elements[end];
//        elements[end] = null;

        full = false;

        --size;
        return element;
    }

    public void remove(int index) {
        final int sz = size();
        if (index < 0 || index >= sz) {
            throw new NoSuchElementException(
                    String.format("The specified index (%1$d) is outside the available range [0, %2$d)",
                            Integer.valueOf(index), Integer.valueOf(sz)));
        }

        index = (start + index) % maxElements;
        --end;
        if (end < 0)
            end = maxElements - 1;

        E next = elements[increment(index)];

        while (index != end) {
            elements[index] = next;

            if (++index == maxElements)
                index = 0;

            next = elements[increment(index)];
        }
        if (start == end)
            full = false;
        --size;
    }

    public void removeFrom(int index) {
        final int sz = size();
        if (index < 0)
            index = 0;
        else if (index >= sz)
            return;

        end = (start + index) % maxElements;
        if (start == end)
            full = false;
        size = -1;
    }

    public void insert(E element, int index) {
        final int sz = size();
        if (index < 0 || index > sz) {
            throw new NoSuchElementException(
                    String.format("The specified index (%1$d) is outside the available range [0, %2$d)",
                            Integer.valueOf(index), Integer.valueOf(sz)));
        }

        if (null == element) {
            throw new NullPointerException("Attempted to add null object to queue");
        }

        if (isAtFullCapacity()) {
            throw new IllegalStateException("Object is currently full");
        }

        index = (start + index) % maxElements;

        while (index != end) {
            E curr;

            curr = elements[index];
            elements[index] = element;
            element = curr;

            if (++index == maxElements)
                index = 0;
        }

        elements[end++] = element;

        if (end == start) {
            full = true;
        }
        ++size;
    }

    public CircularFifoQueue<E> split() {
        if (!isAtFullCapacity())
            throw new IllegalStateException("CircularFifoQueue should be full");

        E[] restElements = (E[]) Array.newInstance(elements.getClass().getComponentType(), maxElements);
        System.arraycopy(elements, start + (maxElements / 2), restElements, 0, max((maxElements + 1) / 2 - start, 0));
        System.arraycopy(elements, 0, restElements, max((maxElements + 1) / 2 - start, 0), min((maxElements + 1) / 2, start));
        removeFrom(maxElements / 2);

        size = -1;
        return new CircularFifoQueue(restElements, (maxElements+1)/2);
    }

    //-----------------------------------------------------------------------
    /**
     * Increments the internal index.
     *
     * @param index  the index to increment
     * @return the updated index
     */
    private int increment(int index) {
        index++;
        if (index >= maxElements) {
            index = 0;
        }
        return index;
    }

    /**
     * Decrements the internal index.
     *
     * @param index  the index to decrement
     * @return the updated index
     */
    private int decrement(int index) {
        index--;
        if (index < 0) {
            index = maxElements - 1;
        }
        return index;
    }

    /**
     * Returns an iterator over this queue's elements.
     *
     * @return an iterator over this queue's elements
     */
    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int index = start;
            private int lastReturnedIndex = -1;
            private boolean isFirst = full;

            @Override
            public boolean hasNext() {
                return isFirst || index != end;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                isFirst = false;
                lastReturnedIndex = index;
                index = increment(index);
                return elements[lastReturnedIndex];
            }

            @Override
            public void remove() {
                if (lastReturnedIndex == -1) {
                    throw new IllegalStateException();
                }

                // First element can be removed quickly
                if (lastReturnedIndex == start) {
                    CircularFifoQueue.this.popFront();
                    lastReturnedIndex = -1;
                    --size;
                    return;
                }

                int pos = lastReturnedIndex + 1;
                if (start < lastReturnedIndex && pos < end) {
                    // shift in one part
                    System.arraycopy(elements, pos, elements, lastReturnedIndex, end - pos);
                } else {
                    // Other elements require us to shift the subsequent elements
                    while (pos != end) {
                        if (pos >= maxElements) {
                            elements[pos - 1] = elements[0];
                            pos = 0;
                        } else {
                            elements[decrement(pos)] = elements[pos];
                            pos = increment(pos);
                        }
                    }
                }

                lastReturnedIndex = -1;
                end = decrement(end);
                elements[end] = null;
                full = false;
                index = decrement(index);
                --size;
            }

        };
    }

}
