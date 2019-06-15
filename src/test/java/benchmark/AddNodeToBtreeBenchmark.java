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
    private static final int InitialSize = 30 * 1000;
    BplusTree<Integer, Integer> bTree;
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
        bTree = new BplusTree<Integer, Integer>();
        list = new LinkedList<>();

        for (int i = 0; i < InitialSize; i++)
            bTree.add(i, i);

        for (int i = InitialSize; i < 300 * InitialSize; i++)
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

//    @Benchmark
    public void AddNodeToBtree() throws BTreeException {
        Integer currIndex = getNextIndex();
        bTree.add(currIndex, currIndex);
    }

//    @Benchmark
    public void AddNodeToBtreeInReverse() throws BTreeException {
        Integer currIndex = getPrevIndex();
        bTree.add(currIndex, currIndex);
    }

    @Benchmark
    public void AddNodeToBtreeRandomly() throws BTreeException {
        Integer currIndex = getNextRandomIndex();
        bTree.add(currIndex, currIndex);
    }
}
