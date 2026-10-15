package kz.qonaq.ui;

import kz.qonaq.model.*;
import kz.qonaq.patterns.facade.RestaurantFacade;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public final class OrdersPage extends JPanel {
    private final RestaurantFacade service;
    private final RestaurantFrame frame;
    private final JPanel list=Design.panel(), details=Design.panel(new BorderLayout());
    private String status="Барлығы",selectedId=null;
    private JTextField search;
    public OrdersPage(RestaurantFacade service,RestaurantFrame frame){
        super(new BorderLayout(17,0));this.service=service;this.frame=frame;setOpaque(false);
        setBorder(new EmptyBorder(0,31,15,31));
        JPanel left=Design.panel(new BorderLayout(0,14));left.setPreferredSize(new Dimension(460,150));
        JPanel head=Design.panel();head.setLayout(new BoxLayout(head,BoxLayout.Y_AXIS));
        JPanel topRow=Design.panel(new BorderLayout());
        topRow.add(Design.label("Барлық тапсырыстар",18,Design.INK,true),BorderLayout.WEST);
        Design.Button export=Design.secondary("⇩  CSV экспорт");export.addActionListener(e->exportCsv());
        topRow.add(export,BorderLayout.EAST);head.add(topRow);head.add(Box.createVerticalStrut(13));
        search=Design.field("ID немесе клиент аты...");
        JPanel searchWrap=Design.surface(Design.WHITE,12);searchWrap.setLayout(new BorderLayout());
        searchWrap.add(Design.label("   ⌕  ",17,Design.PURPLE,false),BorderLayout.WEST);searchWrap.add(search);
        searchWrap.setMaximumSize(new Dimension(Integer.MAX_VALUE,44));head.add(searchWrap);
        head.add(Box.createVerticalStrut(12));
        JPanel filters=Design.panel(new FlowLayout(FlowLayout.LEFT,5,2));
        for(String s:List.of("Барлығы","Жаңа","Дайындалуда","Дайын","Аяқталды","Бас тартылды")){
            Design.Button b=Design.mutedButton(s);b.setFont(Design.font(11,Font.BOLD));
            b.setBorder(new EmptyBorder(9,9,9,9));
            b.addActionListener(e->{status=s;rebuildList();});filters.add(b);
        }
        head.add(filters);left.add(head,BorderLayout.NORTH);
        list.setLayout(new BoxLayout(list,BoxLayout.Y_AXIS));list.setBorder(new EmptyBorder(1,0,6,4));
        left.add(Design.scroll(list),BorderLayout.CENTER);
        add(left,BorderLayout.WEST);
        Design.RoundPanel right=Design.surface(Design.WHITE,20);right.setLayout(new BorderLayout());
        right.add(details,BorderLayout.CENTER);add(right,BorderLayout.CENTER);
        search.getDocument().addDocumentListener(new DocumentListener(){
            public void insertUpdate(DocumentEvent e){rebuildList();}
            public void removeUpdate(DocumentEvent e){rebuildList();}
            public void changedUpdate(DocumentEvent e){rebuildList();}
        });
        List<Order> all=service.orders();if(!all.isEmpty())selectedId=all.get(0).id();
        rebuildList();refreshDetails();
    }
    private void rebuildList(){
        list.removeAll();
        String query=search.getText().trim().toLowerCase(Locale.ROOT);
        List<Order> visible=service.orders().stream().filter(o->
            (status.equals("Барлығы")||o.status().label().equals(status))&&
            (o.id().toLowerCase(Locale.ROOT).contains(query)||o.customer().toLowerCase(Locale.ROOT).contains(query))).toList();
        if(visible.isEmpty())list.add(Design.pad(Design.label("Тапсырыс табылмады",13,Design.MUTED,false),22,22,22,22));
        for(Order o:visible){list.add(orderCard(o));list.add(Box.createVerticalStrut(11));}
        list.revalidate();list.repaint();
    }
    private JComponent orderCard(Order o){
        boolean selected=o.id().equals(selectedId);
        Design.RoundPanel box=Design.surface(selected?Design.c("#EEEAFF"):Design.WHITE,16);
        box.setLayout(new BorderLayout());box.setBorder(new EmptyBorder(17,18,17,18));
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE,112));
        JPanel first=Design.panel(new BorderLayout());
        first.add(Design.label(o.id()+"  •  "+o.customer(),13,Design.INK,true),BorderLayout.WEST);
        first.add(Design.tag(o.status().label(),Design.statusBg(o.status()),Design.statusColor(o.status())),BorderLayout.EAST);
        box.add(first,BorderLayout.NORTH);
        JPanel bottom=Design.panel(new BorderLayout());
        bottom.add(Design.label(o.createdAt().format(DateTimeFormatter.ofPattern("dd.MM.yyyy  HH:mm")),11,Design.MUTED,false),BorderLayout.WEST);
        bottom.add(Design.label(Design.money(o.total()),13,Design.PURPLE,true),BorderLayout.EAST);
        bottom.setBorder(new EmptyBorder(12,0,0,0));box.add(bottom,BorderLayout.SOUTH);
        box.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        box.addMouseListener(new java.awt.event.MouseAdapter(){@Override public void mouseClicked(java.awt.event.MouseEvent e){
            selectedId=o.id();rebuildList();refreshDetails();
        }});return box;
    }
    private void refreshDetails(){
        details.removeAll();
        if(selectedId==null){details.add(Design.pad(Design.label("Тапсырысты таңдаңыз",14,Design.MUTED,false),30,30,30,30));return;}
        Order o;
        try{o=service.find(selectedId);}catch(IllegalArgumentException ex){return;}
        JPanel root=Design.panel(new BorderLayout());root.setBorder(new EmptyBorder(25,27,24,27));
        JPanel top=Design.panel();top.setLayout(new BoxLayout(top,BoxLayout.Y_AXIS));
        JPanel num=Design.panel(new BorderLayout());num.add(Design.label("Тапсырыс "+o.id(),20,Design.INK,true),BorderLayout.WEST);
        JPanel badge=Design.panel(new FlowLayout(FlowLayout.RIGHT,0,0));
        badge.add(Design.tag(o.status().label(),Design.statusBg(o.status()),Design.statusColor(o.status())));
        num.add(badge,BorderLayout.EAST);
        top.add(num);top.add(Box.createVerticalStrut(7));
        top.add(Design.label(o.createdAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy • HH:mm")),12,Design.MUTED,false));
        top.add(Box.createVerticalStrut(22));
        top.add(Design.label("ТАПСЫРЫС БАҒЫТЫ",10,Design.MUTED,true));top.add(Box.createVerticalStrut(11));
        top.add(progress(o.status()));top.add(Box.createVerticalStrut(22));top.add(Design.sep());
        top.add(Box.createVerticalStrut(18));
        top.add(Design.label("КЛИЕНТ АҚПАРАТЫ",10,Design.MUTED,true));top.add(Box.createVerticalStrut(10));
        top.add(Design.label(o.customer()+"  •  "+o.phone(),13,Design.INK,true));top.add(Box.createVerticalStrut(6));
        top.add(Design.label(o.fulfillment()+(o.location().isBlank()?"":"  ·  "+o.location()),12,Design.MUTED,false));
        if(!o.comment().isBlank()){top.add(Box.createVerticalStrut(6));top.add(Design.label("Ескертпе: "+o.comment(),11,Design.MUTED,false));}
        top.add(Box.createVerticalStrut(20));top.add(Design.sep());top.add(Box.createVerticalStrut(15));
        top.add(Design.label("ТАПСЫРЫС ҚҰРАМЫ",10,Design.MUTED,true));top.add(Box.createVerticalStrut(10));
        for(CartLine line:o.items()){
            JPanel row=Design.panel(new BorderLayout());row.setBorder(new EmptyBorder(8,0,9,0));
            row.add(Design.label(line.quantity()+"×  "+line.name(),12,Design.INK,false),BorderLayout.WEST);
            row.add(Design.label(Design.money(line.total()),12,Design.INK,true),BorderLayout.EAST);top.add(row);
            if(!line.note().isBlank()){top.add(Design.label("     ↳ "+line.note(),11,Design.MUTED,false));}
        }
        root.add(Design.scroll(top),BorderLayout.CENTER);
        JPanel bottom=Design.panel();bottom.setLayout(new BoxLayout(bottom,BoxLayout.Y_AXIS));
        bottom.add(Design.sep());bottom.add(Box.createVerticalStrut(13));
        if(o.discount()>0){bottom.add(amount("Жеңілдік", "− "+Design.money(o.discount())));bottom.add(Box.createVerticalStrut(9));}
        bottom.add(amount("Жалпы сома",Design.money(o.total())));bottom.add(Box.createVerticalStrut(15));
        if(o.status()!=OrderStatus.COMPLETED&&o.status()!=OrderStatus.CANCELLED){
            String next=switch(o.status()){
                case NEW->"Дайындауды бастау  →";case PREPARING->"Дайын деп белгілеу  →";
                case READY->"Тапсырысты аяқтау  ✓";default->"";};
            Design.Button act=Design.primary(next);act.addActionListener(e->{try{service.advance(o.id());}
                catch(RuntimeException ex){frame.alert(ex.getMessage());}});
            act.setMaximumSize(new Dimension(Integer.MAX_VALUE,48));bottom.add(act);bottom.add(Box.createVerticalStrut(10));
            if(o.status()!=OrderStatus.READY){
                Design.Button cancel=new Design.Button("Тапсырыстан бас тарту",Design.SOFT_RED,Design.c("#FFE3E5"),Design.RED,13);
                cancel.addActionListener(e->{int choice=JOptionPane.showConfirmDialog(this,"Тапсырысты болдырмау керек пе?",
                    "Растау",JOptionPane.YES_NO_OPTION);
                    if(choice==JOptionPane.YES_OPTION){try{service.cancel(o.id());}catch(RuntimeException ex){frame.alert(ex.getMessage());}}});
                cancel.setMaximumSize(new Dimension(Integer.MAX_VALUE,43));bottom.add(cancel);bottom.add(Box.createVerticalStrut(10));
            }
        }
        Design.Button receipt=Design.mutedButton("▣  Чекті көшіру");
        receipt.addActionListener(e->{
            StringBuilder sb=new StringBuilder("QONAQ RESTAURANT STUDIO\n").append(o.id()).append("\n")
                .append(o.createdAt()).append("\nКлиент: ").append(o.customer()).append("\n\n");
            for(CartLine line:o.items())sb.append(line.quantity()).append("× ").append(line.name()).append(" = ").append(Design.money(line.total())).append("\n");
            sb.append("Жеңілдік: ").append(Design.money(o.discount())).append("\nБарлығы: ").append(Design.money(o.total()));
            try{Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()),null);frame.flash("Чек алмасу буферіне көшірілді");}
            catch(IllegalStateException ex){frame.alert("Буфер бос емес. Қайта көріңіз.");}
        });receipt.setMaximumSize(new Dimension(Integer.MAX_VALUE,42));bottom.add(receipt);
        root.add(bottom,BorderLayout.SOUTH);details.add(root,BorderLayout.CENTER);
        details.revalidate();details.repaint();
    }
    private JComponent progress(OrderStatus s){
        JPanel p=Design.panel(new GridLayout(1,4,5,0));int current=switch(s){
            case NEW->0;case PREPARING->1;case READY->2;case COMPLETED->3;case CANCELLED->-1;};
        String[] stages={"Жаңа","Асүйде","Дайын","Аяқталды"};
        for(int i=0;i<4;i++){
            JPanel stage=Design.panel();stage.setLayout(new BoxLayout(stage,BoxLayout.Y_AXIS));
            Design.RoundPanel strip=Design.surface(i<=current?Design.PURPLE:Design.c("#ECEEF3"),8);
            strip.setMaximumSize(new Dimension(Integer.MAX_VALUE,7));strip.setPreferredSize(new Dimension(60,7));
            stage.add(strip);stage.add(Box.createVerticalStrut(7));
            stage.add(Design.label(stages[i],10,i<=current?Design.PURPLE:Design.MUTED,i==current));p.add(stage);
        }return p;
    }
    private JComponent amount(String name,String money){
        JPanel p=Design.panel(new BorderLayout());p.setMaximumSize(new Dimension(Integer.MAX_VALUE,30));
        p.add(Design.label(name,14,Design.INK,true),BorderLayout.WEST);
        p.add(Design.label(money,18,Design.PURPLE,true),BorderLayout.EAST);return p;
    }
    private void exportCsv(){
        JFileChooser chooser=new JFileChooser();chooser.setSelectedFile(new java.io.File("qonaq-orders.csv"));
        if(chooser.showSaveDialog(this)!=JFileChooser.APPROVE_OPTION)return;
        StringBuilder csv=new StringBuilder("\uFEFFID,Customer,Phone,Date,Fulfillment,Status,Subtotal,Discount,Total\n");
        for(Order o:service.orders()){
            String[] cols={o.id(),o.customer(),o.phone(),o.createdAt().toString(),o.fulfillment(),o.status().label(),
                ""+o.subtotal(),""+o.discount(),""+o.total()};
            for(int i=0;i<cols.length;i++){
                if(i>0)csv.append(',');csv.append('"').append(cols[i].replace("\"","\"\"")).append('"');
            }csv.append('\n');
        }
        try{Files.writeString(chooser.getSelectedFile().toPath(),csv,StandardCharsets.UTF_8);frame.flash("CSV файлы сақталды: "+chooser.getSelectedFile().getName());}
        catch(Exception ex){frame.alert("CSV сақтау қатесі: "+ex.getMessage());}
    }
}
