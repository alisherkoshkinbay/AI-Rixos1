package kz.qonaq.patterns.factory;
import kz.qonaq.model.MenuItem;
import java.util.List;

public final class DessertFactory extends MenuFactory {
    @Override protected MenuItem create(String id, String name, String description, int price, String icon, String tag, int artStyle) {
        return new MenuItem(id, name, description, "Десерттер", price, icon, tag, artStyle);
    }
    @Override public List<MenuItem> catalog() {
        return List.of(
            create("S01", "Berry Cheesecake", "Кремді чизкейк, жидек соусы", 2290, "🍰", "SWEET", 9),
            create("S02", "Chocolate Fondant", "Ыстық шоколад, балмұздақ", 2490, "🍫", "NEW", 10),
            create("S03", "Strawberry Waffle", "Бельгиялық вафли, құлпынай, крем", 2090, "🧇", "FAVORITE", 11)
        );
    }
}
