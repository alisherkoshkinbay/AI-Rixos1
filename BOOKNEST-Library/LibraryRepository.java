package kz.teamworks.library.repository;

import kz.teamworks.library.model.Book;
import kz.teamworks.library.model.Loan;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Singleton pattern: one repository instance owns and persists the library data. */
public final class LibraryRepository {
    private static final LibraryRepository INSTANCE = new LibraryRepository();
    private final List<Book> books = new ArrayList<>();
    private final List<Loan> loans = new ArrayList<>();
    private final File dataFile = new File("library-data.ser");

    private LibraryRepository() { load(); }
    public static LibraryRepository getInstance() { return INSTANCE; }

    public List<Book> books() { return new ArrayList<>(books); }
    public List<Loan> loans() { return new ArrayList<>(loans); }
    public void addBook(Book book) { books.add(book); save(); }
    public boolean removeBook(String id) {
        boolean hasActiveLoan = loans.stream().anyMatch(l -> l.getBookId().equals(id) && !l.isReturned());
        if (hasActiveLoan) throw new IllegalStateException("Кітап қарызда тұрғанда каталогтан өшірілмейді.");
        boolean removed = books.removeIf(b -> b.getId().equals(id));
        if (removed) save();
        return removed;
    }
    public Book findBook(String id) {
        return books.stream().filter(b -> b.getId().equals(id)).findFirst().orElse(null);
    }
    public void addLoan(Loan loan) { loans.add(loan); save(); }
    public void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(dataFile))) {
            output.writeObject(new LibraryData(books, loans));
        } catch (IOException e) {
            throw new IllegalStateException("Деректерді сақтау мүмкін болмады: " + e.getMessage(), e);
        }
    }
    @SuppressWarnings("unchecked")
    private void load() {
        if (dataFile.exists()) {
            try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(dataFile))) {
                LibraryData data = (LibraryData) input.readObject();
                books.addAll(data.books);
                loans.addAll(data.loans);
                return;
            } catch (IOException | ClassNotFoundException | ClassCastException ex) {
                // A damaged or older file should not prevent the application from opening.
                books.clear();
                loans.clear();
            }
        }
        seedDemoData();
        save();
    }

    private void seedDemoData() {
        books.add(new Book("Абай жолы", "Мұхтар Әуезов", "Қазақ әдебиеті", 1942, 5));
        books.add(new Book("Қан мен тер", "Әбдіжәміл Нұрпейісов", "Роман", 1961, 3));
        books.add(new Book("The Alchemist", "Paulo Coelho", "Әлем әдебиеті", 1988, 4));
        books.add(new Book("Clean Code", "Robert C. Martin", "IT / Бағдарламалау", 2008, 2));
        books.add(new Book("Atomic Habits", "James Clear", "Өзін-өзі дамыту", 2018, 4));
        books.add(new Book("Көшпенділер", "Ілияс Есенберлин", "Тарихи роман", 1969, 3));
        books.add(new Book("Алхимик", "Пауло Коэльо", "Әлем әдебиеті", 1988, 2));
        // Demo records make the dashboard useful immediately on first launch.
        Book first = books.get(0);
        first.borrowCopy();
        loans.add(new Loan(first.getId(), first.getTitle(), "Айдана Сәрсен", LocalDate.now().minusDays(3), LocalDate.now().plusDays(11)));
        Book cleanCode = books.get(3);
        cleanCode.borrowCopy();
        loans.add(new Loan(cleanCode.getId(), cleanCode.getTitle(), "Нұрсұлтан Әли", LocalDate.now().minusDays(20), LocalDate.now().minusDays(6)));
        Book atomic = books.get(4);
        atomic.borrowCopy();
        loans.add(new Loan(atomic.getId(), atomic.getTitle(), "Мадина Ермек", LocalDate.now().minusDays(5), LocalDate.now().plusDays(9)));
    }

    private static class LibraryData implements Serializable {
        private static final long serialVersionUID = 1L;
        private final List<Book> books;
        private final List<Loan> loans;
        private LibraryData(List<Book> books, List<Loan> loans) {
            this.books = new ArrayList<>(books);
            this.loans = new ArrayList<>(loans);
        }
    }
}
