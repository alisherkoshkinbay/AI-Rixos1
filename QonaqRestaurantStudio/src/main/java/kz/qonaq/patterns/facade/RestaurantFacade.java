package kz.qonaq.patterns.facade;

import kz.qonaq.model.*;
import kz.qonaq.patterns.builder.OrderBuilder;
import kz.qonaq.patterns.factory.*;
import kz.qonaq.patterns.observer.*;
import kz.qonaq.storage.OrderRepository;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** Facade: UI interacts only with one API, not individual subsystems. */
public final class RestaurantFacade {
    private final Map<String,MenuItem> catalog = new LinkedHashMap<>();
    private final Map<String,CartLine> cart = new LinkedHashMap<>();
    private final List<Order> orders = new ArrayList<>();
    private final OrderRepository repository;
    private final OrderEvents events = new OrderEvents();
    private String promo="";
    private int nextNumber=1001;
    public RestaurantFacade(Path storage) {
        repository=new OrderRepository(storage);
        for (MenuFactory factory:List.of(new FoodFactory(),new DrinkFactory(),new DessertFactory()))
            for(MenuItem item:factory.catalog()) catalog.put(item.id(),item);
        orders.addAll(repository.load());
        for (Order o:orders) {
            String digits=o.id().replaceAll("^QN-", "");
            try { nextNumber=Math.max(nextNumber,Integer.parseInt(digits)+1); } catch(NumberFormatException ignored) {}
        }
        if (orders.isEmpty() && !java.nio.file.Files.exists(storage)) seedDemo();
    }
    public OrderEvents events() { return events; }
    public Path storagePath() { return repository.file(); }
    public List<MenuItem> catalog() { return List.copyOf(catalog.values()); }
    public MenuItem product(String id) { return catalog.get(id); }
    public List<Order> orders() { return orders.stream().sorted(Comparator.comparing(Order::createdAt).reversed()).toList(); }
    public List<CartLine> cart() { return List.copyOf(cart.values()); }
    public int cartQuantity() { return cart.values().stream().mapToInt(CartLine::quantity).sum(); }
    public int subtotal() { return cart.values().stream().mapToInt(CartLine::total).sum(); }
    public String promo() { return promo; }
    public int discount() { return Math.min(subtotal(),(int)Math.round(subtotal()*promoRate())); }
    private double promoRate() { return switch (promo) { case "QONAQ10" -> .1; case "WELCOME15" -> .15; default -> 0; }; }
    public int total() { return subtotal()-discount(); }
    public void addToCart(String itemId,int qty) {
        if (qty<=0 || qty>50) throw new IllegalArgumentException("Саны 1–50 аралығында болуы тиіс");
        MenuItem item=catalog.get(itemId);
        if (item==null) throw new IllegalArgumentException("Тағам табылмады");
        CartLine old=cart.get(itemId);
        int updated=(old==null?0:old.quantity())+qty;
        if (updated>50) throw new IllegalArgumentException("Бір тағамнан ең көбі 50 дана");
        cart.put(itemId,new CartLine(itemId,item.name(),item.price(),updated,old==null?"":old.note()));
    }
    public void setQuantity(String id,int quantity) {
        if (quantity<=0) cart.remove(id);
        else if (quantity>50) throw new IllegalArgumentException("Бір тағамнан ең көбі 50 дана");
        else if (cart.containsKey(id)) cart.put(id,cart.get(id).withQuantity(quantity));
    }
    public void setLineNote(String id,String note) {
        CartLine old=cart.get(id);if(old!=null)cart.put(id,new CartLine(id,old.name(),old.unitPrice(),old.quantity(),note==null?"":note.trim()));
    }
    public void clearCart() { cart.clear();promo=""; }
    public void setPromo(String code) {
        String candidate=code==null?"":code.trim().toUpperCase(Locale.ROOT);
        if (!candidate.isEmpty() && !candidate.equals("QONAQ10") && !candidate.equals("WELCOME15"))
            throw new IllegalArgumentException("Промокод табылмады. QONAQ10 немесе WELCOME15 қолданыңыз");
        promo=candidate;
    }
    public Order checkout(String customer,String phone,String fulfillment,String location,String comment) {
        OrderBuilder builder=new OrderBuilder().id("QN-"+nextNumber).customer(customer).phone(phone)
            .fulfillment(fulfillment).location(location).comment(comment).promo(promo).discount(discount());
        for (CartLine line:cart.values()) builder.addItem(line);
        Order order=builder.build();
        orders.add(order);
        try { repository.save(orders); }
        catch (RuntimeException ex) { orders.remove(order); throw ex; }
        nextNumber++;
        clearCart();
        events.publish(order,"Жаңа тапсырыс қабылданды");
        return order;
    }
    public void advance(String id) { change(id,false); }
    public void cancel(String id) { change(id,true); }
    private void change(String id,boolean cancel) {
        Order order=find(id);
        OrderStatus old=order.status();
        if (cancel) order.cancel(); else order.next();
        try { repository.save(orders); }
        catch (RuntimeException ex) { order.setStatus(old); throw ex; }
        events.publish(order,cancel?"Тапсырыс болдырылмады":"Статус жаңартылды: "+order.status().label());
    }
    public Order find(String id) { return orders.stream().filter(o->o.id().equals(id)).findFirst()
        .orElseThrow(()->new IllegalArgumentException("Тапсырыс табылмады")); }
    public long count(OrderStatus status) { return orders.stream().filter(o->o.status()==status).count(); }
    public long activeCount() { return orders.stream().filter(o->o.status()==OrderStatus.NEW ||
        o.status()==OrderStatus.PREPARING || o.status()==OrderStatus.READY).count(); }
    public int revenue() { return orders.stream().filter(o->o.status()==OrderStatus.COMPLETED).mapToInt(Order::total).sum(); }
    public int avgCheck() { return orders.stream().filter(o->o.status()==OrderStatus.COMPLETED).mapToInt(Order::total).sum()
        /(int)Math.max(1,count(OrderStatus.COMPLETED)); }
    public Map<String,Integer> popularProducts() {
        Map<String,Integer> counts=new HashMap<>();
        for (Order o:orders) if (o.status()!=OrderStatus.CANCELLED)
            for (CartLine line:o.items()) counts.merge(line.name(),line.quantity(),Integer::sum);
        return counts.entrySet().stream().sorted(Map.Entry.<String,Integer>comparingByValue().reversed())
            .collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(a,b)->a,LinkedHashMap::new));
    }
    public int daySales(LocalDate day) { return orders.stream()
        .filter(o->o.status()==OrderStatus.COMPLETED && o.createdAt().toLocalDate().equals(day))
        .mapToInt(Order::total).sum(); }
    public void removeDemo() {
        boolean changed=orders.removeIf(o->o.id().startsWith("DEMO-"));
        if (changed) { repository.save(orders); events.publish(null,"Демо тапсырыстар өшірілді"); }
    }
    private void seedDemo() {
        String[][] rows={ {"DEMO-201","Айдана","COMPLETED","-6"}, {"DEMO-202","Әлихан","COMPLETED","-4"},
            {"DEMO-203","Дана","COMPLETED","-3"}, {"DEMO-204","Нұрлан","COMPLETED","-2"},
            {"DEMO-205","Мәдина","READY","0"}, {"DEMO-206","Арман","PREPARING","0"},
            {"DEMO-207","Айгерім","NEW","0"} };
        String[] ids={"F01","F02","F03","F04","F05","F06","D01","S01"};
        for (int i=0;i<rows.length;i++) {
            MenuItem a=catalog.get(ids[i]), b=catalog.get(ids[(i+3)%ids.length]);
            List<CartLine> lines=List.of(new CartLine(a.id(),a.name(),a.price(),1,""),
                new CartLine(b.id(),b.name(),b.price(),i%2+1,""));
            int subtotal=lines.stream().mapToInt(CartLine::total).sum();
            Order o=new Order(rows[i][0],rows[i][1],"+7 777 100 20 30","Алып кету","","Демо дерек","",
                LocalDateTime.now().plusDays(Integer.parseInt(rows[i][3])).minusMinutes((long)i*26),
                lines,subtotal,0,OrderStatus.valueOf(rows[i][2]));
            orders.add(o);
        }
        repository.save(orders);
    }
}
