package kz.qonaq.ui;

import kz.qonaq.model.OrderStatus;
import kz.qonaq.patterns.facade.RestaurantFacade;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public final class AnalyticsPage extends JPanel {
    public AnalyticsPage(RestaurantFacade service){
        super(new BorderLayout());setOpaque(false);
        JPanel page=Design.vertical(0);page.setBorder(new EmptyBorder(2,31,25,31));
        JPanel stats=Design.panel(new GridLayout(1,3,17,0));
        stats.add(stat("Аяқталған",service.count(OrderStatus.COMPLETED)+" тапсырыс",Design.GREEN));
        stats.add(stat("Бас тартылған",service.count(OrderStatus.CANCELLED)+" тапсырыс",Design.RED));
        stats.add(stat("Конверсия",percent(service)+"%",Design.PURPLE));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE,111));page.add(stats);page.add(Box.createVerticalStrut(22));
        Design.RoundPanel graph=Design.surface(Design.WHITE,18);
        graph.setLayout(new BorderLayout());graph.setBorder(new EmptyBorder(21,22,20,22));
        JPanel header=Design.panel(new BorderLayout());header.add(Design.label("Соңғы 7 күндегі сатылым",18,Design.INK,true),BorderLayout.WEST);
        header.add(Design.tag("KZT  •  COMPLETED",Design.PURPLE_SOFT,Design.PURPLE),BorderLayout.EAST);
        graph.add(header,BorderLayout.NORTH);graph.add(new SalesGraph(service),BorderLayout.CENTER);
        graph.setPreferredSize(new Dimension(700,317));graph.setMaximumSize(new Dimension(Integer.MAX_VALUE,317));
        page.add(graph);page.add(Box.createVerticalStrut(20));
        JPanel lower=Design.panel(new GridLayout(1,2,18,0));
        Design.RoundPanel popular=Design.surface(Design.WHITE,18);popular.setLayout(new BorderLayout());
        popular.setBorder(new EmptyBorder(23,23,19,23));
        popular.add(Design.label("Танымал тағамдар",17,Design.INK,true),BorderLayout.NORTH);
        JPanel items=Design.vertical(0);items.setBorder(new EmptyBorder(12,0,0,0));
        var products=service.popularProducts();int i=0;
        if(products.isEmpty())items.add(Design.label("Дерек әлі жоқ",12,Design.MUTED,false));
        for(var entry:products.entrySet()){
            if(i++>=5)break;
            JPanel row=Design.panel(new BorderLayout());row.setBorder(new EmptyBorder(8,0,8,0));
            row.add(Design.label(i+".  "+entry.getKey(),12,Design.INK,false),BorderLayout.WEST);
            row.add(Design.tag(entry.getValue()+" дана",Design.PURPLE_SOFT,Design.PURPLE),BorderLayout.EAST);
            items.add(row);
        }
        popular.add(items,BorderLayout.CENTER);lower.add(popular);
        Design.RoundPanel states=Design.surface(Design.WHITE,18);states.setLayout(new BorderLayout());
        states.setBorder(new EmptyBorder(23,23,19,23));
        states.add(Design.label("Тапсырыс статустары",17,Design.INK,true),BorderLayout.NORTH);
        JPanel bars=Design.vertical(0);bars.setBorder(new EmptyBorder(12,0,0,0));
        int max=(int)Math.max(1,service.orders().size());
        for(OrderStatus status:OrderStatus.values()){
            JPanel row=Design.panel(new BorderLayout());row.setBorder(new EmptyBorder(6,0,6,0));
            row.add(Design.label(status.label(),12,Design.MUTED,false),BorderLayout.WEST);
            row.add(Design.label(service.count(status)+" / "+max,12,Design.INK,true),BorderLayout.EAST);
            bars.add(row);
            bars.add(new ProgressBar((float)service.count(status)/max,Design.statusColor(status)));
            bars.add(Box.createVerticalStrut(5));
        }
        states.add(bars,BorderLayout.CENTER);lower.add(states);
        lower.setMaximumSize(new Dimension(Integer.MAX_VALUE,335));page.add(lower);
        add(Design.scroll(page),BorderLayout.CENTER);
    }
    private String percent(RestaurantFacade s){long all=s.orders().size();return all==0?"0":String.format(Locale.US,"%.1f",s.count(OrderStatus.COMPLETED)*100.0/all);}
    private JComponent stat(String name,String value,Color color){
        Design.RoundPanel p=Design.surface(Design.WHITE,17);p.setBorder(new EmptyBorder(18,19,18,19));
        p.setLayout(new BorderLayout());p.add(Design.label(name,12,Design.MUTED,false),BorderLayout.NORTH);
        p.add(Design.label(value,24,color,true),BorderLayout.CENTER);return p;
    }
    private static final class ProgressBar extends JComponent{
        private final float percent;private final Color fg;
        ProgressBar(float percent,Color fg){this.percent=percent;this.fg=fg;setPreferredSize(new Dimension(100,7));setMaximumSize(new Dimension(Integer.MAX_VALUE,7));}
        @Override protected void paintComponent(Graphics graphics){Graphics2D g=(Graphics2D)graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Design.c("#F1F1F7"));g.fillRoundRect(0,0,getWidth(),7,7,7);
            g.setColor(fg);g.fillRoundRect(0,0,(int)(getWidth()*percent),7,7,7);g.dispose();}
    }
    private static final class SalesGraph extends JComponent {
        private final int[] sales=new int[7];private final String[] days=new String[7];
        SalesGraph(RestaurantFacade service){setPreferredSize(new Dimension(500,250));
            for(int i=0;i<7;i++){LocalDate day=LocalDate.now().minusDays(6-i);sales[i]=service.daySales(day);
                days[i]=day.format(DateTimeFormatter.ofPattern("dd.MM"));}}
        @Override protected void paintComponent(Graphics graphics){
            Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            int w=getWidth(),h=getHeight(),left=56,right=12,top=22,bottom=37,chartH=h-top-bottom;
            int max=Arrays.stream(sales).max().orElse(0);max=Math.max(10000,((max+4999)/5000)*5000);
            g.setFont(Design.font(11,Font.PLAIN));
            for(int i=0;i<=4;i++){
                int y=top+chartH*i/4;g.setColor(Design.c("#F0F1F7"));g.drawLine(left,y,w-right,y);
                g.setColor(Design.MUTED);g.drawString((max-max*i/4)/1000+"k",8,y+4);
            }
            int available=Math.max(100,w-left-right);int gap=available/7;
            int barWidth=Math.min(52,Math.max(15,(int)(gap*.51)));
            for(int i=0;i<7;i++){
                int x=left+i*gap+(gap-barWidth)/2;
                int bh=(int)(chartH*((double)sales[i]/max));
                if(sales[i]>0)bh=Math.max(4,bh);
                g.setPaint(new GradientPaint(0,top,Design.c("#AC97FF"),0,top+chartH,Design.PURPLE));
                if(bh>0)g.fillRoundRect(x,top+chartH-bh,barWidth,bh,9,9);
                g.setColor(Design.MUTED);g.drawString(days[i],x-2,top+chartH+22);
            }
            g.dispose();
        }
    }
}
