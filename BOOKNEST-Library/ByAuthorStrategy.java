package kz.teamworks.library.strategy;
import kz.teamworks.library.model.Book;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ByAuthorStrategy implements BookSortStrategy {
    public List<Book> sort(List<Book> books) {
        List<Book> result = new ArrayList<>(books);
        result.sort(Comparator.comparing(Book::getAuthor, String.CASE_INSENSITIVE_ORDER));
        return result;
    }
    public String label() { return "Автор бойынша"; }
    @Override public String toString() { return label(); }
}
