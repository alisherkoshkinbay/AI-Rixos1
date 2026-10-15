package kz.qonaq.ui;

import kz.qonaq.model.Order;
import kz.qonaq.patterns.facade.RestaurantFacade;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public final class CheckoutDialog extends JDialog {
    public CheckoutDialog(JFrame owner,RestaurantFacade service,Runnable done){
        super(owner,"Тапсырысты рәсімдеу",true);setSize(540,665);setLocationRelativeTo(owner);setResizable(false);
        JPanel root=new JPanel(new BorderLayout());root.setBackground(Design.WHITE);root.setBorder(new EmptyBorder(26,30,24,30));
        JPanel head=Design.panel();head.setLayout(new BoxLayout(head,BoxLayout.Y_AXIS));
        head.add(Design.label("Тапсырысты рәсімдеу",25,Design.INK,true));head.add(Box.createVerticalStrut(6));
        head.add(Design.label("Клиент мәліметін толтырыңыз • "+service.cartQuantity()+" өнім",12,Design.MUTED,false));
        root.add(head,BorderLayout.NORTH);
        JPanel form=Design.panel();form.setLayout(new BoxLayout(form,BoxLayout.Y_AXIS));form.setBorder(new EmptyBorder(25,0,5,0));
        JTextField name=Design.field("Аты-жөні");name.setText("");
        JTextField phone=Design.field("+7 777 123 45 67");
        JComboBox<String> delivery=new JComboBox<>(new String[]{"Алып кету","Үстелде","Жеткізу"});
        delivery.setFont(Design.font(13,Font.PLAIN));delivery.setBackground(Design.WHITE);
        JTextField location=Design.field("Мекенжай немесе үстел №");
        JTextField comment=Design.field("Арнайы нұсқаулық...");
        field(form,"КЛИЕНТ АТЫ *",name);
        field(form,"ТЕЛЕФОН *",phone);
        form.add(Design.label("ҚЫЗМЕТ ТҮРІ",10,Design.MUTED,true));form.add(Box.createVerticalStrut(8));
        delivery.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));form.add(delivery);form.add(Box.createVerticalStrut(17));
        field(form,"МЕКЕНЖАЙ / ҮСТЕЛ",location);field(form,"ЕСКЕРТПЕ",comment);
        JLabel info=Design.label("* Жеткізу үшін мекенжай міндетті.",11,Design.MUTED,false);form.add(info);
        root.add(form,BorderLayout.CENTER);
        JPanel foot=Design.panel();foot.setLayout(new BoxLayout(foot,BoxLayout.Y_AXIS));
        foot.add(Design.sep());foot.add(Box.createVerticalStrut(13));
        JPanel price=Design.panel(new BorderLayout());price.add(Design.label("Төлеуге",15,Design.INK,true),BorderLayout.WEST);
        price.add(Design.label(Design.money(service.total()),22,Design.PURPLE,true),BorderLayout.EAST);
        foot.add(price);foot.add(Box.createVerticalStrut(15));
        JPanel buttons=Design.panel(new GridLayout(1,2,10,0));
        Design.Button close=Design.mutedButton("Болдырмау");close.addActionListener(e->dispose());
        Design.Button submit=Design.primary("Тапсырыс беру  ✓");
        submit.addActionListener(e->{
            try {
                Order order=service.checkout(name.getText(),phone.getText(),
                    String.valueOf(delivery.getSelectedItem()),location.getText(),comment.getText());
                dispose();done.run();
                JOptionPane.showMessageDialog(owner,"Тапсырыс қабылданды!\nID: "+order.id()+"\nСома: "+Design.money(order.total()),
                    "QONAQ • Сәтті",JOptionPane.INFORMATION_MESSAGE);
            }catch(RuntimeException ex){JOptionPane.showMessageDialog(this,ex.getMessage(),"Қате",JOptionPane.WARNING_MESSAGE);}
        });
        buttons.add(close);buttons.add(submit);foot.add(buttons);root.add(foot,BorderLayout.SOUTH);
        setContentPane(root);
    }
    private void field(JPanel panel,String caption,JTextField input){
        panel.add(Design.label(caption,10,Design.MUTED,true));panel.add(Box.createVerticalStrut(7));
        JComponent box=Design.fieldBox(input);box.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));
        panel.add(box);panel.add(Box.createVerticalStrut(17));
    }
}
