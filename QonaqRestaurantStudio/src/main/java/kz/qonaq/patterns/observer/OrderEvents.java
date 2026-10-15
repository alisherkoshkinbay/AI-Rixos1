package kz.qonaq.patterns.observer;
import kz.qonaq.model.Order;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/** Observer: UI, activity log, and future integrations subscribe to changes. */
public final class OrderEvents {
    private final List<OrderObserver> listeners = new CopyOnWriteArrayList<>();
    public void subscribe(OrderObserver listener) { listeners.add(listener); }
    public void unsubscribe(OrderObserver listener) { listeners.remove(listener); }
    public void publish(Order order, String message) { for (OrderObserver listener:listeners) listener.onOrderChanged(order,message); }
}
