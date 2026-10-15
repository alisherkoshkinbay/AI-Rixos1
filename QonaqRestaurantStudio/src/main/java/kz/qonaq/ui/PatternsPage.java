package kz.qonaq.ui;

import kz.qonaq.patterns.facade.RestaurantFacade;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class PatternsPage extends JPanel {
    public PatternsPage(RestaurantFacade service,RestaurantFrame frame){
        super(new BorderLayout());setOpaque(false);
        JPanel stack=Design.vertical(0);stack.setBorder(new EmptyBorder(1,31,26,31));
        stack.add(Design.label("5 GoF Design Patterns",23,Design.INK,true));stack.add(Box.createVerticalStrut(8));
        stack.add(Design.label("Қосымша архитектурасында паттерндер тек атау емес — нақты қолданылады.",12,Design.MUTED,false));
        stack.add(Box.createVerticalStrut(20));
        addPattern(stack,"01","Factory Method","КАТАЛОГ ҚҰРУ",
            "MenuFactory → FoodFactory, DrinkFactory, DessertFactory",
            "Әр фабрика тағам, сусын және десерт объектілерін өз санатында жасайды.");
        addPattern(stack,"02","Builder","ТАПСЫРЫС ЖИНАУ",
            "OrderBuilder → Order",
            "Клиент, телефон, жеткізу түрі, тағамдар және жеңілдік тексеріліп, бір тапсырысқа біріктіріледі.");
        addPattern(stack,"03","State","ТАПСЫРЫС ӨМІРЛІК ЦИКЛІ",
            "NEW → PREPARING → READY → COMPLETED",
            "Әр күй келесі әрекетке рұқсат береді немесе тыйым салады; CANCELLED — соңғы күй.");
        addPattern(stack,"04","Observer","ХАБАРЛАМАЛАР",
            "OrderEvents → OrderObserver → RestaurantFrame",
            "Жаңа тапсырыс пен статус өзгергенде, жазылған интерфейс автоматты түрде жаңарады.");
        addPattern(stack,"05","Facade","БІРЫҢҒАЙ API",
            "RestaurantFacade → Catalog / Cart / Orders / Repository / Events",
            "UI бір сервиске жүгінеді; паттерндер мен сақтаудың ішкі логикасын білмейді.");
        stack.add(Box.createVerticalStrut(10));
        Design.RoundPanel data=Design.surface(Design.PURPLE_SOFT,16);data.setLayout(new BorderLayout(10,0));
        data.setBorder(new EmptyBorder(18,21,18,21));
        JPanel info=Design.panel();info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS));
        info.add(Design.label("ДЕРЕКТЕРДІ САҚТАУ",11,Design.PURPLE,true));info.add(Box.createVerticalStrut(5));
        info.add(Design.label("Жүйе файлдары: "+service.storagePath(),11,Design.INK,false));
        data.add(info,BorderLayout.CENTER);
        Design.Button clear=Design.secondary("Демо деректерді өшіру");
        clear.addActionListener(e->{int n=JOptionPane.showConfirmDialog(this,"Тек DEMO тапсырыстарын өшіреміз бе?",
            "Растау",JOptionPane.YES_NO_OPTION);if(n==JOptionPane.YES_OPTION){try{service.removeDemo();frame.flash("Демо тапсырыстар өшірілді");}
            catch(Exception ex){frame.alert(ex.getMessage());}}});
        data.add(clear,BorderLayout.EAST);stack.add(data);
        add(Design.scroll(stack),BorderLayout.CENTER);
    }
    private void addPattern(JPanel parent,String id,String name,String category,String path,String explanation){
        Design.RoundPanel p=Design.surface(Design.WHITE,16);p.setLayout(new BorderLayout(15,0));
        p.setBorder(new EmptyBorder(19,20,19,20));
        JLabel marker=Design.label(id,29,Design.PURPLE,true);marker.setPreferredSize(new Dimension(70,60));
        p.add(marker,BorderLayout.WEST);
        JPanel body=Design.panel();body.setLayout(new BoxLayout(body,BoxLayout.Y_AXIS));
        body.add(Design.label(name+"   /   "+category,15,Design.INK,true));body.add(Box.createVerticalStrut(7));
        body.add(Design.label(path,12,Design.PURPLE,true));body.add(Box.createVerticalStrut(7));
        body.add(Design.label(explanation,11,Design.MUTED,false));p.add(body,BorderLayout.CENTER);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE,119));parent.add(p);parent.add(Box.createVerticalStrut(11));
    }
}
