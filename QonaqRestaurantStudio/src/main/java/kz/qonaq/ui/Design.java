package kz.qonaq.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public final class Design {
    private Design() {}
    public static final Color BG=c("#F6F7FB"), WHITE=Color.WHITE, INK=c("#20233B"), MUTED=c("#9297AA"),
        PURPLE=c("#7557F6"), PURPLE_SOFT=c("#F0ECFF"), NAV=c("#19172E"), STROKE=c("#ECECF4"),
        GREEN=c("#12A878"), SOFT_GREEN=c("#E8FAF3"), RED=c("#E65B68"), SOFT_RED=c("#FFF0F0"),
        ORANGE=c("#F69F45"), SOFT_ORANGE=c("#FFF4E7");
    public static Color c(String v){return Color.decode(v);}
    public static Font font(int size,int style) { return new Font("SansSerif",style,size); }
    public static JLabel label(String text,int size,Color color,boolean bold) {
        JLabel l=new JLabel(text); l.setFont(font(size,bold?Font.BOLD:Font.PLAIN)); l.setForeground(color); return l;
    }
    public static JLabel label(String text,int size) {return label(text,size,INK,false);}
    public static JPanel panel(LayoutManager layout) { JPanel p=new JPanel(layout); p.setOpaque(false); return p; }
    public static JPanel panel() { return panel(new BorderLayout()); }
    public static JPanel vertical(int gap) { JPanel p=panel();p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS)); return p; }
    public static JPanel pad(JComponent content,int top,int left,int bottom,int right) {
        JPanel p=panel(new BorderLayout()); p.setBorder(new EmptyBorder(top,left,bottom,right));p.add(content);return p;
    }
    public static JPanel fixedHeight(int height) { JPanel p=panel(); p.setPreferredSize(new Dimension(1,height));p.setMaximumSize(new Dimension(Integer.MAX_VALUE,height));return p; }
    public static JPanel fixedWidth(int width) { JPanel p=panel();p.setPreferredSize(new Dimension(width,1));return p; }
    public static void size(JComponent c,int w,int h){c.setPreferredSize(new Dimension(w,h));}
    public static RoundPanel surface(Color color,int radius) {return new RoundPanel(color,radius);}
    public static JScrollPane scroll(JComponent component) {
        JScrollPane pane=new JScrollPane(component,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        pane.setBorder(null); pane.setOpaque(false);pane.getViewport().setOpaque(false);
        pane.getVerticalScrollBar().setUnitIncrement(18);pane.getVerticalScrollBar().setPreferredSize(new Dimension(8,0));
        pane.getVerticalScrollBar().setUI(new BasicScrollBarUI(){
            protected void configureScrollBarColors() {thumbColor=c("#D8D9E5");trackColor=BG;}
            protected JButton createDecreaseButton(int o){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
            protected JButton createIncreaseButton(int o){JButton b=new JButton();b.setPreferredSize(new Dimension(0,0));return b;}
            protected void paintThumb(Graphics g,JComponent comp,Rectangle r) {
                Graphics2D g2=(Graphics2D)g.create();g2.setColor(c("#D6D6E3"));g2.fillRoundRect(r.x+2,r.y,r.width-4,r.height,8,8);g2.dispose();
            }
            protected void paintTrack(Graphics g,JComponent comp,Rectangle r){}
        }); return pane;
    }
    public static JTextField field(String placeholder) {
        JTextField f=new JTextField(){
            @Override protected void paintComponent(Graphics graphics){
                super.paintComponent(graphics);
                if(getText().isEmpty()){
                    Graphics2D g=(Graphics2D)graphics.create();
                    g.setColor(Design.c("#B6B8C7"));g.setFont(getFont());
                    Insets in=getInsets();
                    FontMetrics fm=g.getFontMetrics();
                    g.drawString(placeholder,in.left,(getHeight()-fm.getHeight())/2+fm.getAscent());
                    g.dispose();
                }
            }
        };
        f.setFont(font(13,Font.PLAIN));f.setForeground(INK);f.setCaretColor(PURPLE);
        f.setOpaque(false);f.setBorder(new EmptyBorder(10,13,10,13));
        return f;
    }
    public static JComponent fieldBox(JTextField f) {
        RoundPanel p=surface(c("#F7F8FB"),13);p.setLayout(new BorderLayout());p.setBorder(BorderFactory.createLineBorder(STROKE,1,true));p.add(f);return p;
    }
    public static final class RoundPanel extends JPanel {
        private final Color fill; private final int radius;
        public RoundPanel(Color fill,int radius){this.fill=fill;this.radius=radius;setOpaque(false);}
        @Override protected void paintComponent(Graphics graphics) {
            Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);g.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);g.dispose();super.paintComponent(graphics);
        }
    }
    public static final class Button extends JButton {
        private final Color normal, hover; private boolean hovered=false; private final int radius;
        public Button(String text,Color normal,Color hover,Color foreground,int radius) {
            super(text);this.normal=normal;this.hover=hover;this.radius=radius;
            setFont(font(13,Font.BOLD));setForeground(foreground);setBackground(normal);setFocusPainted(false);setBorderPainted(false);
            setContentAreaFilled(false);setOpaque(false);setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(new EmptyBorder(10,15,10,15));
            addMouseListener(new MouseAdapter(){public void mouseEntered(MouseEvent e){hovered=true;repaint();}
                public void mouseExited(MouseEvent e){hovered=false;repaint();}});
        }
        @Override protected void paintComponent(Graphics graphics){
            Graphics2D g=(Graphics2D)graphics.create();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(!isEnabled()?c("#D5D4E2"):(hovered?hover:getBackground()));
            g.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);
            g.dispose();super.paintComponent(graphics);
        }
    }
    public static Button primary(String text){return new Button(text,PURPLE,c("#6244E7"),WHITE,14);}
    public static Button secondary(String text){return new Button(text,PURPLE_SOFT,c("#E4DCFF"),PURPLE,14);}
    public static Button mutedButton(String text){return new Button(text,c("#F3F4F8"),c("#EBECF5"),INK,12);}
    public static JLabel tag(String text,Color bg,Color fg){
        JLabel l=label("  "+text+"  ",10,fg,true);l.setOpaque(true);l.setBackground(bg);l.setBorder(new EmptyBorder(5,6,5,6));return l;
    }
    public static JSeparator sep(){JSeparator s=new JSeparator();s.setForeground(STROKE);s.setBackground(STROKE);return s;}
    public static String money(int value){return String.format(java.util.Locale.US,"%,d ₸",value).replace(',',' ');}
    public static Color statusBg(kz.qonaq.model.OrderStatus s) {return switch(s){
        case NEW->PURPLE_SOFT;case PREPARING->SOFT_ORANGE;case READY,COMPLETED->SOFT_GREEN;case CANCELLED->SOFT_RED;};}
    public static Color statusColor(kz.qonaq.model.OrderStatus s) {return switch(s){
        case NEW->PURPLE;case PREPARING->c("#BD761F");case READY,COMPLETED->GREEN;case CANCELLED->RED;};}
    public static void applyWindowIcon(JFrame frame){
        java.awt.image.BufferedImage img=new java.awt.image.BufferedImage(64,64,java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g=img.createGraphics();g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(PURPLE);g.fillRoundRect(0,0,64,64,18,18);g.setColor(Color.WHITE);g.setFont(font(37,Font.BOLD));
        g.drawString("Q",14,45);g.dispose();frame.setIconImage(img);
    }
}
