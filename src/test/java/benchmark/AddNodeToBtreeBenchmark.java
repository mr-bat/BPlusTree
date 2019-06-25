package benchmark;

import bplustree.BTreeException;
import bplustree.BplusTree;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.options.Options;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

@State(Scope.Thread)
public class AddNodeToBtreeBenchmark extends AbstractBenchmark {
    private static final int InitialSize = 35 * 1000, Period = 3500;
    private BplusTree<Integer, Integer> bplusTree;
    private ArrayList<Integer> list;
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
        occCounter = new int[InitialSize];

        for (int i = 0; i < InitialSize; i++) {
            bplusTree.add(i * Period, i * Period);
            list.add(i);
        }
        java.util.Collections.shuffle(list);

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
        Integer currIndex = getNextRandPermutationIndex();
        bplusTree.add(currIndex, currIndex);
    }

    @Benchmark
    public void addNodeRandom() throws BTreeException {
        Integer currIndex = getNextRandIndex();
        bplusTree.add(currIndex, currIndex);
    }
}
