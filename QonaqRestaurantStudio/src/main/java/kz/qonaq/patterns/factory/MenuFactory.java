package kz.qonaq.patterns.factory;

import kz.qonaq.model.MenuItem;
import java.util.List;

/** Factory Method: subclasses determine which product family to instantiate. */
public abstract class MenuFactory {
    protected abstract MenuItem create(String id, String name, String description,
                                       int price, String icon, String tag, int artStyle);
    public abstract List<MenuItem> catalog();
}
