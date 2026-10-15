package kz.qonaq.ui;

import kz.qonaq.model.CartLine;
import kz.qonaq.model.MenuItem;
import kz.qonaq.patterns.facade.RestaurantFacade;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.*;
import java.util.List;

public final class MenuPage extends JPanel {
    private final RestaurantFacade service;
    private final RestaurantFrame frame;
    private final JPanel grid=Design.panel(new GridLayout(0,2,16,16));
    private final JPanel cartArea=Design.panel(new BorderLayout());
    private final JTextField search=Design.field("Іздеу...");
    private final JPanel filterRow=Design.panel(new FlowLayout(FlowLayout.LEFT,9,0));
    private String category="Барлығы";
    public MenuPage(RestaurantFacade service,RestaurantFrame frame){
        super(new BorderLayout(16,0));this.service=service;this.frame=frame;setOpaque(false);
        setBorder(new EmptyBorder(0,31,16,31));
        JPanel left=Design.panel(new BorderLayout(0,15));
        JPanel top=Design.panel();top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));
        JPanel title=Design.panel(new BorderLayout());
        title.add(Design.label("Discover our menu",19,Design.INK,true),BorderLayout.WEST);
        title.add(Design.tag(service.catalog().size()+" ТАҒАМ",Design.PURPLE_SOFT,Design.PURPLE),BorderLayout.EAST);
        top.add(title);top.add(Box.createVerticalStrut(14));
        JPanel searchBar=Design.surface(Design.WHITE,15);searchBar.setLayout(new BorderLayout());
        searchBar.setBorder(new EmptyBorder(0,9,0,5));
        searchBar.add(Design.label("⌕",26,Design.PURPLE,true),BorderLayout.WEST);
        search.setToolTipText("Тағам атауы немесе сипаттамасы бойынша іздеу");
        searchBar.add(search,BorderLayout.CENTER);searchBar.setMaximumSize(new Dimension(Integer.MAX_VALUE,45));
        top.add(searchBar);top.add(Box.createVerticalStrut(15));
        top.add(filterRow);left.add(top,BorderLayout.NORTH);
        grid.setBorder(new EmptyBorder(2,0,15,5));
        left.add(Design.scroll(grid),BorderLayout.CENTER);
        add(left,BorderLayout.CENTER);
        Design.RoundPanel cart=Design.surface(Design.WHITE,20);cart.setLayout(new BorderLayout());
        cart.setPreferredSize(new Dimension(330,200));
        cart.add(cartArea,BorderLayout.CENTER);
        add(cart,BorderLayout.EAST);
        search.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){rebuildCards();}
            public void removeUpdate(DocumentEvent e){rebuildCards();}
            public void changedUpdate(DocumentEvent e){rebuildCards();}
        });
        rebuildFilters();rebuildCards();refreshCart();
    }
    private void rebuildFilters(){
        filterRow.removeAll();
        for (String value:List.of("Барлығы","Тағамдар","Сусындар","Десерттер")){
            boolean selected=value.equals(category);
            Design.Button b=new Design.Button(value,selected?Design.PURPLE:Design.WHITE,
                selected?Design.c("#6746EA"):Design.PURPLE_SOFT,selected?Design.WHITE:Design.MUTED,13);
            b.addActionListener(e->{category=value;rebuildFilters();rebuildCards();});
            filterRow.add(b);
        }
        filterRow.revalidate();filterRow.repaint();
    }
    private void rebuildCards(){
        grid.removeAll();
        String query=search.getText().trim().toLowerCase(Locale.ROOT);
        List<MenuItem> visible=service.catalog().stream().filter(item->
            (category.equals("Барлығы")||item.category().equals(category))&&
                (item.name().toLowerCase(Locale.ROOT).contains(query)||
                 item.description().toLowerCase(Locale.ROOT).contains(query))).toList();
        if(visible.isEmpty()) {
            JPanel p=Design.surface(Design.WHITE,16);p.setLayout(new GridBagLayout());
            p.add(Design.label("Ештеңе табылмады  •  Іздеуді өзгертіңіз",13,Design.MUTED,false));
            grid.add(p);
        }else for(MenuItem item:visible)grid.add(itemCard(item));
        grid.revalidate();grid.repaint();
        SwingUtilities.invokeLater(()->{
            Container p=grid.getParent();if(p instanceof JViewport vp && vp.getViewPosition().y!=0)vp.setViewPosition(new Point(0,0));
        });
    }
    private JComponent itemCard(MenuItem item){
        Design.RoundPanel card=Design.surface(Design.WHITE,19);
        card.setLayout(new BorderLayout(0,8));card.setBorder(new EmptyBorder(10,11,12,11));
        card.setPreferredSize(new Dimension(240,282));
        JPanel art=Design.panel(new BorderLayout());
        art.add(new FoodArt(item.artStyle()),BorderLayout.CENTER);
        JLabel tag=Design.tag(item.tag(),Design.WHITE,Design.PURPLE);
        JPanel badgeRow=Design.panel(new FlowLayout(FlowLayout.LEFT,0,0));badgeRow.setBorder(new EmptyBorder(8,8,0,0));badgeRow.add(tag);
        art.add(badgeRow,BorderLayout.NORTH);card.add(art,BorderLayout.CENTER);
        JPanel details=Design.panel();details.setLayout(new BoxLayout(details,BoxLayout.Y_AXIS));
        details.add(Design.label(item.name(),16,Design.INK,true));details.add(Box.createVerticalStrut(5));
        JLabel d=Design.label(item.description(),11,Design.MUTED,false);
        d.setToolTipText(item.description());details.add(d);details.add(Box.createVerticalStrut(11));
        JPanel bottom=Design.panel(new BorderLayout(7,0));
        bottom.add(Design.label(Design.money(item.price()),17,Design.INK,true),BorderLayout.WEST);
        JPanel buttons=Design.panel(new FlowLayout(FlowLayout.RIGHT,6,0));
        Design.Button detail=Design.mutedButton("⋯");detail.setToolTipText("Тағам туралы толық ақпарат және ескертпе");
        detail.addActionListener(e->new ProductDialog(frame,item,service,()->refreshCart()).setVisible(true));
        Design.Button add=Design.primary("＋ Қосу");
        add.addActionListener(e->{service.addToCart(item.id(),1);refreshCart();frame.flash(item.name()+" себетке қосылды");});
        buttons.add(detail);buttons.add(add);bottom.add(buttons,BorderLayout.EAST);
        details.add(bottom);card.add(details,BorderLayout.SOUTH);return card;
    }
    public void refreshCart(){
        cartArea.removeAll();
        JPanel head=Design.panel(new BorderLayout());head.setBorder(new EmptyBorder(23,20,15,20));
        JPanel heading=Design.panel();heading.setLayout(new BoxLayout(heading,BoxLayout.Y_AXIS));
        heading.add(Design.label("Менің себетім",19,Design.INK,true));heading.add(Box.createVerticalStrut(3));
        heading.add(Design.label(service.cartQuantity()+" өнім таңдалды",12,Design.MUTED,false));
        head.add(heading,BorderLayout.WEST);
        Design.Button clear=Design.mutedButton("Тазалау");clear.setEnabled(!service.cart().isEmpty());
        clear.addActionListener(e->{service.clearCart();refreshCart();});head.add(clear,BorderLayout.EAST);
        cartArea.add(head,BorderLayout.NORTH);
        JPanel list=Design.panel();list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(2,14,0,14));
        if(service.cart().isEmpty()){
            JPanel empty=Design.panel();empty.setLayout(new BoxLayout(empty,BoxLayout.Y_AXIS));
            empty.setBorder(new EmptyBorder(65,12,0,12));
            JLabel icon=Design.label("◈",55,Design.c("#D9D1FF"),false);icon.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel t=Design.label("Себет әзірге бос",14,Design.INK,true);t.setAlignmentX(Component.CENTER_ALIGNMENT);
            JLabel h=Design.label("Тағамдарды мәзірден таңдаңыз",11,Design.MUTED,false);h.setAlignmentX(Component.CENTER_ALIGNMENT);
            empty.add(icon);empty.add(Box.createVerticalStrut(10));empty.add(t);empty.add(Box.createVerticalStrut(6));empty.add(h);
            list.add(empty);
        } else for(CartLine line:service.cart()){
            list.add(cartLine(line));list.add(Box.createVerticalStrut(11));
        }
        cartArea.add(Design.scroll(list),BorderLayout.CENTER);
        JPanel foot=Design.panel();foot.setLayout(new BoxLayout(foot,BoxLayout.Y_AXIS));
        foot.setBorder(new EmptyBorder(12,20,20,20));foot.add(Design.sep());foot.add(Box.createVerticalStrut(14));
        foot.add(Design.label("ПРОМОКОД",10,Design.MUTED,true));foot.add(Box.createVerticalStrut(7));
        JPanel promoRow=Design.panel(new BorderLayout(6,0));
        JTextField promo=Design.field("QONAQ10");promo.setText(service.promo());
        promoRow.add(Design.fieldBox(promo),BorderLayout.CENTER);
        Design.Button apply=Design.secondary("Қолдану");
        apply.addActionListener(e->{try{service.setPromo(promo.getText());frame.flash(service.promo().isEmpty()?"Промокод тазаланды":"Промокод қолданылды");refreshCart();}
            catch(IllegalArgumentException ex){frame.alert(ex.getMessage());}});
        promo.addActionListener(e->apply.doClick());promoRow.add(apply,BorderLayout.EAST);foot.add(promoRow);
        foot.add(Box.createVerticalStrut(14));foot.add(priceRow("Аралық сома",Design.money(service.subtotal()),false));
        foot.add(Box.createVerticalStrut(9));foot.add(priceRow("Жеңілдік", "− "+Design.money(service.discount()),false));
        foot.add(Box.createVerticalStrut(12));foot.add(Design.sep());foot.add(Box.createVerticalStrut(12));
        foot.add(priceRow("Төлеуге",Design.money(service.total()),true));foot.add(Box.createVerticalStrut(14));
        Design.Button checkout=Design.primary("Тапсырысты рәсімдеу  →");
        checkout.setAlignmentX(Component.LEFT_ALIGNMENT);checkout.setMaximumSize(new Dimension(Integer.MAX_VALUE,49));
        checkout.setEnabled(!service.cart().isEmpty());
        checkout.addActionListener(e->{new CheckoutDialog(frame,service,()->frame.navigate("orders")).setVisible(true);});
        foot.add(checkout);cartArea.add(foot,BorderLayout.SOUTH);
        cartArea.revalidate();cartArea.repaint();
    }
    private JComponent cartLine(CartLine line){
        JPanel p=Design.panel(new BorderLayout(10,0));
        MenuItem item=service.product(line.productId());
        FoodArt art=new FoodArt(item==null?0:item.artStyle());art.setPreferredSize(new Dimension(70,70));
        p.add(art,BorderLayout.WEST);
        JPanel center=Design.panel();center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        center.add(Design.label(line.name(),12,Design.INK,true));center.add(Box.createVerticalStrut(5));
        center.add(Design.label(Design.money(line.total()),12,Design.PURPLE,true));center.add(Box.createVerticalStrut(4));
        JPanel qty=Design.panel(new FlowLayout(FlowLayout.LEFT,5,0));
        Design.Button minus=Design.mutedButton("−");minus.setBorder(new EmptyBorder(4,9,4,9));
        minus.addActionListener(e->{service.setQuantity(line.productId(),line.quantity()-1);refreshCart();});
        Design.Button plus=Design.mutedButton("+");plus.setBorder(new EmptyBorder(4,9,4,9));
        plus.addActionListener(e->{try{service.addToCart(line.productId(),1);refreshCart();}catch(IllegalArgumentException ex){frame.alert(ex.getMessage());}});
        qty.add(minus);qty.add(Design.label(""+line.quantity(),12,Design.INK,true));qty.add(plus);
        center.add(qty);p.add(center,BorderLayout.CENTER);return p;
    }
    private JComponent priceRow(String title,String amount,boolean prominent){
        JPanel p=Design.panel(new BorderLayout());p.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        p.add(Design.label(title,prominent?15:12,prominent?Design.INK:Design.MUTED,prominent),BorderLayout.WEST);
        p.add(Design.label(amount,prominent?19:13,prominent?Design.PURPLE:Design.INK,true),BorderLayout.EAST);return p;
    }
}
