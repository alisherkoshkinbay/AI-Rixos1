package kz.qonaq.model;

import kz.qonaq.patterns.state.OrderState;
import kz.qonaq.patterns.state.States;
import java.time.LocalDateTime;
import java.util.List;

public final class Order {
    private final String id, customer, phone, fulfillment, location, comment, promo;
    private final LocalDateTime createdAt;
    private final List<CartLine> items;
    private final int subtotal, discount;
    private OrderStatus status;
    private transient OrderState state;

    public Order(String id, String customer, String phone, String fulfillment, String location, String comment,
                 String promo, LocalDateTime createdAt, List<CartLine> items, int subtotal, int discount, OrderStatus status) {
        this.id=id; this.customer=customer; this.phone=phone; this.fulfillment=fulfillment; this.location=location;
        this.comment=comment; this.promo=promo; this.createdAt=createdAt; this.items=List.copyOf(items);
        this.subtotal=subtotal; this.discount=discount; setStatus(status);
    }
    public void setStatus(OrderStatus newStatus) { status = newStatus; state = States.forStatus(newStatus); }
    public void next() { state.advance(this); }
    public void cancel() { state.cancel(this); }
    public String id() { return id; }
    public String customer() { return customer; }
    public String phone() { return phone; }
    public String fulfillment() { return fulfillment; }
    public String location() { return location; }
    public String comment() { return comment; }
    public String promo() { return promo; }
    public LocalDateTime createdAt() { return createdAt; }
    public List<CartLine> items() { return items; }
    public int subtotal() { return subtotal; }
    public int discount() { return discount; }
    public int total() { return subtotal-discount; }
    public OrderStatus status() { return status; }
}
