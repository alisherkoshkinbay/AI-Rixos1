package kz.qonaq.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

/** Built-in vector dish artwork. No internet, images, or native fonts required. */
public final class FoodArt extends JComponent {
    private final int variant;
    private static final Color[][] COLORS={
      {Design.c("#FFE9D9"),Design.c("#FFCBAB")},{Design.c("#FFF1DB"),Design.c("#FFE0AF")},
      {Design.c("#E9E9FF"),Design.c("#D7D7FF")},{Design.c("#E6FAEA"),Design.c("#CFF3D7")},
      {Design.c("#FFF0DD"),Design.c("#FFD8BA")},{Design.c("#FFE4DE"),Design.c("#FFCAC4")},
      {Design.c("#E4F7E8"),Design.c("#C7EAD1")},{Design.c("#FFE4EA"),Design.c("#FFD0E0")},
      {Design.c("#F2E8DE"),Design.c("#E7D4C0")},{Design.c("#FBE4EA"),Design.c("#F7CBD8")},
      {Design.c("#EEE5E7"),Design.c("#E4D3D9")},{Design.c("#FFF0DA"),Design.c("#FFE1B8")}
    };
    public FoodArt(int variant){this.variant=Math.floorMod(variant,12);setPreferredSize(new Dimension(220,150));setOpaque(false);}
    private static void oval(Graphics2D g,Color c,double x,double y,double w,double h){g.setColor(c);g.fill(new Ellipse2D.Double(x,y,w,h));}
    private static void rr(Graphics2D g,Color c,double x,double y,double w,double h,double r){g.setColor(c);g.fill(new RoundRectangle2D.Double(x,y,w,h,r,r));}
    @Override protected void paintComponent(Graphics graphics){
        super.paintComponent(graphics);Graphics2D g=(Graphics2D)graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        int w=getWidth(),h=getHeight();g.setPaint(new GradientPaint(0,0,COLORS[variant][0],w,h,COLORS[variant][1]));g.fillRoundRect(0,0,w,h,18,18);
        // soft decorative sparkles
        oval(g,new Color(255,255,255,90),w*.77,-38,150,150);oval(g,new Color(255,255,255,70),-38,h*.55,110,110);
        // Small cart thumbnails render a scaled complete plate, not a cropped central slice.
        if(w<180 || h<120){
            double s=Math.min((w-8)/155.0,(h-8)/120.0);
            s=Math.max(.1,s);
            g.translate((w-225*s)/2.0,(h-150*s)/2.0);
            g.scale(s,s);
        }else{
            g.translate((w-225)/2.0,(h-150)/2.0);
        }
        if(variant<=5){
            oval(g,new Color(76,58,69,29),40,110,155,24);
            oval(g,Color.WHITE,33,15,160,122);
            oval(g,Design.c("#F8F8F8"),46,28,134,96);
        }
        switch(variant) {
            case 0 -> burger(g);case 1 -> pizza(g);case 2 -> pasta(g);case 3 -> bowl(g);
            case 4 -> wings(g);case 5 -> steak(g);case 6 -> matcha(g);case 7 -> lemonade(g);
            case 8 -> coffee(g);case 9 -> cake(g);case 10 -> chocolate(g);case 11 -> waffle(g);
        }
        g.dispose();
    }
    private void burger(Graphics2D g){
        oval(g,Design.c("#C78935"),57,93,117,21);rr(g,Design.c("#F5C96A"),55,88,121,20,12);
        rr(g,Design.c("#6C382B"),52,77,128,21,14);rr(g,Design.c("#F9D254"),51,74,130,8,5);
        rr(g,Design.c("#579F47"),51,68,131,10,5);rr(g,Design.c("#E25C43"),56,63,122,8,4);
        oval(g,Design.c("#E8A84B"),55,35,125,40);oval(g,Design.c("#F7C875"),58,38,119,34);
        for(int i=0;i<10;i++)oval(g,Color.WHITE,76+(i*17)%84,48+(i%3)*7,6,3);
    }
    private void pizza(Graphics2D g){
        oval(g,Design.c("#C58039"),62,18,107,106);oval(g,Design.c("#F8D786"),68,24,95,94);
        oval(g,Design.c("#E65F4D"),75,30,81,80);oval(g,Design.c("#F8E5A2"),80,35,71,69);
        for(int i=0;i<7;i++){double a=i*Math.PI*2/7;oval(g,Design.c("#BC4D49"),111+28*Math.cos(a)-10,70+27*Math.sin(a)-10,18,17);}
        for(int i=0;i<8;i++){double a=i*Math.PI*2/8;oval(g,Design.c("#4E9D5B"),108+37*Math.cos(a)-4,69+34*Math.sin(a)-4,7,12);}
    }
    private void pasta(Graphics2D g){
        oval(g,Design.c("#F2C66D"),66,41,102,68);
        g.setStroke(new BasicStroke(5,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
        for(int i=0;i<8;i++){g.setColor(i%2==0?Design.c("#E9A84B"):Design.c("#F6D483"));
            g.draw(new Arc2D.Double(74+i*8,45+(i%2)*8,56,54,10+i*31,250,Arc2D.OPEN));}
        for(int i=0;i<9;i++)oval(g,Design.c("#448E51"),80+(i*31)%69,47+(i*19)%40,7,5);
    }
    private void bowl(Graphics2D g){
        oval(g,Design.c("#A4C56C"),68,30,98,85);
        for(int i=0;i<12;i++){double a=i*Math.PI/6;oval(g,Design.c(i%3==0?"#ED8655":i%2==0?"#4EAF74":"#BFE1A0"),
            104+31*Math.cos(a)-10,63+24*Math.sin(a)-10,25,22);}
        oval(g,Design.c("#F6B37D"),97,56,35,24);oval(g,Design.c("#FFF2C7"),106,61,13,13);
    }
    private void wings(Graphics2D g){
        for(int i=0;i<4;i++){g.rotate(.18,113,75);oval(g,Design.c("#B96B35"),65+i*14,47+i*5,65,35);
            oval(g,Design.c("#E6A45A"),69+i*14,50+i*5,57,25);}
        oval(g,Design.c("#E6E2D6"),139,94,23,17);oval(g,Design.c("#DE5E42"),144,96,14,11);
    }
    private void steak(Graphics2D g){
        g.rotate(-.2,115,73);oval(g,Design.c("#854234"),65,35,104,75);oval(g,Design.c("#BB6D55"),72,42,90,61);
        g.setStroke(new BasicStroke(4));g.setColor(Design.c("#854234"));for(int i=0;i<4;i++)g.drawLine(85+i*16,52,73+i*19,89);
        oval(g,Design.c("#89B368"),143,35,25,12);
    }
    private void glass(Graphics2D g,Color drink){
        oval(g,new Color(64,75,70,25),82,127,76,10);
        g.setPaint(new GradientPaint(0,25,new Color(255,255,255,230),0,120,new Color(255,255,255,105)));
        g.fill(new RoundRectangle2D.Double(82,26,73,106,16,16));
        rr(g,drink,88,51,61,74,10);rr(g,new Color(255,255,255,140),91,48,55,12,8);
        g.setStroke(new BasicStroke(4));g.setColor(Design.c("#9C9CD2"));g.drawLine(131,19,143,70);
        oval(g,new Color(255,255,255,170),96,68,13,15);oval(g,new Color(255,255,255,150),122,80,12,13);
    }
    private void matcha(Graphics2D g){glass(g,Design.c("#80BC83"));}
    private void lemonade(Graphics2D g){glass(g,Design.c("#E7939C"));oval(g,Design.c("#F8C45B"),146,45,24,24);}
    private void coffee(Graphics2D g){
        oval(g,new Color(60,35,20,28),77,119,85,13);rr(g,Color.WHITE,79,37,74,79,16);
        g.setStroke(new BasicStroke(10));g.setColor(Color.WHITE);g.drawArc(136,55,39,47,-85,178);
        rr(g,Design.c("#C78C60"),85,56,61,55,8);oval(g,Design.c("#F7E8C5"),91,54,48,14);
        g.setColor(Design.c("#E6BB8E"));g.setStroke(new BasicStroke(3));g.drawArc(103,54,27,13,180,175);
    }
    private void cake(Graphics2D g){
        oval(g,new Color(76,58,69,25),65,114,105,17);
        g.setColor(Design.c("#E9BEA5"));Polygon p=new Polygon(new int[]{72,159,132},new int[]{92,90,119},3);g.fillPolygon(p);
        g.setColor(Design.c("#FFFAEE"));Polygon q=new Polygon(new int[]{70,155,127},new int[]{76,73,107},3);g.fillPolygon(q);
        g.setColor(Design.c("#D36B85"));g.fillOval(94,61,25,20);oval(g,Design.c("#F6A0B6"),100,65,9,8);
    }
    private void chocolate(Graphics2D g){
        oval(g,new Color(76,58,69,25),67,121,105,13);
        rr(g,Design.c("#623E3E"),78,55,82,61,12);rr(g,Design.c("#8C5152"),83,58,72,51,11);
        rr(g,Design.c("#402B2E"),93,64,56,11,7);rr(g,Design.c("#452A2E"),104,73,12,38,5);
        oval(g,Design.c("#FBF3E9"),131,36,39,40);
    }
    private void waffle(Graphics2D g){
        g.rotate(-.13,114,83);rr(g,Design.c("#B97938"),70,41,97,78,16);rr(g,Design.c("#E9B36F"),75,46,87,66,14);
        g.setColor(Design.c("#BE864E"));g.setStroke(new BasicStroke(3));
        for(int i=0;i<4;i++){g.drawLine(79+i*20,48,79+i*20,111);g.drawLine(76,58+i*17,161,58+i*17);}
        oval(g,Design.c("#EB6A7C"),85,44,27,22);oval(g,Design.c("#C64562"),127,77,29,23);
    }
}
