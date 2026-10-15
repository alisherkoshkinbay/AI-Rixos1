package kz.qonaq.model;

public record MenuItem(String id, String name, String description, String category,
                       int price, String icon, String tag, int artStyle) {
    public MenuItem {
        if (id == null || id.isBlank() || price < 0) throw new IllegalArgumentException("Invalid menu item");
    }
}
