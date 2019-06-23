package benchmark;

import bplustree.BTreeException;
import bplustree.BplusTree;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.options.Options;

import java.text.MessageFormat;
import java.util.ArrayList;

@State(Scope.Thread)
public class AddNodeToBtreeBenchmark extends AbstractBenchmark {
    private static final int InitialSize = 35 * 1000, Period = 3500;
    private BplusTree<Integer, Integer> bplusTree;
    private ArrayList<Integer> list;
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
    int getNextRandomIndex() {
        if (listIndex == InitialSize) {
            listIndex = 0;
            periodCounter++;
        }

        return list.get(listIndex++) * Period + periodCounter;
    }

    @Benchmark
    public void addNodeToBtree() throws BTreeException {
        Integer currIndex = getNextIndex();
        bplusTree.add(currIndex, currIndex);
    }

    @Benchmark
    public void addNodeToBtreeInReverse() throws BTreeException {
        Integer currIndex = getPrevIndex();
        bplusTree.add(currIndex, currIndex);
    }

    @Benchmark
    public void addNodeToBtreeRandomly() throws BTreeException {
        Integer currIndex = getNextRandomIndex();
        bplusTree.add(currIndex, currIndex);
    }

    @TearDown
    public void tearDown() {
        System.out.println(MessageFormat.format("test finished with {0} hits and {1} misses", bplusTree.getHit(), bplusTree.getMiss()));
        System.out.println(MessageFormat.format("sample depth is {0}", bplusTree.getSampleDepth()));
    }
}
