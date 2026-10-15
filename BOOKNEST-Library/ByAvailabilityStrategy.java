package kz.teamworks.library.strategy;
import kz.teamworks.library.model.Book;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ByAvailabilityStrategy implements BookSortStrategy {
    public List<Book> sort(List<Book> books) {
        List<Book> result = new ArrayList<>(books);
        result.sort(Comparator.comparingInt(Book::getAvailableCopies).reversed()
                .thenComparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
        return result;
    }
    public String label() { return "Бос данасы бойынша"; }
    @Override public String toString() { return label(); }
}
