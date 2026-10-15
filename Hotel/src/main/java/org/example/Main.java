import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.List;

// ============================================================
// AI RIXOS HOTEL
// Hotel Reservation Management System
//
// Design Patterns:
// 1. Abstract Factory
// 2. State
// 3. Strategy
// 4. Observer
// ============================================================

public class Main extends JFrame {

    // ==================== RIXOS COLOR PALETTE ====================

    private static final Color TEAL = new Color(0, 127, 134);
    private static final Color TEAL_DARK = new Color(0, 99, 105);
    private static final Color TEAL_LIGHT = new Color(224, 243, 241);

    private static final Color GOLD = new Color(198, 161, 91);
    private static final Color GOLD_LIGHT = new Color(246, 239, 222);

    private static final Color BACKGROUND = new Color(247, 245, 240);
    private static final Color WHITE = Color.WHITE;

    private static final Color TEXT = new Color(40, 52, 54);
    private static final Color TEXT_LIGHT = new Color(112, 124, 124);
    private static final Color BORDER = new Color(228, 229, 224);

    private static final Color RED = new Color(187, 75, 75);
    private static final Color RED_LIGHT = new Color(250, 235, 233);

    private static final Color GREEN = new Color(47, 126, 92);
    private static final Color GREEN_LIGHT = new Color(230, 244, 235);

    private static final Font FONT = new Font("SansSerif", Font.PLAIN, 13);
    private static final Font FONT_BOLD = new Font("SansSerif", Font.BOLD, 13);

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE;

    // ==================== APPLICATION DATA ====================

    private final RoomInventory roomInventory = new RoomInventory();
    private final BookingNotifier notifier = new BookingNotifier();
    private final BookingService bookingService =
            new BookingService(roomInventory, notifier);

    private final List<Booking> bookings = new ArrayList<>();

    private JTextField guestField;
    private JTextField checkInField;
    private JTextField checkOutField;

    private JComboBox<HotelRoom> roomCombo;
    private JComboBox<String> strategyCombo;

    private JLabel roomInfoLabel;
    private JLabel priceLabel;

    private JLabel totalBookingsValue;
    private JLabel activeBookingsValue;
    private JLabel revenueValue;

    private JTable bookingTable;
    private DefaultTableModel tableModel;

    private JTextArea logArea;

    private JButton calculateButton;
    private JButton confirmButton;
    private JButton cancelButton;

    // ==================== CONSTRUCTOR ====================

