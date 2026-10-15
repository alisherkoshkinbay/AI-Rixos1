package kz.teamworks.library.model;

import java.io.Serializable;
import java.util.UUID;

/** Represents a title in the library catalogue. */
public class Book implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String id;
    private String title;
    private String author;
    private String category;
    private int year;
    private int totalCopies;
    private int availableCopies;

    public Book(String title, String author, String category, int year, int totalCopies) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.title = requireText(title, "Book title");
        this.author = requireText(author, "Author");
        this.category = category == null || category.isBlank() ? "Жалпы" : category.trim();
        this.year = year;
        if (totalCopies < 1) throw new IllegalArgumentException("Дана саны кемінде 1 болуы керек.");
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " бос болмауы керек.");
        return value.trim();
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public int getYear() { return year; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }
    public boolean isAvailable() { return availableCopies > 0; }

    public void borrowCopy() {
        if (availableCopies <= 0) throw new IllegalStateException("Бұл кітаптың бос данасы жоқ.");
        availableCopies--;
    }

    public void returnCopy() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    @Override public String toString() { return title + " — " + author; }
}
