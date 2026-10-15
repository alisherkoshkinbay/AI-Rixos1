package kz.teamworks.library.iterator;

import kz.teamworks.library.model.Book;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/** Iterable catalogue. It exposes a custom iterator instead of leaking storage internals. */
public class BookCatalog implements Iterable<Book> {
    private final List<Book> snapshot;

    public BookCatalog(List<Book> books) {
        this.snapshot = new ArrayList<>(books);
    }

    @Override public Iterator<Book> iterator() {
        return new BookCatalogIterator(snapshot);
    }

    private static class BookCatalogIterator implements Iterator<Book> {
        private final List<Book> items;
        private int cursor = 0;

        private BookCatalogIterator(List<Book> items) { this.items = items; }
        @Override public boolean hasNext() { return cursor < items.size(); }
        @Override public Book next() {
            if (!hasNext()) throw new NoSuchElementException("Каталогтың соңы.");
            return items.get(cursor++);
        }
    }
}
