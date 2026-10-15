package kz.teamworks.library.service;

import kz.teamworks.library.iterator.BookCatalog;
import kz.teamworks.library.model.Book;
import kz.teamworks.library.model.Loan;
import kz.teamworks.library.observer.LibraryEventPublisher;
import kz.teamworks.library.repository.LibraryRepository;
import kz.teamworks.library.strategy.BookSortStrategy;
import kz.teamworks.library.strategy.ByTitleStrategy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Facade pattern: a single, simple API for catalogue, loans and fines. */
public class LibraryFacade {
    public static final int FINE_PER_DAY_KZT = 200;
    private final LibraryRepository repository = LibraryRepository.getInstance();
    private final LibraryEventPublisher events;
    private BookSortStrategy sortStrategy = new ByTitleStrategy();

    public LibraryFacade(LibraryEventPublisher events) { this.events = events; }

    public BookCatalog catalog() { return new BookCatalog(repository.books()); }
    public List<Book> getSortedBooks() { return sortStrategy.sort(repository.books()); }
    public void setSortStrategy(BookSortStrategy strategy) {
        if (strategy != null) { sortStrategy = strategy; events.publish("Каталогты сұрыптау тәсілі өзгертілді."); }
    }
    public BookSortStrategy getSortStrategy() { return sortStrategy; }
    public List<Loan> getLoans() { return repository.loans(); }
    public List<Loan> getActiveLoans() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : repository.loans()) if (!loan.isReturned()) result.add(loan);
        return result;
    }
    public List<Loan> getOverdueLoans() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : repository.loans()) if (loan.isOverdue()) result.add(loan);
        return result;
    }
    public void addBook(String title, String author, String category, int year, int copies) {
        repository.addBook(new Book(title, author, category, year, copies));
        events.publish("«" + title + "» каталогқа қосылды.");
    }
    public void removeBook(Book book) {
        if (book == null) return;
        repository.removeBook(book.getId());
        events.publish("«" + book.getTitle() + "» каталогтан өшірілді.");
    }
    public void issueBook(Book book, String readerName, LocalDate dueDate) {
        if (book == null) throw new IllegalArgumentException("Алдымен кітапты таңдаңыз.");
        if (readerName == null || readerName.isBlank()) throw new IllegalArgumentException("Оқырманның атын енгізіңіз.");
        if (dueDate == null || dueDate.isBefore(LocalDate.now())) throw new IllegalArgumentException("Қайтару мерзімі бүгіннен ерте болмауы керек.");
        Book stored = repository.findBook(book.getId());
        if (stored == null) throw new IllegalArgumentException("Кітап каталогтан табылмады.");
        stored.borrowCopy();
        repository.addLoan(new Loan(stored.getId(), stored.getTitle(), readerName.trim(), LocalDate.now(), dueDate));
        repository.save();
        events.publish("«" + stored.getTitle() + "» кітабы " + readerName.trim() + " оқырманына берілді.");
    }
    public int returnBook(Loan loan) {
        if (loan == null || loan.isReturned()) throw new IllegalArgumentException("Белсенді берілімді таңдаңыз.");
        int fine = loan.close(LocalDate.now(), FINE_PER_DAY_KZT);
        Book book = repository.findBook(loan.getBookId());
        if (book != null) book.returnCopy();
        repository.save();
        events.publish("«" + loan.getBookTitle() + "» қайтарылды. Айыппұл: " + fine + " ₸.");
        return fine;
    }
    public int fineFor(Loan loan) { return loan.calculateFine(FINE_PER_DAY_KZT); }
    public int getTotalBooks() {
        int total = 0;
        for (Book book : catalog()) total += book.getTotalCopies(); // Iterator pattern used by enhanced-for.
        return total;
    }
    public int getAvailableCopies() {
        int total = 0;
        for (Book book : catalog()) total += book.getAvailableCopies();
        return total;
    }
    public int getActiveLoanCount() { return getActiveLoans().size(); }
    public int getOverdueCount() { return getOverdueLoans().size(); }
    public int getOutstandingFines() {
        int total = 0;
        for (Loan loan : getOverdueLoans()) total += fineFor(loan);
        return total;
    }
}
