package kz.qonaq.model;

public enum OrderStatus {
    NEW("Жаңа"), PREPARING("Дайындалуда"), READY("Дайын"), COMPLETED("Аяқталды"), CANCELLED("Бас тартылды");
    private final String label;
    OrderStatus(String label) { this.label=label; }
    public String label() { return label; }
}
