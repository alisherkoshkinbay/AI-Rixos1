package kz.qonaq.tests;

import kz.qonaq.patterns.facade.RestaurantFacade;
import kz.qonaq.ui.RestaurantFrame;
import kz.qonaq.ui.CheckoutDialog;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.*;

/** Manual preview harness; not part of the production runtime. */
public final class UiCapture {
    public static void main(String[] args) throws Exception {
        Path docs=Path.of(args.length>0?args[0]:"docs");Files.createDirectories(docs);
        final RestaurantFrame[] f={null};
        final RestaurantFacade[] service={null};
        SwingUtilities.invokeAndWait(()->{
            service[0]=new RestaurantFacade(Path.of(System.getProperty("java.io.tmpdir"),"qonaq-preview-"+ProcessHandle.current().pid(),"orders.txt"));
            f[0]=new RestaurantFrame(service[0]);
            f[0].setSize(1460,900);f[0].setLocation(65,45);f[0].setVisible(true);
        });
        Robot robot=new Robot();robot.setAutoDelay(100);
        for(String page:new String[]{"dashboard","menu","orders","analytics","patterns"}){
            SwingUtilities.invokeAndWait(()->f[0].navigate(page));
            Thread.sleep(800);
            BufferedImage img=robot.createScreenCapture(new Rectangle(f[0].getLocationOnScreen(),f[0].getSize()));
            Path out=docs.resolve("preview-"+page+".png");ImageIO.write(img,"png",out.toFile());
            System.out.println(out);
        }
        SwingUtilities.invokeAndWait(()->{
            service[0].addToCart("F01",2);service[0].addToCart("D01",1);
            service[0].addToCart("S01",1);service[0].setPromo("QONAQ10");
            f[0].navigate("menu");
        });
        Thread.sleep(800);
        ImageIO.write(robot.createScreenCapture(new Rectangle(f[0].getLocationOnScreen(),f[0].getSize())),
            "png",docs.resolve("preview-cart.png").toFile());
        SwingUtilities.invokeLater(()->new CheckoutDialog(f[0],service[0],()->{}).setVisible(true));
        Thread.sleep(950);
        for(Window window:Window.getWindows()) if(window instanceof JDialog dialog && dialog.isShowing()) {
            ImageIO.write(robot.createScreenCapture(new Rectangle(dialog.getLocationOnScreen(),dialog.getSize())),
                "png",docs.resolve("preview-checkout.png").toFile());
            SwingUtilities.invokeLater(dialog::dispose);
        }
        SwingUtilities.invokeAndWait(()->f[0].dispose());
    }
}
