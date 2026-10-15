package kz.qonaq.model;

public record CartLine(String productId, String name, int unitPrice, int quantity, String note) {
    public CartLine {
        if (unitPrice < 0 || quantity <= 0) throw new IllegalArgumentException("Invalid cart line");
        if (note == null) note = "";
    }
    public int total() { return unitPrice * quantity; }
    public CartLine withQuantity(int newQuantity) { return new CartLine(productId, name, unitPrice, newQuantity, note); }
}
