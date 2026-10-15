package kz.teamworks.library.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.UUID;

/** A lending record linking a book to a reader. */
public class Loan implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    private final String bookId;
    private final String bookTitle;
    private final String readerName;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;
    private int finePaid;

    public Loan(String bookId, String bookTitle, String readerName, LocalDate issueDate, LocalDate dueDate) {
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.readerName = readerName;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
    }

    public String getId() { return id; }
    public String getBookId() { return bookId; }
    public String getBookTitle() { return bookTitle; }
    public String getReaderName() { return readerName; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public int getFinePaid() { return finePaid; }
    public boolean isReturned() { return returnDate != null; }
    public boolean isOverdue() { return !isReturned() && LocalDate.now().isAfter(dueDate); }

    public int calculateFine(int ratePerDay) {
        LocalDate end = returnDate == null ? LocalDate.now() : returnDate;
        long daysLate = Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(dueDate, end));
        return Math.toIntExact(daysLate * ratePerDay);
    }

    public int close(LocalDate date, int ratePerDay) {
        if (isReturned()) throw new IllegalStateException("Бұл кітап қайтарылып қойған.");
        returnDate = date;
        finePaid = calculateFine(ratePerDay);
        return finePaid;
    }
}
