package kz.teamworks.library.strategy;

import kz.teamworks.library.model.Book;
import java.util.List;

/** Strategy interface for choosing a book catalogue ordering algorithm. */
public interface BookSortStrategy {
    List<Book> sort(List<Book> books);
    String label();
}
