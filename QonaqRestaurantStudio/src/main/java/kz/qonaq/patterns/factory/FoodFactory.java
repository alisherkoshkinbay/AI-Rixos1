package kz.qonaq.patterns.factory;
import kz.qonaq.model.MenuItem;
import java.util.List;

public final class FoodFactory extends MenuFactory {
    @Override protected MenuItem create(String id, String name, String description, int price, String icon, String tag, int artStyle) {
        return new MenuItem(id, name, description, "Тағамдар", price, icon, tag, artStyle);
    }
    @Override public List<MenuItem> catalog() {
        return List.of(
            create("F01", "Signature Burger", "Сиыр еті, чеддер, көкөніс, авторлық соус", 3490, "🍔", "BESTSELLER", 0),
            create("F02", "Qonaq Pizza", "Моцарелла, қызанақ, пепперони, базилик", 4290, "🍕", "TOP CHOICE", 1),
            create("F03", "Creamy Pasta", "Феттучини, кілегейлі соус, пармезан", 3890, "🍝", "CHEF'S PICK", 2),
            create("F04", "Salmon Bowl", "Лосось, күріш, авокадо, балғын көкөніс", 4990, "🥗", "HEALTHY", 3),
            create("F05", "Chicken Wings", "Қытырлақ қанаттар, BBQ соусы", 2990, "🍗", "POPULAR", 4),
            create("F06", "Steak Premium", "Грильдегі сиыр еті, картоп және тұздық", 7990, "🥩", "PREMIUM", 5)
        );
    }
}
