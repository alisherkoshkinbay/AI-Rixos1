package kz.teamworks.library.strategy;
import kz.teamworks.library.model.Book;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ByTitleStrategy implements BookSortStrategy {
    public List<Book> sort(List<Book> books) {
        List<Book> result = new ArrayList<>(books);
        result.sort(Comparator.comparing(Book::getTitle, String.CASE_INSENSITIVE_ORDER));
        return result;
    }
    public String label() { return "Атауы бойынша"; }
    @Override public String toString() { return label(); }
}
