package benchmark;

import bplustree.BTreeException;
import bplustree.BplusTree;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.options.Options;
import java.util.LinkedList;

@State(Scope.Thread)
public class AddNodeToBtreeBenchmark extends AbstractBenchmark {
    private static final int InitialSize = 35 * 1000, ReserveredSize = 3500 * InitialSize;
    BplusTree<Integer, Integer> bplusTree;
    LinkedList<Integer> list;
    int indexIterator;

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
        indexIterator = InitialSize;
        bplusTree = new BplusTree<>();
        list = new LinkedList<>();

        for (int i = 0; i < InitialSize; i++)
            bplusTree.add(i, i);

        for (int i = InitialSize; i < ReserveredSize; i++)
            list.add(i);
        java.util.Collections.shuffle(list);
    }

    int getNextIndex() {
        return indexIterator++;
    }
    int getPrevIndex() {
        return -indexIterator++;
    }
    int getNextRandomIndex() {
        return list.pop();
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
}
