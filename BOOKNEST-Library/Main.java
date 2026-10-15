package kz.teamworks.library;

import kz.teamworks.library.observer.LibraryEventPublisher;
import kz.teamworks.library.service.LibraryFacade;
import kz.teamworks.library.ui.LibraryFrame;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
        catch (Exception ignored) { }
        SwingUtilities.invokeLater(() -> {
            LibraryEventPublisher events = new LibraryEventPublisher();
            LibraryFacade library = new LibraryFacade(events);
            LibraryFrame frame = new LibraryFrame(library, events);
            frame.setVisible(true);
        });
    }
}
