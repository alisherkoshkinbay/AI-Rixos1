package kz.qonaq.patterns.observer;
import kz.qonaq.model.Order;
@FunctionalInterface public interface OrderObserver { void onOrderChanged(Order order, String description); }
