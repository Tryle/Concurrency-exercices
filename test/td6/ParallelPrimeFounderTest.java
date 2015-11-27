package td6;

import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static org.junit.Assert.*;

public class ParallelPrimeFounderTest {
    @Test
    public void arrayTest() {
        long[] arr = LongStream.range(1, 100).toArray();
        assertEquals(25, ParallelPrimeFounder.primeInParallelWithExecutors(arr));
        assertEquals(25, ParallelPrimeFounder.primeInParallelWithStream(arr));
    }

    @Test
    public void bigArrayTest() {
        long[] arr = LongStream.range(1, 1_000_000).toArray();
        assertEquals(ParallelPrimeFounder.primeInParallelWithStream(arr), ParallelPrimeFounder.primeInParallelWithExecutors(arr));
    }
}
