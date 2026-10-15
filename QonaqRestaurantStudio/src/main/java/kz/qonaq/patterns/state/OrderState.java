package kz.qonaq.patterns.state;
import kz.qonaq.model.Order;

/** State: each state controls legal actions. */
public interface OrderState {
    void advance(Order order);
    void cancel(Order order);
}
