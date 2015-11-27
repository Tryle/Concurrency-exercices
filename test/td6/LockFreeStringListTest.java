package td6;

import com.sun.org.apache.xerces.internal.xs.StringList;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class LockFreeStringListTest {
    @Test
    public void addLastAndForEachTest() {
        LockFreeStringList list = new LockFreeStringList();
        String[] elems = {"foo", "bar", "baz"};
        for(String str: elems) {
            list.addLast(str);
        }
        final int[] count = {0};
        list.forEach(e -> count[0]++);
        assertEquals(3, count[0]);
        count[0] = 0;
        list.forEach(e -> {
            assertEquals(elems[count[0]++], e);
        });
    }

    @Test
    public void addLastThreadSafeTest() {
        LockFreeStringList list = new LockFreeStringList();
        List<Thread> threads = new LinkedList<>();
        IntStream.range(0, 1000).forEach(e -> threads.add(new Thread(() -> {
            IntStream.range(0, 2).forEach(i -> {
                list.addLast("Thread " + e + " added " + i + ".");
            });
        })));
        threads.forEach(Thread::start);
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException e1) {
            }
        });
        final List<String> testList = new ArrayList<>();
        list.forEach(testList::add);
        assertEquals(2000, testList.size());
        IntStream.range(0, 1000).forEach(e -> {
            IntStream.range(0, 2).forEach(i -> {
                assertTrue(testList.contains("Thread " + e + " added " + i + "."));
            });
        });
    }

    @Test
    public void forEachThreadSafeTest() {
        LockFreeStringList list = new LockFreeStringList();
        String[] elems = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
        for(String str: elems) {
            list.addLast(str);
        }
        final List<List<String>> testList = new ArrayList<List<String>>(5);
        List<Thread> threads = new LinkedList<>();
        IntStream.range(0, 5).forEach(e -> {
            testList.add(new LinkedList<>());
            threads.add(new Thread(() -> {
                list.forEach(elem -> testList.get(e).add(elem));
            }));
        });
        threads.forEach(Thread::start);
        threads.forEach(e -> {
            try {
                e.join();
            } catch (InterruptedException e1) {
            }
        });
        for(List<String> test: testList) {
            assertEquals(10, test.size());
            for(String str: elems) {
                assertTrue(test.contains(str));
            }
        }
    }
}
