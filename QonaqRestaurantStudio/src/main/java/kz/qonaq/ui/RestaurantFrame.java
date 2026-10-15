package kz.qonaq.ui;

import kz.qonaq.patterns.facade.RestaurantFacade;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RestaurantFrame extends JFrame {
    private final RestaurantFacade service;
    private final JPanel center=Design.panel(new BorderLayout()), nav=Design.panel();
    private final JLabel pageTitle=Design.label("",23,Design.INK,true), pageDescription=Design.label("",12,Design.MUTED,false);
    private final JLabel topBadge=Design.label("",12,Design.PURPLE,true);
    private final Map<String,Design.Button> menuButtons=new LinkedHashMap<>();
    private String page="dashboard";
    private String toast="Жүйе дайын • Барлық функциялар белсенді";
    private JLabel bottomStatus;
    public RestaurantFrame(RestaurantFacade service) {
        super("QONAQ Restaurant Studio • Smart Ordering System"); this.service=service;
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100,720));setSize(1460,900);setLocationRelativeTo(null);
        getContentPane().setBackground(Design.BG);setLayout(new BorderLayout());
        Design.applyWindowIcon(this);
        add(buildSidebar(),BorderLayout.WEST);
        JPanel workspace=new JPanel(new BorderLayout());workspace.setBackground(Design.BG);
        workspace.add(buildHeader(),BorderLayout.NORTH);workspace.add(center,BorderLayout.CENTER);
        workspace.add(buildStatus(),BorderLayout.SOUTH);add(workspace,BorderLayout.CENTER);
        service.events().subscribe((order,message)->{
            Runnable r=()->{toast=(order==null?"":order.id()+" • ")+message;
                if(bottomStatus!=null)bottomStatus.setText("●  "+toast); navigate(page);};
            if(SwingUtilities.isEventDispatchThread())r.run();else SwingUtilities.invokeLater(r);
        });
        navigate("dashboard");
    }
    private JComponent buildSidebar(){
        JPanel bar=new JPanel(new BorderLayout());bar.setBackground(Design.NAV);bar.setPreferredSize(new Dimension(242,1));
        JPanel main=Design.panel();main.setBorder(new EmptyBorder(27,15,18,15));
        main.setLayout(new BoxLayout(main,BoxLayout.Y_AXIS));
        JPanel logo=Design.panel(new BorderLayout(12,0));logo.setMaximumSize(new Dimension(220,60));
        JLabel symbol=Design.label("  Q  ",22,Design.WHITE,true);
        symbol.setOpaque(true);symbol.setBackground(Design.PURPLE);symbol.setBorder(new EmptyBorder(8,5,8,5));
        logo.add(symbol,BorderLayout.WEST);
        JPanel brand=Design.panel();brand.setLayout(new BoxLayout(brand,BoxLayout.Y_AXIS));
        brand.add(Design.label("QONAQ",21,Design.WHITE,true));brand.add(Box.createVerticalStrut(2));
        brand.add(Design.label("RESTAURANT OS",9,Design.c("#9C9BAE"),true));logo.add(brand,BorderLayout.CENTER);
        main.add(logo);main.add(Box.createVerticalStrut(48));
        JLabel section=Design.label("   WORKSPACE",10,Design.c("#818096"),true);
        section.setAlignmentX(Component.LEFT_ALIGNMENT);main.add(section);main.add(Box.createVerticalStrut(16));
        addNav(main,"dashboard","▦    Dashboard");
        addNav(main,"menu","◈    Тағам мәзірі");
        addNav(main,"orders","▤    Тапсырыстар");
        addNav(main,"analytics","◷    Аналитика");
        main.add(Box.createVerticalStrut(40));
        JLabel other=Design.label("   ЖҮЙЕ",10,Design.c("#818096"),true);other.setAlignmentX(Component.LEFT_ALIGNMENT);
        main.add(other);main.add(Box.createVerticalStrut(16));
        addNav(main,"patterns","◇    Паттерндер / UML");
        bar.add(main,BorderLayout.NORTH);
        JPanel bottom=Design.panel();bottom.setLayout(new BoxLayout(bottom,BoxLayout.Y_AXIS));
        bottom.setBorder(new EmptyBorder(0,20,28,20));
        JLabel pill=Design.label("  ●  SYSTEM ONLINE",11,Design.c("#79E6BD"),true);
        pill.setAlignmentX(Component.LEFT_ALIGNMENT);bottom.add(pill);bottom.add(Box.createVerticalStrut(9));
        bottom.add(Design.label("Java 17   •   v1.0.0",11,Design.c("#8D8CA1"),false));
        bar.add(bottom,BorderLayout.SOUTH);return bar;
    }
    private void addNav(JPanel root,String key,String text){
        Design.Button btn=new Design.Button(text,Design.NAV,Design.c("#292540"),Design.c("#B9B7CE"),13);
        btn.setHorizontalAlignment(SwingConstants.LEFT);btn.setMaximumSize(new Dimension(Integer.MAX_VALUE,47));
        btn.setPreferredSize(new Dimension(210,47));btn.addActionListener(e->navigate(key));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);root.add(btn);root.add(Box.createVerticalStrut(7));menuButtons.put(key,btn);
    }
    private JComponent buildHeader(){
        JPanel header=new JPanel(new BorderLayout(12,0));header.setBackground(Design.BG);
        header.setBorder(new EmptyBorder(23,31,19,32));
        JPanel titles=Design.panel();titles.setLayout(new BoxLayout(titles,BoxLayout.Y_AXIS));
        titles.add(pageTitle);titles.add(Box.createVerticalStrut(4));titles.add(pageDescription);
        header.add(titles,BorderLayout.WEST);
        JPanel right=Design.panel(new FlowLayout(FlowLayout.RIGHT,12,2));
        right.add(Design.tag("● LIVE DATA",Design.SOFT_GREEN,Design.GREEN));
        right.add(Design.label(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")),12,Design.MUTED,false));
        Design.Button newOrder=Design.primary("＋ Жаңа тапсырыс");
        newOrder.addActionListener(e->navigate("menu"));right.add(newOrder);
        header.add(right,BorderLayout.EAST);return header;
    }
    private JComponent buildStatus(){
        JPanel bar=Design.panel(new BorderLayout());bar.setBorder(new EmptyBorder(8,32,11,32));
        bottomStatus=Design.label("●  "+toast,11,Design.MUTED,false);bar.add(bottomStatus,BorderLayout.WEST);
        topBadge.setText("OFFLINE-FIRST  /  JAVA SWING");bar.add(topBadge,BorderLayout.EAST);return bar;
    }
    public void navigate(String newPage){
        page=newPage;
        for(var entry:menuButtons.entrySet()){
            boolean selected=entry.getKey().equals(page);
            entry.getValue().setBackground(selected?Design.c("#332B59"):Design.NAV);
            entry.getValue().setForeground(selected?Design.WHITE:Design.c("#B9B7CE"));
        }
        String[] heads=switch(page){
            case "menu"->new String[]{"Тағам мәзірі","Таңдаңыз, себетке қосыңыз және бірден тапсырыс беріңіз"};
            case "orders"->new String[]{"Тапсырыстарды басқару","Асүй процесі мен тапсырыс статустарын бақылау"};
            case "analytics"->new String[]{"Бизнес аналитика","Сатылым, танымал тағамдар және нәтижелер"};
            case "patterns"->new String[]{"Архитектура және паттерндер","GoF: Factory Method • Builder • State • Observer • Facade"};
            default->new String[]{"Басқару панелі","Қош келдіңіз! Бүгінгі мейрамхана жағдайы"};};
        pageTitle.setText(heads[0]);pageDescription.setText(heads[1]);
        center.removeAll();
        JComponent content=switch(page){
            case "menu"->new MenuPage(service,this);
            case "orders"->new OrdersPage(service,this);
            case "analytics"->new AnalyticsPage(service);
            case "patterns"->new PatternsPage(service,this);
            default->new DashboardPage(service,this);
        };
        center.add(content,BorderLayout.CENTER);center.revalidate();center.repaint();
    }
    public void flash(String message) {
        toast=message;if(bottomStatus!=null)bottomStatus.setText("●  "+message);
    }
    public void alert(String message){
        JOptionPane.showMessageDialog(this,message,"QONAQ Restaurant Studio",JOptionPane.INFORMATION_MESSAGE);
    }
}
