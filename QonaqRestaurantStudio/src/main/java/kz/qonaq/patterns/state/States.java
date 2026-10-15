package kz.qonaq.patterns.state;
import kz.qonaq.model.*;

public final class States {
    private States() {}
    public static OrderState forStatus(OrderStatus status) {
        return switch (status) {
            case NEW -> new NewState();
            case PREPARING -> new PreparingState();
            case READY -> new ReadyState();
            case COMPLETED, CANCELLED -> new FinalState();
        };
    }
    private static final class NewState implements OrderState {
        public void advance(Order o) { o.setStatus(OrderStatus.PREPARING); }
        public void cancel(Order o) { o.setStatus(OrderStatus.CANCELLED); }
    }
    private static final class PreparingState implements OrderState {
        public void advance(Order o) { o.setStatus(OrderStatus.READY); }
        public void cancel(Order o) { o.setStatus(OrderStatus.CANCELLED); }
    }
    private static final class ReadyState implements OrderState {
        public void advance(Order o) { o.setStatus(OrderStatus.COMPLETED); }
        public void cancel(Order o) { throw new IllegalStateException("Дайын тапсырысты болдырмауға болмайды"); }
    }
    private static final class FinalState implements OrderState {
        public void advance(Order o) { throw new IllegalStateException("Бұл тапсырыс аяқталған"); }
        public void cancel(Order o) { throw new IllegalStateException("Бұл тапсырыс аяқталған"); }
    }
}
