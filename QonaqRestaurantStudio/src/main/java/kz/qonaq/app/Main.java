package kz.qonaq.app;

import kz.qonaq.patterns.facade.RestaurantFacade;
import kz.qonaq.ui.RestaurantFrame;
import javax.swing.*;
import java.nio.file.Path;

public final class Main {
    public static void main(String[] args) {
        System.setProperty("awt.useSystemAAFontSettings","on");
        System.setProperty("swing.aatext","true");
        SwingUtilities.invokeLater(()->{
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                Path storage=Path.of(System.getProperty("user.home"),".qonaq-restaurant-studio","orders.txt");
                new RestaurantFrame(new RestaurantFacade(storage)).setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Бағдарламаны ашу қатесі: "+ex.getMessage(), "QONAQ", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
