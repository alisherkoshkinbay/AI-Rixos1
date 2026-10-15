package kz.qonaq.patterns.factory;
import kz.qonaq.model.MenuItem;
import java.util.List;

public final class DrinkFactory extends MenuFactory {
    @Override protected MenuItem create(String id, String name, String description, int price, String icon, String tag, int artStyle) {
        return new MenuItem(id, name, description, "Сусындар", price, icon, tag, artStyle);
    }
    @Override public List<MenuItem> catalog() {
        return List.of(
            create("D01", "Iced Matcha Latte", "Матча, сүт, мұз және нәзік крем", 1890, "🍵", "TRENDING", 6),
            create("D02", "Berry Lemonade", "Таңқурай, лимон, жалбыз және мұз", 1690, "🍹", "FRESH", 7),
            create("D03", "Caramel Coffee", "Эспрессо, сүт, карамель шәрбаты", 1590, "☕", "CLASSIC", 8)
        );
    }
}
