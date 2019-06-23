package benchmark;

import bplustree.BTreeException;
import bplustree.BplusTree;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.options.Options;

import java.text.MessageFormat;
import java.util.ArrayList;

@State(Scope.Thread)
public class PeekAndPopNodeInBtreeBenchmark extends AbstractBenchmark {
    private static final int InitialSize = 1000 * 1000 * 1000;
    private BplusTree<Integer, Integer> bplusTree;

    @Override
    public Options setupBenchmarkAndBuildAdditionalOption() {
        return defaultOptions();
    }

    @Override
    public String getClassSimpleName() {
        return PeekAndPopNodeInBtreeBenchmark.class.getSimpleName();
    }

    /**
     * InitialSize should be divisible by RandomizedSize
     */
    @Setup
    public void setup() throws BTreeException {
        System.out.println("Begin");
        long startTime = System.nanoTime();

        final int RandomizedSize = 3000 * 1000;
        assert InitialSize % RandomizedSize == 0;

        bplusTree = new BplusTree<>();
        ArrayList<Integer> list = new ArrayList<>();

        for (int i = 0; i < RandomizedSize; i++)
            list.add(i);
        java.util.Collections.shuffle(list);

        for (int i = 0; i <= InitialSize / RandomizedSize; i++) {
            for (int j = 0; j < RandomizedSize; j++) {
                int currIndex = list.get(j) + i * RandomizedSize;
                bplusTree.add(currIndex, currIndex);
            }
        }

        long endTime = System.nanoTime();
        System.out.println(MessageFormat.format("Initialized @ {0}ns", endTime - startTime));
    }

    @Benchmark
    public void popNodeInBtree(Blackhole blackhole) throws BTreeException {
        blackhole.consume(bplusTree.pop());
    }

    @Benchmark
    public void peekAndPopNodeInBtree(Blackhole blackhole) throws BTreeException {
        blackhole.consume(bplusTree.peekKey());
        blackhole.consume(bplusTree.pop());
    }
}