    public Main() {
        super("AI Rixos Hotel | Reservation Management");

        initializeData();
        initializeInterface();
        refreshTable();
        refreshDashboard();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 760));
        setSize(1250, 850);
        setLocationRelativeTo(null);
    }

    private void initializeData() {

        notifier.addObserver(new GuestObserver());
        notifier.addObserver(new AdminObserver());
        notifier.addObserver(new UIObserver());
    }

    private void initializeInterface() {

        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND);

        JPanel root = new JPanel(new BorderLayout(0, 0));
        root.setBackground(BACKGROUND);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createMainContent(), BorderLayout.CENTER);

        add(root, BorderLayout.CENTER);
    }

    // ==================== HEADER ====================

    private JPanel createHeader() {

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(WHITE);

        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER),
                new EmptyBorder(20, 28, 20, 28)
        ));

        JPanel brandPanel = new JPanel();
        brandPanel.setOpaque(false);
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("AI RIXOS HOTEL");
        brand.setFont(new Font("SansSerif", Font.BOLD, 25));
        brand.setForeground(TEAL);

        JLabel subtitle = new JLabel(
                "LUXURY HOSPITALITY MANAGEMENT SYSTEM"
        );
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
        subtitle.setForeground(TEXT_LIGHT);

        brandPanel.add(brand);
        brandPanel.add(Box.createVerticalStrut(5));
        brandPanel.add(subtitle);

        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        JLabel status = new JLabel("● SYSTEM ONLINE");
        status.setFont(FONT_BOLD);
        status.setForeground(GREEN);
        status.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel date = new JLabel(LocalDate.now().toString());
        date.setFont(FONT);
        date.setForeground(TEXT_LIGHT);
        date.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(status);
        rightPanel.add(Box.createVerticalStrut(6));
        rightPanel.add(date);

        header.add(brandPanel, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    // ==================== MAIN CONTENT ====================

    private JPanel createMainContent() {

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setBackground(BACKGROUND);

        content.setBorder(new EmptyBorder(22, 28, 24, 28));

        content.add(createWelcomePanel(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(18, 0));
        center.setBackground(BACKGROUND);

        center.add(createLeftPanel(), BorderLayout.WEST);
        center.add(createRightPanel(), BorderLayout.CENTER);

        content.add(center, BorderLayout.CENTER);

        return content;
    }

    // ==================== WELCOME PANEL ====================

    private JPanel createWelcomePanel() {

        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(BACKGROUND);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Reservation Management");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(TEXT);

        JLabel description = new JLabel(
                "Manage rooms, guest reservations and hotel operations."
        );
        description.setFont(FONT);
        description.setForeground(TEXT_LIGHT);

        left.add(title);
        left.add(Box.createVerticalStrut(6));
        left.add(description);

        JPanel badge = new JPanel(new FlowLayout(
                FlowLayout.CENTER, 12, 8
        ));

        badge.setBackground(GOLD_LIGHT);
        badge.setBorder(new CompoundBorder(
                new LineBorder(new Color(233, 220, 188), 1, true),
                new EmptyBorder(0, 5, 0, 5)
        ));

        JLabel badgeText = new JLabel("✦  PREMIUM SERVICE");
        badgeText.setForeground(TEAL_DARK);
        badgeText.setFont(FONT_BOLD);

        badge.add(badgeText);

        panel.add(left, BorderLayout.WEST);
        panel.add(badge, BorderLayout.EAST);

        return panel;
    }

    // ==================== LEFT PANEL ====================

    private JPanel createLeftPanel() {

        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND);

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(345, 600));

        panel.add(createBookingForm());
        panel.add(Box.createVerticalStrut(16));
        panel.add(createRoomInfoPanel());

        return panel;
    }

    // ==================== BOOKING FORM ====================

    private JPanel createBookingForm() {

        JPanel panel = createCardPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel title = new JLabel("New Reservation");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel description = new JLabel(
                "Enter guest and booking details"
        );
        description.setFont(FONT);
        description.setForeground(TEXT_LIGHT);
        description.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(5));
        panel.add(description);
        panel.add(Box.createVerticalStrut(20));

        // Guest name
        panel.add(createFieldLabel("GUEST FULL NAME"));

        guestField = createTextField();
        guestField.setToolTipText("Enter the guest's full name");

        panel.add(guestField);
        panel.add(Box.createVerticalStrut(14));

        // Room selection
        panel.add(createFieldLabel("ROOM CATEGORY"));

        roomCombo = new JComboBox<>(
                roomInventory.getRooms().toArray(new HotelRoom[0])
        );

        roomCombo.setFont(FONT);
        roomCombo.setBackground(WHITE);
        roomCombo.setForeground(TEXT);
        roomCombo.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 39)
        );
        roomCombo.setPreferredSize(new Dimension(280, 39));
        roomCombo.setBorder(new LineBorder(BORDER, 1, true));

        roomCombo.addActionListener(e -> updateRoomInfo());

        panel.add(roomCombo);
        panel.add(Box.createVerticalStrut(14));

        // Check-in
        panel.add(createFieldLabel("CHECK-IN DATE"));

        checkInField = createTextField();
        checkInField.setText(LocalDate.now().toString());

        panel.add(checkInField);
        panel.add(Box.createVerticalStrut(14));

        // Check-out
        panel.add(createFieldLabel("CHECK-OUT DATE"));

        checkOutField = createTextField();
        checkOutField.setText(LocalDate.now().plusDays(1).toString());

        panel.add(checkOutField);
        panel.add(Box.createVerticalStrut(14));

        // Pricing strategy
        panel.add(createFieldLabel("PRICING PLAN"));

        strategyCombo = new JComboBox<>(new String[]{
                "Standard Rate",
                "Seasonal Rate (+20%)",
                "VIP Discount (-15%)"
        });

        strategyCombo.setFont(FONT);
        strategyCombo.setBackground(WHITE);
        strategyCombo.setForeground(TEXT);
        strategyCombo.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 39)
        );
        strategyCombo.setPreferredSize(new Dimension(280, 39));
        strategyCombo.setBorder(new LineBorder(BORDER, 1, true));

        panel.add(strategyCombo);
        panel.add(Box.createVerticalStrut(18));

        // Price preview
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.setBackground(TEAL_LIGHT);

        pricePanel.setBorder(new CompoundBorder(
                new LineBorder(new Color(195, 225, 221), 1, true),
                new EmptyBorder(13, 14, 13, 14)
        ));

        JLabel priceTitle = new JLabel("ESTIMATED TOTAL");
        priceTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        priceTitle.setForeground(TEAL_DARK);

        priceLabel = new JLabel("0 KZT");
        priceLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        priceLabel.setForeground(TEAL_DARK);

        pricePanel.add(priceTitle, BorderLayout.NORTH);
        pricePanel.add(priceLabel, BorderLayout.CENTER);

        pricePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pricePanel.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 72)
        );

        panel.add(pricePanel);
        panel.add(Box.createVerticalStrut(16));

        // Calculate button
        calculateButton = createButton(
                "Calculate Price",
                WHITE,
                TEAL,
                false
        );

        calculateButton.addActionListener(
                e -> calculatePrice()
        );

        panel.add(calculateButton);
        panel.add(Box.createVerticalStrut(9));

        // Confirm booking button
        confirmButton = createButton(
                "Confirm Reservation",
                WHITE,
                TEAL,
                true
        );

        confirmButton.addActionListener(
                e -> createBooking()
        );

        panel.add(confirmButton);
        panel.add(Box.createVerticalStrut(9));

        // Cancel booking button
        cancelButton = createButton(
                "Cancel Selected Booking",
                RED,
                WHITE,
                false
        );

        cancelButton.setBorder(new CompoundBorder(
                new LineBorder(new Color(239, 204, 201), 1, true),
                new EmptyBorder(10, 8, 10, 8)
        ));

        cancelButton.addActionListener(
                e -> cancelSelectedBooking()
        );

        panel.add(cancelButton);

        return panel;
    }

    // ==================== ROOM INFORMATION ====================

    private JPanel createRoomInfoPanel() {

        JPanel panel = createCardPanel();

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(18, 20, 18, 20)
        ));

        JLabel title = new JLabel("Selected Room");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        title.setForeground(TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        roomInfoLabel = new JLabel();
        roomInfoLabel.setFont(FONT);
        roomInfoLabel.setForeground(TEXT_LIGHT);
        roomInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(12));
        panel.add(roomInfoLabel);

        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    private void updateRoomInfo() {

        HotelRoom room = getSelectedRoom();

        if (room == null || roomInfoLabel == null) {
            return;
        }

        roomInfoLabel.setText(
                "<html>"
                        + "<b style='color:#007F86;'>"
                        + room.getCategory()
                        + "</b><br><br>"
                        + "Room number: " + room.getRoomNumber()
                        + "<br>"
                        + "Rate per night: "
                        + formatMoney(room.getPrice())
                        + "<br><br>"
                        + "<span style='color:#778184;'>"
                        + room.getDescription()
                        + "</span>"
                        + "</html>"
        );
    }

    // ==================== RIGHT PANEL ====================

    private JPanel createRightPanel() {

        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(BACKGROUND);

        panel.add(createDashboard(), BorderLayout.NORTH);

        JPanel lower = new JPanel(new BorderLayout(0, 16));
        lower.setBackground(BACKGROUND);

        lower.add(createBookingsPanel(), BorderLayout.CENTER);
        lower.add(createActivityPanel(), BorderLayout.SOUTH);

        panel.add(lower, BorderLayout.CENTER);

        return panel;
    }

    // ==================== DASHBOARD ====================

    private JPanel createDashboard() {

        JPanel dashboard = new JPanel(
                new GridLayout(1, 3, 12, 0)
        );

        dashboard.setBackground(BACKGROUND);

        totalBookingsValue = new JLabel("0");
        activeBookingsValue = new JLabel("0");
        revenueValue = new JLabel("0 KZT");

        dashboard.add(createStatCard(
                "TOTAL BOOKINGS",
                totalBookingsValue,
                "All reservations",
                TEAL
        ));

        dashboard.add(createStatCard(
                "ACTIVE BOOKINGS",
                activeBookingsValue,
                "Pending and confirmed",
                GOLD
        ));

        dashboard.add(createStatCard(
                "CONFIRMED REVENUE",
                revenueValue,
                "Before check-out",
                GREEN
        ));

        return dashboard;
    }

    private JPanel createStatCard(
            String title,
            JLabel value,
            String description,
            Color accent
    ) {

        JPanel card = createCardPanel();

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(17, 15, 17, 15)
        ));

        JPanel accentBar = new JPanel();
        accentBar.setBackground(accent);
        accentBar.setMaximumSize(new Dimension(
                Integer.MAX_VALUE, 4
        ));
        accentBar.setPreferredSize(new Dimension(100, 4));
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 10));
        titleLabel.setForeground(TEXT_LIGHT);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        value.setFont(new Font("SansSerif", Font.BOLD, 20));
        value.setForeground(TEXT);
        value.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel desc = new JLabel(description);
        desc.setFont(new Font("SansSerif", Font.PLAIN, 11));
        desc.setForeground(TEXT_LIGHT);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(accentBar);
        card.add(Box.createVerticalStrut(13));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(value);
        card.add(Box.createVerticalStrut(6));
        card.add(desc);

        return card;
    }

    // ==================== BOOKINGS TABLE ====================

    private JPanel createBookingsPanel() {

        JPanel panel = createCardPanel();

        panel.setLayout(new BorderLayout(0, 14));

        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Reservations");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(TEXT);

        JLabel subtitle = new JLabel("Manage all hotel bookings");
        subtitle.setFont(FONT);
        subtitle.setForeground(TEXT_LIGHT);

        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));

        titles.add(title);
        titles.add(Box.createVerticalStrut(5));
        titles.add(subtitle);

        header.add(titles, BorderLayout.WEST);

        String[] columns = {
                "Booking ID",
                "Guest",
                "Room",
                "Check-in",
                "Check-out",
                "Total (KZT)",
                "Status"
        };

        tableModel = new DefaultTableModel(columns, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        bookingTable = new JTable(tableModel);

        bookingTable.setFont(FONT);
        bookingTable.setForeground(TEXT);
        bookingTable.setBackground(WHITE);

        bookingTable.setRowHeight(35);
        bookingTable.setShowVerticalLines(false);
        bookingTable.setShowHorizontalLines(true);

        bookingTable.setGridColor(BORDER);
        bookingTable.setSelectionBackground(TEAL_LIGHT);
        bookingTable.setSelectionForeground(TEXT);

        bookingTable.setFillsViewportHeight(true);
        bookingTable.setAutoCreateRowSorter(true);
        bookingTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        JTableHeader tableHeader = bookingTable.getTableHeader();

        tableHeader.setFont(
                new Font("SansSerif", Font.BOLD, 11)
        );

        tableHeader.setBackground(
                new Color(241, 243, 239)
        );

        tableHeader.setForeground(TEAL_DARK);
        tableHeader.setPreferredSize(new Dimension(100, 38));
        tableHeader.setBorder(
                new MatteBorder(0, 0, 1, 0, BORDER)
        );

        // Status column renderer
        bookingTable.getColumnModel()
                .getColumn(6)
                .setCellRenderer(new StatusRenderer());

        // Column widths
        bookingTable.getColumnModel()
                .getColumn(0).setPreferredWidth(100);

        bookingTable.getColumnModel()
                .getColumn(1).setPreferredWidth(125);

        bookingTable.getColumnModel()
                .getColumn(2).setPreferredWidth(65);

        bookingTable.getColumnModel()
                .getColumn(3).setPreferredWidth(90);

        bookingTable.getColumnModel()
                .getColumn(4).setPreferredWidth(90);

        bookingTable.getColumnModel()
                .getColumn(5).setPreferredWidth(100);

        bookingTable.getColumnModel()
                .getColumn(6).setPreferredWidth(90);

        JScrollPane scrollPane = new JScrollPane(bookingTable);

        scrollPane.setBorder(
                new LineBorder(BORDER, 1, true)
        );

        scrollPane.getViewport().setBackground(WHITE);

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ==================== STATUS RENDERER ====================

    private static class StatusRenderer
            extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table,
                Object value,
                boolean isSelected,
                boolean hasFocus,
                int row,
                int column
        ) {

            JLabel label = (JLabel) super
                    .getTableCellRendererComponent(
                            table,
                            value,
                            isSelected,
                            hasFocus,
                            row,
                            column
                    );

            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("SansSerif", Font.BOLD, 11));

            if (!isSelected) {

                String status = value == null
                        ? ""
                        : value.toString();

                switch (status) {

                    case "CONFIRMED":
                        label.setForeground(GREEN);
                        label.setBackground(GREEN_LIGHT);
                        break;

                    case "CANCELLED":
                        label.setForeground(RED);
                        label.setBackground(RED_LIGHT);
                        break;

                    default:
                        label.setForeground(TEAL_DARK);
                        label.setBackground(TEAL_LIGHT);
                        break;
                }

            } else {

                label.setBackground(TEAL_LIGHT);
                label.setForeground(TEAL_DARK);
            }

            return label;
        }
    }

    // ==================== ACTIVITY LOG ====================

    private JPanel createActivityPanel() {

        JPanel panel = createCardPanel();

        panel.setLayout(new BorderLayout(0, 10));

        panel.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(15, 18, 15, 18)
        ));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel title = new JLabel("Activity Log");
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        title.setForeground(TEXT);

        JLabel indicator = new JLabel("● LIVE EVENTS");
        indicator.setFont(new Font("SansSerif", Font.BOLD, 10));
        indicator.setForeground(TEAL);

        header.add(title, BorderLayout.WEST);
        header.add(indicator, BorderLayout.EAST);

        logArea = new JTextArea(5, 30);

        logArea.setEditable(false);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        logArea.setForeground(TEAL_DARK);
        logArea.setBackground(new Color(249, 250, 247));

        logArea.setBorder(
                new EmptyBorder(10, 10, 10, 10)
        );

        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logArea);

        scrollPane.setBorder(
                new LineBorder(BORDER, 1, true)
        );

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // ==================== PRICE CALCULATION ====================

    private void calculatePrice() {

        try {

            HotelRoom room = getSelectedRoom();

            if (room == null) {
                showError("Please select a room.");
                return;
            }

            LocalDate checkIn = parseDate(checkInField.getText());
            LocalDate checkOut = parseDate(checkOutField.getText());

            validateDates(checkIn, checkOut);

            long nights = ChronoUnit.DAYS.between(
                    checkIn, checkOut
            );

            PriceStrategy strategy = getSelectedStrategy();

            double total = strategy.calculatePrice(
                    room.getPrice(),
                    nights
            );

            priceLabel.setText(formatMoney(total));

        } catch (IllegalArgumentException ex) {

            showError(ex.getMessage());

        } catch (Exception ex) {

            showError(
                    "Could not calculate price. Check the entered dates."
            );
        }
    }

    // ==================== CREATE BOOKING ====================

    private void createBooking() {

        try {

            String guestName = guestField.getText().trim();

            HotelRoom room = getSelectedRoom();

            if (room == null) {
                showError("Please select a room.");
                return;
            }

            LocalDate checkIn = parseDate(checkInField.getText());
            LocalDate checkOut = parseDate(checkOutField.getText());

            validateDates(checkIn, checkOut);

            PriceStrategy strategy = getSelectedStrategy();

            Booking booking = bookingService.createBooking(
                    guestName,
                    room,
                    checkIn,
                    checkOut,
                    strategy
            );

            bookings.add(booking);

            refreshTable();
            refreshDashboard();
            updateRoomInfo();

            priceLabel.setText(formatMoney(booking.getTotalPrice()));

            JOptionPane.showMessageDialog(
                    this,
                    "Reservation created successfully!\n\n"
                            + "Booking ID: " + booking.getId() + "\n"
                            + "Guest: " + booking.getGuestName() + "\n"
                            + "Room: " + booking.getRoom().getRoomNumber() + "\n"
                            + "Total: " + formatMoney(booking.getTotalPrice()),
                    "Reservation Successful",
                    JOptionPane.INFORMATION_MESSAGE
            );

            guestField.setText("");

        } catch (IllegalArgumentException ex) {

            showError(ex.getMessage());

        } catch (Exception ex) {

            showError("Could not create reservation.");
        }
    }

    // ==================== CANCEL BOOKING ====================

    private void cancelSelectedBooking() {

        int selectedRow = bookingTable.getSelectedRow();

        if (selectedRow == -1) {

            showError(
                    "Please select a reservation from the table first."
            );

            return;
        }

        int modelRow = bookingTable.convertRowIndexToModel(
                selectedRow
        );

        String bookingId = tableModel
                .getValueAt(modelRow, 0)
                .toString();

        Booking booking = bookingService.findBooking(bookingId);

        if (booking == null) {

            showError("Reservation not found.");
            return;
        }

        if (booking.getState() instanceof CancelledState) {

            showError("This reservation has already been cancelled.");
            return;
        }

        int answer = JOptionPane.showConfirmDialog(
                this,
                "Cancel reservation " + booking.getId() + "?\n"
                        + "Guest: " + booking.getGuestName(),
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {

            bookingService.cancelBooking(booking);

            refreshTable();
            refreshDashboard();
            updateRoomInfo();

            JOptionPane.showMessageDialog(
                    this,
                    "Reservation " + booking.getId()
                            + " has been cancelled.",
                    "Reservation Cancelled",
                    JOptionPane.INFORMATION_MESSAGE
            );

        } catch (IllegalStateException ex) {

            showError(ex.getMessage());
        }
    }

    // ==================== REFRESH TABLE ====================

    private void refreshTable() {

        if (tableModel == null) {
            return;
        }

        tableModel.setRowCount(0);

        for (Booking booking : bookings) {

            tableModel.addRow(new Object[]{
                    booking.getId(),
                    booking.getGuestName(),
                    booking.getRoom().getRoomNumber(),
                    booking.getCheckIn(),
                    booking.getCheckOut(),
                    String.format(
                            Locale.US,
                            "%,.0f",
                            booking.getTotalPrice()
                    ),
                    booking.getState().getStatus()
            });
        }
    }

    // ==================== REFRESH DASHBOARD ====================

    private void refreshDashboard() {

        if (totalBookingsValue == null) {
            return;
        }

        int active = 0;
        double revenue = 0;

        LocalDate today = LocalDate.now();

        for (Booking booking : bookings) {

            if (booking.getState() instanceof CancelledState) {
                continue;
            }

            active++;

            // Count confirmed bookings that have not checked out.
            if (booking.getState() instanceof ConfirmedState
                    && booking.getCheckOut().isAfter(today)) {

                revenue += booking.getTotalPrice();
            }
        }

        totalBookingsValue.setText(
                String.valueOf(bookings.size())
        );

        activeBookingsValue.setText(
                String.valueOf(active)
        );

        revenueValue.setText(formatMoney(revenue));
    }

    // ==================== INPUT HELPERS ====================

    private HotelRoom getSelectedRoom() {

        return (HotelRoom) roomCombo.getSelectedItem();
    }

    private PriceStrategy getSelectedStrategy() {

        int index = strategyCombo.getSelectedIndex();

        switch (index) {

            case 1:
                return new SeasonalPriceStrategy();

            case 2:
                return new VIPDiscountStrategy();

            default:
                return new StandardPriceStrategy();
        }
    }

    private LocalDate parseDate(String input) {

        try {

            return LocalDate.parse(
                    input.trim(),
                    DATE_FORMAT
            );

        } catch (DateTimeParseException ex) {

            throw new IllegalArgumentException(
                    "Enter dates in YYYY-MM-DD format."
            );
        }
    }

    private void validateDates(
            LocalDate checkIn,
            LocalDate checkOut
    ) {

        if (checkIn.isBefore(LocalDate.now())) {

            throw new IllegalArgumentException(
                    "Check-in date cannot be in the past."
            );
        }

        if (!checkOut.isAfter(checkIn)) {

            throw new IllegalArgumentException(
                    "Check-out must be after check-in."
            );
        }
    }

    private String formatMoney(double amount) {

        return String.format(
                Locale.US,
                "%,.0f KZT",
                amount
        );
    }

    private void showError(String message) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Notice",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // ==================== UI COMPONENT HELPERS ====================

    private JPanel createCardPanel() {

        JPanel panel = new JPanel();

        panel.setBackground(WHITE);

        panel.setBorder(new LineBorder(
                BORDER, 1, true
        ));

        return panel;
    }

    private JLabel createFieldLabel(String text) {

        JLabel label = new JLabel(text);

        label.setFont(new Font(
                "SansSerif", Font.BOLD, 10
        ));

        label.setForeground(TEXT_LIGHT);

        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        label.setBorder(
                new EmptyBorder(0, 0, 7, 0)
        );

        return label;
    }

    private JTextField createTextField() {

        JTextField field = new JTextField();

        field.setFont(FONT);
        field.setForeground(TEXT);
        field.setBackground(WHITE);

        field.setCaretColor(TEAL);

        field.setBorder(new CompoundBorder(
                new LineBorder(BORDER, 1, true),
                new EmptyBorder(9, 10, 9, 10)
        ));

        field.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 39)
        );

        field.setPreferredSize(
                new Dimension(280, 39)
        );

        return field;
    }

    private JButton createButton(
            String text,
            Color foreground,
            Color background,
            boolean primary
    ) {

        JButton button = new JButton(text);

        button.setFont(
                new Font("SansSerif", Font.BOLD, 12)
        );

        button.setForeground(foreground);
        button.setBackground(background);

        button.setFocusPainted(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );

        button.setOpaque(true);
        button.setContentAreaFilled(true);

        if (primary) {

            button.setBorder(new CompoundBorder(
                    new LineBorder(TEAL, 1, true),
                    new EmptyBorder(11, 8, 11, 8)
            ));

        } else {

            button.setBorder(new CompoundBorder(
                    new LineBorder(BORDER, 1, true),
                    new EmptyBorder(10, 8, 10, 8)
            ));
        }

        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 42)
        );

        button.setPreferredSize(
                new Dimension(280, 42)
        );

        return button;
    }

    // ============================================================
    // PATTERN 1: ABSTRACT FACTORY
    // Responsible for creating hotel room objects.
    // ============================================================

    interface HotelRoom {

        String getRoomNumber();

        String getCategory();

        double getPrice();

        String getDescription();
    }

    abstract static class BaseHotelRoom implements HotelRoom {

        private final String roomNumber;
        private final String category;
        private final double price;
        private final String description;

        public BaseHotelRoom(
                String roomNumber,
                String category,
                double price,
                String description
        ) {

            this.roomNumber = roomNumber;
            this.category = category;
            this.price = price;
            this.description = description;
        }

        @Override
        public String getRoomNumber() {
            return roomNumber;
        }

        @Override
        public String getCategory() {
            return category;
        }

        @Override
        public double getPrice() {
            return price;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {

            return roomNumber + " | " + category
                    + " | " + String.format(
                    Locale.US, "%,.0f KZT/night", price
            );
        }
    }

    static class StandardRoom extends BaseHotelRoom {

        public StandardRoom(String number) {

            super(
                    number,
                    "Standard",
                    35000,
                    "Comfortable room with essential amenities."
            );
        }
    }

    static class DeluxeRoom extends BaseHotelRoom {

        public DeluxeRoom(String number) {

            super(
                    number,
                    "Deluxe",
                    65000,
                    "Spacious room with premium amenities."
            );
        }
    }

    static class SuiteRoom extends BaseHotelRoom {

        public SuiteRoom(String number) {

            super(
                    number,
                    "Suite",
                    120000,
                    "Luxury suite with an exclusive interior."
            );
        }
    }

    interface RoomFactory {

        HotelRoom createRoom(String number);
    }

    static class StandardRoomFactory implements RoomFactory {

        @Override
        public HotelRoom createRoom(String number) {

            return new StandardRoom(number);
        }
    }

    static class DeluxeRoomFactory implements RoomFactory {

        @Override
        public HotelRoom createRoom(String number) {

            return new DeluxeRoom(number);
        }
    }

    static class SuiteRoomFactory implements RoomFactory {

        @Override
        public HotelRoom createRoom(String number) {

            return new SuiteRoom(number);
        }
    }

    static class RoomInventory {

        private final List<HotelRoom> rooms = new ArrayList<>();

        public RoomInventory() {

            RoomFactory standardFactory =
                    new StandardRoomFactory();

            RoomFactory deluxeFactory =
                    new DeluxeRoomFactory();

            RoomFactory suiteFactory =
                    new SuiteRoomFactory();

            rooms.add(standardFactory.createRoom("101"));
            rooms.add(standardFactory.createRoom("102"));
            rooms.add(standardFactory.createRoom("103"));

            rooms.add(deluxeFactory.createRoom("201"));
            rooms.add(deluxeFactory.createRoom("202"));
            rooms.add(deluxeFactory.createRoom("203"));

            rooms.add(suiteFactory.createRoom("301"));
            rooms.add(suiteFactory.createRoom("302"));
        }

        public List<HotelRoom> getRooms() {

            return Collections.unmodifiableList(rooms);
        }
    }

    // ============================================================
    // PATTERN 2: STATE
    // Booking behavior depends on its current state.
    // ============================================================

    interface BookingState {

        String getStatus();

        void confirm(Booking booking);

        void cancel(Booking booking);
    }

    static class PendingState implements BookingState {

        @Override
        public String getStatus() {
            return "PENDING";
        }

        @Override
        public void confirm(Booking booking) {

            booking.setState(new ConfirmedState());
        }

        @Override
        public void cancel(Booking booking) {

            booking.setState(new CancelledState());
        }
    }

    static class ConfirmedState implements BookingState {

        @Override
        public String getStatus() {
            return "CONFIRMED";
        }

        @Override
        public void confirm(Booking booking) {

            // Already confirmed.
        }

        @Override
        public void cancel(Booking booking) {

            booking.setState(new CancelledState());
        }
    }

    static class CancelledState implements BookingState {

        @Override
        public String getStatus() {
            return "CANCELLED";
        }

        @Override
        public void confirm(Booking booking) {

            throw new IllegalStateException(
                    "A cancelled reservation cannot be confirmed."
            );
        }

        @Override
        public void cancel(Booking booking) {

            throw new IllegalStateException(
                    "Reservation is already cancelled."
            );
        }
    }

    static class Booking {

        private final String id;
        private final String guestName;
        private final HotelRoom room;
        private final LocalDate checkIn;
        private final LocalDate checkOut;
        private final double totalPrice;

        private BookingState state;

        public Booking(
                String id,
                String guestName,
                HotelRoom room,
                LocalDate checkIn,
                LocalDate checkOut,
                double totalPrice
        ) {

            this.id = id;
            this.guestName = guestName;
            this.room = room;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.totalPrice = totalPrice;

            this.state = new PendingState();
        }

        public void confirm() {

            state.confirm(this);
        }

        public void cancel() {

            state.cancel(this);
        }

        public void setState(BookingState state) {

            this.state = state;
        }

        public String getId() {
            return id;
        }

        public String getGuestName() {
            return guestName;
        }

        public HotelRoom getRoom() {
            return room;
        }

        public LocalDate getCheckIn() {
            return checkIn;
        }

        public LocalDate getCheckOut() {
            return checkOut;
        }

        public double getTotalPrice() {
            return totalPrice;
        }

        public BookingState getState() {
            return state;
        }
    }

    // ============================================================
    // PATTERN 3: STRATEGY
    // Different pricing algorithms can be selected at runtime.
    // ============================================================

    interface PriceStrategy {

        double calculatePrice(double pricePerNight, long nights);
    }

    static class StandardPriceStrategy implements PriceStrategy {

        @Override
        public double calculatePrice(
                double pricePerNight,
                long nights
        ) {

            return pricePerNight * nights;
        }
    }

    static class SeasonalPriceStrategy implements PriceStrategy {

        @Override
        public double calculatePrice(
                double pricePerNight,
                long nights
        ) {

            return pricePerNight * nights * 1.20;
        }
    }

    static class VIPDiscountStrategy implements PriceStrategy {

        @Override
        public double calculatePrice(
                double pricePerNight,
                long nights
        ) {

            return pricePerNight * nights * 0.85;
        }
    }

    // ============================================================
    // PATTERN 4: OBSERVER
    // Guest, administrator and UI receive booking events.
    // ============================================================

    interface BookingObserver {

        void update(String event, Booking booking);
    }

    static class BookingNotifier {

        private final List<BookingObserver> observers =
                new ArrayList<>();

        public void addObserver(BookingObserver observer) {

            observers.add(observer);
        }

        public void notifyObservers(
                String event,
                Booking booking
        ) {

            for (BookingObserver observer :
                    new ArrayList<>(observers)) {

                observer.update(event, booking);
            }
        }
    }

    static class GuestObserver implements BookingObserver {

        @Override
        public void update(String event, Booking booking) {

            System.out.println(
                    "[GUEST NOTIFICATION] "
                            + booking.getGuestName()
                            + " | Booking: "
                            + booking.getId()
                            + " | Event: "
                            + event
            );
        }
    }

    static class AdminObserver implements BookingObserver {

        @Override
        public void update(String event, Booking booking) {

            System.out.println(
                    "[ADMIN NOTIFICATION] "
                            + event
                            + " | Booking ID: "
                            + booking.getId()
                            + " | Room: "
                            + booking.getRoom().getRoomNumber()
            );
        }
    }

    class UIObserver implements BookingObserver {

        @Override
        public void update(String event, Booking booking) {

            Runnable task = () -> {

                if (logArea == null) {
                    return;
                }

                String timestamp = java.time.LocalTime.now()
                        .format(
                                DateTimeFormatter.ofPattern("HH:mm:ss")
                        );

                logArea.append(
                        "[" + timestamp + "] "
                                + event
                                + " | "
                                + booking.getId()
                                + " | "
                                + booking.getGuestName()
                                + " | Room "
                                + booking.getRoom().getRoomNumber()
                                + "\n"
                );

                logArea.setCaretPosition(
                        logArea.getDocument().getLength()
                );
            };

            if (SwingUtilities.isEventDispatchThread()) {
                task.run();
            } else {
                SwingUtilities.invokeLater(task);
            }
        }
    }

    // ============================================================
    // BOOKING SERVICE
    // Coordinates booking creation, availability and cancellation.
    // ============================================================

    static class BookingService {

        private final RoomInventory inventory;
        private final BookingNotifier notifier;

        private final List<Booking> allBookings =
                new ArrayList<>();

        private int nextBookingNumber = 1001;

        public BookingService(
                RoomInventory inventory,
                BookingNotifier notifier
        ) {

            this.inventory = inventory;
            this.notifier = notifier;
        }

        public Booking createBooking(
                String guestName,
                HotelRoom room,
                LocalDate checkIn,
                LocalDate checkOut,
                PriceStrategy strategy
        ) {

            if (guestName == null || guestName.trim().isEmpty()) {

                throw new IllegalArgumentException(
                        "Please enter the guest's full name."
                );
            }

            if (room == null) {

                throw new IllegalArgumentException(
                        "Please select a room."
                );
            }

            if (!inventory.getRooms().contains(room)) {

                throw new IllegalArgumentException(
                        "Selected room is not registered in the hotel."
                );
            }

            if (strategy == null) {

                throw new IllegalArgumentException(
                        "Please select a pricing strategy."
                );
            }

            if (checkIn == null || checkOut == null
                    || checkIn.isBefore(LocalDate.now())
                    || !checkOut.isAfter(checkIn)) {

                throw new IllegalArgumentException(
                        "Please provide valid check-in and check-out dates."
                );
            }

            // Verify that the selected room is available.
            for (Booking existing : allBookings) {

                if (existing.getRoom().getRoomNumber()
                        .equals(room.getRoomNumber())
                        && !(existing.getState()
                        instanceof CancelledState)) {

                    boolean overlaps =
                            checkIn.isBefore(existing.getCheckOut())
                                    && checkOut.isAfter(
                                    existing.getCheckIn()
                            );

                    if (overlaps) {

                        throw new IllegalArgumentException(
                                "Room " + room.getRoomNumber()
                                        + " is already booked for "
                                        + "some or all of these dates."
                        );
                    }
                }
            }

            long nights = ChronoUnit.DAYS.between(
                    checkIn, checkOut
            );

            double total = strategy.calculatePrice(
                    room.getPrice(),
                    nights
            );

            String id = "RX-" + nextBookingNumber++;

            Booking booking = new Booking(
                    id,
                    guestName.trim(),
                    room,
                    checkIn,
                    checkOut,
                    total
            );

            // Use State pattern to transition the booking.
            booking.confirm();

            allBookings.add(booking);

            notifier.notifyObservers(
                    "RESERVATION CONFIRMED",
                    booking
            );

            return booking;
        }

        public void cancelBooking(Booking booking) {

            if (booking == null) {

                throw new IllegalArgumentException(
                        "Reservation not found."
                );
            }

            if (!allBookings.contains(booking)) {

                throw new IllegalArgumentException(
                        "Reservation does not belong to this hotel."
                );
            }

            // State pattern controls whether cancellation is allowed.
            booking.cancel();

            notifier.notifyObservers(
                    "RESERVATION CANCELLED",
                    booking
            );
        }

        public Booking findBooking(String id) {

            for (Booking booking : allBookings) {

                if (booking.getId().equals(id)) {
                    return booking;
                }
            }

            return null;
        }
    }

    // ==================== APPLICATION ENTRY POINT ====================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                UIManager.setLookAndFeel(
                        UIManager.getSystemLookAndFeelClassName()
                );

            } catch (Exception ignored) {
                // Default Swing look and feel will be used.
            }

            Main application = new Main();
            application.setVisible(true);
        });
    }
}