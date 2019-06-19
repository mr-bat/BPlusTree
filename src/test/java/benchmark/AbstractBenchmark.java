package benchmark;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

public abstract class AbstractBenchmark {

    @Test
    public Options defaultOptions(){

        Options opt = new OptionsBuilder()
                .include(getClassSimpleName())
                .mode (Mode.SampleTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .warmupTime(TimeValue.seconds(2))
                .warmupIterations(2)
                .measurementTime(TimeValue.seconds(4))
                .measurementIterations(2)
                .threads(1)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
//                       .addProfiler(StackProfiler.class, "lines=15;detailLine=true")
//                       .result("jhm.json").resultFormat(ResultFormatType.JSON)
                .build();

        return opt;
    }

    public abstract Options setupBenchmarkAndBuildAdditionalOption();

    public abstract String getClassSimpleName();

    @Test
    @Tag("benchmark")
    public void runBenchmark()throws RunnerException {
        Options runningOptions = setupBenchmarkAndBuildAdditionalOption();
        new Runner(runningOptions).run();
    }
}
