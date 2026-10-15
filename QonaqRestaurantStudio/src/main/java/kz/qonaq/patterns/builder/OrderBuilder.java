package kz.qonaq.patterns.builder;

import kz.qonaq.model.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Builder: validates and assembles an immutable order snapshot. */
public final class OrderBuilder {
    private String id, customer, phone, fulfillment="Алып кету", location="", comment="", promo="";
    private final List<CartLine> items = new ArrayList<>();
    private int discount;
    public OrderBuilder id(String v) { id=v; return this; }
    public OrderBuilder customer(String v) { customer=v==null?"":v.trim(); return this; }
    public OrderBuilder phone(String v) { phone=v==null?"":v.trim(); return this; }
    public OrderBuilder fulfillment(String v) { fulfillment=v; return this; }
    public OrderBuilder location(String v) { location=v==null?"":v.trim(); return this; }
    public OrderBuilder comment(String v) { comment=v==null?"":v.trim(); return this; }
    public OrderBuilder promo(String v) { promo=v==null?"":v; return this; }
    public OrderBuilder addItem(CartLine v) { items.add(v); return this; }
    public OrderBuilder discount(int v) { discount=v; return this; }
    public Order build() {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Тапсырыс ID көрсетілмеген");
        if (customer == null || customer.isBlank()) throw new IllegalArgumentException("Клиенттің атын жазыңыз");
        if (phone == null || phone.replaceAll("\\D", "").length() < 10) throw new IllegalArgumentException("Телефон нөмірін дұрыс енгізіңіз (кемінде 10 сан)");
        if (items.isEmpty()) throw new IllegalArgumentException("Себет бос");
        if ("Жеткізу".equals(fulfillment) && location.isBlank()) throw new IllegalArgumentException("Жеткізу мекенжайын енгізіңіз");
        int subtotal=items.stream().mapToInt(CartLine::total).sum();
        if (discount < 0 || discount > subtotal) throw new IllegalArgumentException("Жеңілдік дұрыс емес");
        return new Order(id,customer,phone,fulfillment,location,comment,promo,LocalDateTime.now(),items,
                         subtotal,discount,OrderStatus.NEW);
    }
}
