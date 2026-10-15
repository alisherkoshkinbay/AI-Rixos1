package kz.qonaq.ui;

import kz.qonaq.model.MenuItem;
import kz.qonaq.patterns.facade.RestaurantFacade;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class ProductDialog extends JDialog {
    public ProductDialog(JFrame owner,MenuItem item,RestaurantFacade service,Runnable done){
        super(owner,"Тағам мәліметі",true);setSize(460,520);setLocationRelativeTo(owner);setResizable(false);
        JPanel root=new JPanel(new BorderLayout(0,12));root.setBackground(Design.WHITE);
        root.setBorder(new EmptyBorder(18,20,20,20));
        FoodArt art=new FoodArt(item.artStyle());art.setPreferredSize(new Dimension(340,195));root.add(art,BorderLayout.NORTH);
        JPanel body=Design.panel();body.setLayout(new BoxLayout(body,BoxLayout.Y_AXIS));
        body.add(Design.label(item.name(),22,Design.INK,true));body.add(Box.createVerticalStrut(8));
        body.add(Design.label(item.description(),12,Design.MUTED,false));body.add(Box.createVerticalStrut(15));
        body.add(Design.label("Саны",12,Design.INK,true));body.add(Box.createVerticalStrut(7));
        JSpinner spinner=new JSpinner(new SpinnerNumberModel(1,1,50,1));spinner.setFont(Design.font(14,Font.BOLD));
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE,37));body.add(spinner);body.add(Box.createVerticalStrut(10));
        body.add(Design.label("Арнайы ескертпе (міндетті емес)",12,Design.INK,true));body.add(Box.createVerticalStrut(6));
        JTextField note=Design.field("Мысалы: пиязсыз, ащы емес...");
        JComponent field=Design.fieldBox(note);field.setMaximumSize(new Dimension(Integer.MAX_VALUE,43));body.add(field);
        root.add(body,BorderLayout.CENTER);
        JPanel bottom=Design.panel(new BorderLayout());
        bottom.add(Design.label(Design.money(item.price()),17,Design.PURPLE,true),BorderLayout.WEST);
        Design.Button add=Design.primary("Себетке қосу  ＋");
        add.addActionListener(e->{try{
            service.addToCart(item.id(),(int)spinner.getValue());
            if(!note.getText().isBlank())service.setLineNote(item.id(),note.getText());
            done.run();dispose();
        }catch(IllegalArgumentException ex){JOptionPane.showMessageDialog(this,ex.getMessage());}});
        bottom.add(add,BorderLayout.EAST);root.add(bottom,BorderLayout.SOUTH);
        setContentPane(root);
    }
}
