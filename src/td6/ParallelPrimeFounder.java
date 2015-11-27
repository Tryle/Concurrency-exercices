package td6;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class ParallelPrimeFounder {
    public static long primeInParallelWithStream(long[] array) {
        return Arrays.stream(array).parallel().filter(ParallelPrimeFounder::isPrime).count();
    }

    public static long primeInParallelWithExecutors(long[] array) {
        ExecutorService service = Executors.newFixedThreadPool(5);
        List<Callable<Boolean>> callableList = new ArrayList<Callable<Boolean>>();
        Arrays.stream(array).forEach(e -> callableList.add(() -> {
            return isPrime(e);
        }));
        List<Future<Boolean>> futures = null;
        try {
            futures = service.invokeAll(callableList);
        } catch (InterruptedException e) {
            // ERROR
        }
        service.shutdown();
        assert futures != null;
        return futures.stream().filter(e -> {
            try {
                return e.get();
            } catch (InterruptedException | ExecutionException e1) {
                e1.printStackTrace();
                return false;
            }
        }).count();
    }

    public static boolean isPrime(long n) {
        if(n == 1) return false;
        return IntStream.rangeClosed(2, (int) Math.sqrt(n)).noneMatch(divisor -> n % divisor == 0);
    }
}

