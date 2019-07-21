package benchmark;

import bplustree.BTreeException;
import bplustree.BplusTree;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.options.Options;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Thread)
public class AddNodeToBtreeBenchmark extends AbstractBenchmark {
    private static final int InitialSize = 35 * 1000, Period = 3500, MaxBatch = 1000 * 1000;
    private BplusTree<Integer, Integer> bplusTree;
    private ArrayList<Integer> list, randomShuffled;
    private int occCounter[];
    private int indexIterator, periodCounter = 1, listIndex = 0;

    @Override
    public Options setupBenchmarkAndBuildAdditionalOption() {
        return defaultOptions();
    }

    @Override
    public String getClassSimpleName() {
        return AddNodeToBtreeBenchmark.class.getSimpleName();
    }

    @Setup
    public void setup() throws BTreeException {
        System.out.println("Begin");
        long startTime = System.nanoTime();

        indexIterator = InitialSize * Period;
        bplusTree = new BplusTree<>();
        list = new ArrayList<>();
        randomShuffled = new ArrayList<>();
        occCounter = new int[InitialSize];

        for (int i = 0; i < InitialSize; i++) {
            bplusTree.add(i * Period, i * Period);
            list.add(i);
        }
        java.util.Collections.shuffle(list);

        for (int i = 0; i < MaxBatch; i++) randomShuffled.add(i);
        java.util.Collections.shuffle(randomShuffled);

        long endTime = System.nanoTime();
        System.out.println(MessageFormat.format("Initialized @ {0}ns", endTime - startTime));
    }

    int getNextIndex() {
        return indexIterator++;
    }
    int getPrevIndex() {
        return -indexIterator++;
    }
    int getNextRandPermutationIndex() {
        if (listIndex == InitialSize) {
            listIndex = 0;
            periodCounter++;
        }

        return list.get(listIndex++) * Period + periodCounter;
    }
    int getNextRandIndex() {
        int index = ThreadLocalRandom.current().nextInt(0, InitialSize);

        return index * Period + ++occCounter[index];
    }

    void addNodeRandomPermutationBatch(int batchSize) throws BTreeException {
        Integer currIndex = getNextRandPermutationIndex();
        for (int i = 0; i < batchSize; ++i)
            bplusTree.add(currIndex * batchSize + i, currIndex);
    }

    @Benchmark
    public void addNodeInIncrement() throws BTreeException {
        Integer currIndex = getNextIndex();
        bplusTree.add(currIndex, currIndex);
    }

    @Benchmark
    public void addNodeInDecrement() throws BTreeException {
        Integer currIndex = getPrevIndex();
        bplusTree.add(currIndex, currIndex);
    }

    @Benchmark
    public void addNodeRandomPermutation() throws BTreeException {
        addNodeRandomPermutationBatch(1);
    }

    @Benchmark
    public void addNodeRandomPermutationBatch5() throws BTreeException {
        addNodeRandomPermutationBatch(5);
    }

    @Benchmark
    public void addNodeRandomPermutationBatch10() throws BTreeException {
        addNodeRandomPermutationBatch(10);
    }

    @Benchmark
    public void addNodeRandomPermutationBatch20() throws BTreeException {
        addNodeRandomPermutationBatch(20);
    }

    @Benchmark
    public void addNodeRandomPermutationBatch30() throws BTreeException {
        addNodeRandomPermutationBatch(30);
    }

    @Benchmark
    public void addNodeRandomPermutationBatch100() throws BTreeException {
        addNodeRandomPermutationBatch(100);
    }

    @Benchmark
    public void addNodeRandom() throws BTreeException {
        Integer currIndex = getNextRandIndex();
        bplusTree.add(currIndex, currIndex);
    }

    @Benchmark
    public void addBatch1k() throws BTreeException {
        bplusTree.removeFrom(0);

        for (int i = 0; i < 1000; i++)
            bplusTree.add(randomShuffled.get(i), i);
    }

    @Benchmark
    public void addBatch100k() throws BTreeException {
        bplusTree.removeFrom(0);

        for (int i = 0; i < 100 * 1000; i++)
            bplusTree.add(randomShuffled.get(i), i);
    }

    @Benchmark
    public void addBatch1m() throws BTreeException {
        bplusTree.removeFrom(0);

        for (int i = 0; i < 1000 * 1000; i++)
            bplusTree.add(randomShuffled.get(i), i);
    }


    @TearDown(Level.Iteration)
    public void tearDown() {
        System.out.println(MessageFormat.format("test finished with {0} hits and {1} misses", bplusTree.getHit(), bplusTree.getMiss()));
        System.out.println(MessageFormat.format("sample depth is {0}", bplusTree.getSampleDepth()));
    }
}
