package kz.qonaq.ui;

import kz.qonaq.model.*;
import kz.qonaq.patterns.facade.RestaurantFacade;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public final class DashboardPage extends JPanel {
    public DashboardPage(RestaurantFacade service,RestaurantFrame frame){
        super(new BorderLayout());setOpaque(false);
        JPanel stack=Design.vertical(0);stack.setBorder(new EmptyBorder(2,31,28,32));
        stack.add(hero(frame));stack.add(Box.createVerticalStrut(20));
        JPanel stats=Design.panel(new GridLayout(1,4,15,0));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE,135));
        stats.add(stat("ТҮСІМ",Design.money(service.revenue()),"Аяқталған тапсырыстар",Design.PURPLE,"↗"));
        stats.add(stat("БАРЛЫҚ ТАПСЫРЫС",""+service.orders().size(),"Жалпы база",Design.c("#3198C1"),"▣"));
        stats.add(stat("БЕЛСЕНДІ",""+service.activeCount(),"Асүйде және кезекте",Design.ORANGE,"◉"));
        stats.add(stat("ОРТАША ЧЕК",Design.money(service.avgCheck()),"Аяқталғандар бойынша",Design.GREEN,"◈"));
        stack.add(stats);stack.add(Box.createVerticalStrut(24));
        JPanel heading=Design.panel(new BorderLayout());
        JPanel title=Design.panel();title.setLayout(new BoxLayout(title,BoxLayout.Y_AXIS));
        title.add(Design.label("Соңғы тапсырыстар",18,Design.INK,true));title.add(Box.createVerticalStrut(5));
        title.add(Design.label("Мейрамханаңыздағы соңғы әрекеттер",12,Design.MUTED,false));
        heading.add(title,BorderLayout.WEST);
        Design.Button all=Design.secondary("Барлық тапсырыс  →");all.addActionListener(e->frame.navigate("orders"));
        heading.add(all,BorderLayout.EAST);stack.add(heading);stack.add(Box.createVerticalStrut(14));
        Design.RoundPanel list=Design.surface(Design.WHITE,18);
        list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));
        List<Order> orders=service.orders();
        if(orders.isEmpty()) list.add(Design.pad(Design.label("Әзірше тапсырыс жоқ. Мәзірден бірінші тапсырысты құрыңыз.",13),24,24,24,24));
        else for(int i=0;i<Math.min(6,orders.size());i++){
            if(i>0)list.add(Design.sep());list.add(orderRow(orders.get(i),frame));
        }
        stack.add(list);stack.add(Box.createVerticalStrut(15));
        stack.add(Design.label("* DEMO нөмірлі тапсырыстар — интерфейсті көрсету үшін жасалған тест деректер.",11,Design.MUTED,false));
        add(Design.scroll(stack),BorderLayout.CENTER);
    }
    private JComponent hero(RestaurantFrame frame) {
        JComponent hero=new JComponent(){
            @Override public Dimension getPreferredSize(){return new Dimension(650,180);}
            @Override public Dimension getMaximumSize(){return new Dimension(Integer.MAX_VALUE,180);}
            @Override protected void paintComponent(Graphics graphics){
                Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g.setPaint(new GradientPaint(0,0,Design.c("#7256F5"),getWidth(),getHeight(),Design.c("#9C73FB")));
                g.fillRoundRect(0,0,getWidth(),getHeight(),22,22);
                g.setColor(new Color(255,255,255,24));g.fillOval(getWidth()-280,-120,300,300);
                g.fillOval(getWidth()-190,65,170,170);g.fillOval(getWidth()-350,85,105,105);
                g.dispose();
            }
        };
        hero.setLayout(new BorderLayout());
        JPanel text=Design.panel();text.setLayout(new BoxLayout(text,BoxLayout.Y_AXIS));
        text.setBorder(new EmptyBorder(28,31,25,0));
        text.add(Design.label("RESTAURANT MANAGEMENT  /  2026",11,Design.c("#E2D9FF"),true));
        text.add(Box.createVerticalStrut(14));text.add(Design.label("Жақсы сервис — бір қадамда.",25,Design.WHITE,true));
        text.add(Box.createVerticalStrut(8));
        text.add(Design.label("Мәзір, тапсырыс, асүй және аналитика — барлығы бір жерде.",12,Design.c("#E8E2FF"),false));
        text.add(Box.createVerticalStrut(13));
        Design.Button b=new Design.Button("Мәзірді ашу   →",Design.WHITE,Design.c("#EFEAFF"),Design.PURPLE,12);
        b.addActionListener(e->frame.navigate("menu"));b.setAlignmentX(Component.LEFT_ALIGNMENT);b.setMaximumSize(new Dimension(190,40));
        text.add(b);hero.add(text,BorderLayout.WEST);
        JLabel decor=Design.label("✦",105,new Color(255,255,255,150),false);decor.setBorder(new EmptyBorder(0,0,0,85));
        hero.add(decor,BorderLayout.EAST);return hero;
    }
    private JComponent stat(String title,String value,String sub,Color color,String glyph) {
        Design.RoundPanel p=Design.surface(Design.WHITE,18);
        p.setLayout(new BorderLayout());p.setBorder(new EmptyBorder(18,19,17,17));
        JPanel t=Design.panel(new BorderLayout());t.add(Design.label(title,10,Design.MUTED,true),BorderLayout.WEST);
        t.add(Design.label(glyph,23,color,true),BorderLayout.EAST);p.add(t,BorderLayout.NORTH);
        JPanel center=Design.panel();center.setLayout(new BoxLayout(center,BoxLayout.Y_AXIS));
        center.add(Box.createVerticalStrut(5));center.add(Design.label(value,25,Design.INK,true));
        center.add(Box.createVerticalStrut(5));center.add(Design.label(sub,11,Design.MUTED,false));p.add(center,BorderLayout.CENTER);return p;
    }
    private JComponent orderRow(Order order,RestaurantFrame frame){
        JPanel p=Design.panel(new BorderLayout(14,0));p.setBorder(new EmptyBorder(14,20,14,20));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,78));
        JPanel icon=Design.surface(Design.PURPLE_SOFT,13);icon.setPreferredSize(new Dimension(48,48));
        icon.setLayout(new GridBagLayout());icon.add(Design.label("▤",23,Design.PURPLE,true));p.add(icon,BorderLayout.WEST);
        JPanel name=Design.panel();name.setLayout(new BoxLayout(name,BoxLayout.Y_AXIS));
        name.add(Design.label(order.id()+"   ·   "+order.customer(),13,Design.INK,true));
        name.add(Box.createVerticalStrut(7));name.add(Design.label(order.createdAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm"))+
            "  •  "+order.items().size()+" позиция",11,Design.MUTED,false));p.add(name,BorderLayout.CENTER);
        JPanel end=Design.panel();end.setLayout(new BoxLayout(end,BoxLayout.Y_AXIS));
        JLabel sum=Design.label(Design.money(order.total()),13,Design.INK,true);sum.setAlignmentX(Component.RIGHT_ALIGNMENT);
        end.add(sum);end.add(Box.createVerticalStrut(7));
        JLabel status=Design.tag(order.status().label(),Design.statusBg(order.status()),Design.statusColor(order.status()));
        status.setAlignmentX(Component.RIGHT_ALIGNMENT);end.add(status);p.add(end,BorderLayout.EAST);
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        p.addMouseListener(new java.awt.event.MouseAdapter(){@Override public void mouseClicked(java.awt.event.MouseEvent e){frame.navigate("orders");}});
        return p;
    }
}
