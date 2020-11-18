package com.gocypher.cybench.utils;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;


class NodesTest {

    @Setup
    public void setup() {
    }

    @TearDown
    public void tearDown() {
    }

    @BenchmarkMode(Mode.Throughput)
    @Warmup(iterations = 3)
    @Measurement(iterations = 10, time = 5, timeUnit = TimeUnit.SECONDS)
    @Threads(8)
    @Fork(2)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Benchmark
    public void findNode() {
    }

    @Benchmark
    public void addTest() {
    }

    @Benchmark
    public void addClass() {
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(NodesTest.class.getSimpleName())
                .addProfiler("gc")
                .output("temp.log")
                .build();
        new Runner(opt).run();
    }
}
