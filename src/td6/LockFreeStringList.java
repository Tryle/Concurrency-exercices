package td6;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class LockFreeStringList {
    static final class Entry {
        final String element;
        volatile Entry next;

        Entry(String element) {
            this.element = element;
            this.next = null;
        }
    }

    private final AtomicReferenceFieldUpdater<Entry, Entry> nextUpdater;
    private final AtomicReference<Entry> head;
    private Entry tail;

    LockFreeStringList() {
        tail = new Entry(null);
        head = new AtomicReference<>(tail);
        nextUpdater = AtomicReferenceFieldUpdater.newUpdater(Entry.class, Entry.class, "next");
    }

    public void forEach(Consumer<? super String> consumer) {
        for(Entry e = head.get().next; e != null; e = e.next) {
            consumer.accept(e.element);
        }
    }

    public void addLast(String element) {
        Entry entry = new Entry(element);
        for(;;) {
            if(nextUpdater.compareAndSet(tail, null, entry)) {
                tail = entry;
                return;
            }
        }
    }
}