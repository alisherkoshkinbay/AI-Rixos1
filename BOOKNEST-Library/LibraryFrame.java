package kz.teamworks.library.ui;

import kz.teamworks.library.model.Book;
import kz.teamworks.library.model.Loan;
import kz.teamworks.library.observer.LibraryEventPublisher;
import kz.teamworks.library.observer.LibraryObserver;
import kz.teamworks.library.service.LibraryFacade;
import kz.teamworks.library.strategy.ByAuthorStrategy;
import kz.teamworks.library.strategy.ByAvailabilityStrategy;
import kz.teamworks.library.strategy.ByTitleStrategy;
import kz.teamworks.library.strategy.BookSortStrategy;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Polished Swing dashboard for the Library Management System. */
public class LibraryFrame extends JFrame implements LibraryObserver {
    private static final Color NAVY = new Color(23, 38, 67);
    private static final Color NAVY_LIGHT = new Color(43, 63, 103);
    private static final Color BG = new Color(245, 247, 252);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(31, 43, 65);
    private static final Color MUTED = new Color(126, 137, 158);
    private static final Color BLUE = new Color(88, 90, 230);
    private static final Color TEAL = new Color(25, 165, 151);
    private static final Color AMBER = new Color(222, 159, 55);
    private static final Color RED = new Color(219, 91, 113);
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final LibraryFacade library;
    private final LibraryEventPublisher events;
    private final CardLayout pageLayout = new CardLayout();
    private final JPanel pageHost = new JPanel(pageLayout);
    private final List<JButton> navButtons = new ArrayList<>();

    private JLabel totalCopiesValue;
    private JLabel availableValue;
    private JLabel activeLoansValue;
    private JLabel overdueValue;
    private JLabel fineValue;
    private JLabel statusLabel;
    private JTable booksTable;
    private JTable loansTable;
    private DefaultTableModel booksModel;
    private DefaultTableModel loansModel;
    private JTextField searchField;
    private JComboBox<BookSortStrategy> sortCombo;
    private List<Book> visibleBooks = new ArrayList<>();
    private List<Loan> visibleLoans = new ArrayList<>();

    public LibraryFrame(LibraryFacade library, LibraryEventPublisher events) {
        this.library = library;
        this.events = events;
        setTitle("BOOKNEST — Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1120, 720));
        setSize(1360, 850);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        pageHost.setBackground(BG);
        pageHost.add(buildDashboardPage(), "dashboard");
        pageHost.add(buildBooksPage(), "books");
        pageHost.add(buildLoansPage(), "loans");
        add(pageHost, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);

        events.subscribe(this);
        refreshAll();
        showPage("dashboard", 0);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(236, 0));
        sidebar.setBackground(NAVY);
        sidebar.setBorder(new EmptyBorder(26, 17, 20, 17));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JPanel brand = new JPanel(new BorderLayout(11, 0));
        brand.setOpaque(false);
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);
        brand.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        JLabel logo = new JLabel("B");
        logo.setOpaque(true);
        logo.setBackground(new Color(106, 105, 239));
        logo.setForeground(WHITE);
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logo.setFont(new Font("SansSerif", Font.BOLD, 24));
        logo.setPreferredSize(new Dimension(45, 45));
        JLabel brandText = new JLabel("<html><b style='font-size:15px'>BOOKNEST</b><br><span style='color:#AAB7D0;font-size:10px'>LIBRARY WORKSPACE</span></html>");
        brandText.setForeground(WHITE);
        brand.add(logo, BorderLayout.WEST);
        brand.add(brandText, BorderLayout.CENTER);
        sidebar.add(brand);
        sidebar.add(Box.createVerticalStrut(32));

        JPanel profile = new JPanel(new BorderLayout(10, 0));
        profile.setOpaque(true);
        profile.setBackground(new Color(32, 49, 82));
        profile.setBorder(new EmptyBorder(12, 10, 12, 10));
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel avatar = new JLabel("L");
        avatar.setOpaque(true);
        avatar.setBackground(new Color(225, 232, 255));
        avatar.setForeground(BLUE);
        avatar.setHorizontalAlignment(SwingConstants.CENTER);
        avatar.setFont(new Font("SansSerif", Font.BOLD, 17));
        avatar.setPreferredSize(new Dimension(38, 38));
        JLabel profileLabel = new JLabel("<html><b>Library Admin</b><br><span style='color:#AAB7D0;font-size:10px'>Жүйе әкімшісі</span></html>");
        profileLabel.setForeground(WHITE);
        profile.add(avatar, BorderLayout.WEST);
        profile.add(profileLabel, BorderLayout.CENTER);
        sidebar.add(profile);
        sidebar.add(Box.createVerticalStrut(29));
        addSidebarLabel(sidebar, "WORKSPACE");
        sidebar.add(Box.createVerticalStrut(10));
        addNavButton(sidebar, "▦", "Басты бет", "dashboard", 0);
        addNavButton(sidebar, "▤", "Кітап каталогы", "books", 1);
        addNavButton(sidebar, "↔", "Берілімдер және айыппұл", "loans", 2);

        sidebar.add(Box.createVerticalGlue());
        JPanel tip = new JPanel();
        tip.setOpaque(true);
        tip.setBackground(new Color(29, 46, 78));
        tip.setLayout(new BoxLayout(tip, BoxLayout.Y_AXIS));
        tip.setBorder(new EmptyBorder(13, 12, 13, 12));
        tip.setMaximumSize(new Dimension(Integer.MAX_VALUE, 94));
        JLabel tipTitle = new JLabel("YOUR LIBRARY, ORGANIZED");
        tipTitle.setForeground(new Color(194, 204, 226));
        tipTitle.setFont(new Font("SansSerif", Font.BOLD, 10));
        JLabel tipLine1 = new JLabel("Каталог. Оқырмандар.");
        JLabel tipLine2 = new JLabel("Қайтару мерзімі. Айыппұл.");
        tipLine1.setForeground(WHITE);
        tipLine2.setForeground(WHITE);
        tipLine1.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tipLine2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tip.add(tipTitle);
        tip.add(Box.createVerticalStrut(9));
        tip.add(tipLine1);
        tip.add(tipLine2);
        sidebar.add(tip);
        return sidebar;
    }

    private void addSidebarLabel(JPanel sidebar, String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(132, 149, 182));
        label.setFont(new Font("SansSerif", Font.BOLD, 10));
        label.setBorder(new EmptyBorder(0, 8, 0, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(label);
    }

    private void addNavButton(JPanel sidebar, String symbol, String label, String page, int index) {
        JButton button = new JButton("  " + symbol + "     " + label);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setForeground(new Color(206, 215, 234));
        button.setBackground(NAVY);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(13, 8, 13, 8));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(e -> showPage(page, index));
        navButtons.add(button);
        sidebar.add(button);
        sidebar.add(Box.createVerticalStrut(5));
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(WHITE);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 234, 242)),
                new EmptyBorder(7, 18, 7, 18)));
        statusLabel = new JLabel("Жүйе дайын · Мәліметтер автоматты сақталады");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        statusLabel.setForeground(MUTED);
        JLabel dateLabel = new JLabel(LocalDate.now().format(DATE) + "   ·   Қазақстан уақыты");
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dateLabel.setForeground(MUTED);
        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(dateLabel, BorderLayout.EAST);
        return bar;
    }

    private JPanel buildDashboardPage() {
        JPanel page = pageBase();
        page.add(headerBlock("Кітапхана басқару орталығы", "Кітаптарды, берілімдерді және қайтару мерзімдерін бір жерден басқарыңыз."));
        page.add(Box.createVerticalStrut(18));

        JPanel metrics = new JPanel(new GridLayout(1, 4, 13, 0));
        metrics.setOpaque(false);
        totalCopiesValue = new JLabel("0");
        availableValue = new JLabel("0");
        activeLoansValue = new JLabel("0");
        overdueValue = new JLabel("0");
        metrics.add(metricCard("Кітап даналары", totalCopiesValue, "Каталогтағы жалпы сан", BLUE, "▤"));
        metrics.add(metricCard("Қолжетімді", availableValue, "Беруге дайын", TEAL, "✓"));
        metrics.add(metricCard("Қолдағы берілім", activeLoansValue, "Қайтарылмаған кітаптар", AMBER, "↗"));
        metrics.add(metricCard("Мерзімі өткен", overdueValue, "Назар аударуды қажет етеді", RED, "!"));
        metrics.setMaximumSize(new Dimension(Integer.MAX_VALUE, 132));
        page.add(metrics);
        page.add(Box.createVerticalStrut(17));

        JPanel quick = whitePanel();
        quick.setLayout(new BorderLayout(12, 5));
        quick.setBorder(new EmptyBorder(17, 19, 17, 19));
        JPanel quickText = new JPanel();
        quickText.setOpaque(false);
        quickText.setLayout(new BoxLayout(quickText, BoxLayout.Y_AXIS));
        quickText.add(makeLabel("Жылдам әрекеттер", 15, TEXT, true));
        quickText.add(Box.createVerticalStrut(5));
        quickText.add(makeLabel("Кітап қорын толықтырыңыз немесе берілімді тіркеңіз.", 11, MUTED, false));
        JPanel quickButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 9, 0));
        quickButtons.setOpaque(false);
        JButton addBook = primaryButton("＋  Кітап қосу");
        JButton issueBook = softButton("↗  Қарызға беру");
        JButton openLoans = softButton("Берілімдерді көру  →");
        addBook.addActionListener(e -> showPage("books", 1));
        issueBook.addActionListener(e -> quickIssueDialog());
        openLoans.addActionListener(e -> showPage("loans", 2));
        quickButtons.add(addBook);
        quickButtons.add(issueBook);
        quickButtons.add(openLoans);
        quick.add(quickText, BorderLayout.WEST);
        quick.add(quickButtons, BorderLayout.EAST);
        quick.setMaximumSize(new Dimension(Integer.MAX_VALUE, 92));
        page.add(quick);
        page.add(Box.createVerticalStrut(18));

        JPanel details = new JPanel(new GridLayout(1, 2, 14, 0));
        details.setOpaque(false);
        details.add(buildRecentLoansCard());
        details.add(buildLibraryNotesCard());
        details.setMaximumSize(new Dimension(Integer.MAX_VALUE, 270));
        page.add(details);
        page.add(Box.createVerticalGlue());
        return page;
    }

    private JPanel buildRecentLoansCard() {
        JPanel card = whitePanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(17, 18, 15, 18));
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        heading.add(makeLabel("Соңғы берілімдер", 14, TEXT, true), BorderLayout.WEST);
        JButton seeAll = textButton("Барлығын көру  →");
        seeAll.addActionListener(e -> showPage("loans", 2));
        heading.add(seeAll, BorderLayout.EAST);
        DefaultTableModel model = new DefaultTableModel(new Object[]{"КІТАП", "ОҚЫРМАН", "МЕРЗІМ"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = styleTable(new JTable(model));
        table.setRowHeight(33);
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(WHITE);
        card.add(heading, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildLibraryNotesCard() {
        JPanel card = whitePanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(17, 18, 15, 18));
        card.add(makeLabel("Бақылау панелі", 14, TEXT, true));
        card.add(Box.createVerticalStrut(15));
        JPanel overdue = new JPanel(new BorderLayout(11, 0));
        overdue.setOpaque(true);
        overdue.setBackground(new Color(255, 246, 247));
        overdue.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel icon = new JLabel("!");
        icon.setOpaque(true);
        icon.setBackground(new Color(255, 226, 231));
        icon.setForeground(RED);
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.BOLD, 18));
        icon.setPreferredSize(new Dimension(37, 37));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(makeLabel("Мерзімі өткен кітаптар", 12, TEXT, true));
        text.add(Box.createVerticalStrut(4));
        text.add(makeLabel("Әр кешіккен күн үшін " + LibraryFacade.FINE_PER_DAY_KZT + " ₸ айыппұл есептеледі.", 10, MUTED, false));
        overdue.add(icon, BorderLayout.WEST);
        overdue.add(text, BorderLayout.CENTER);
        overdue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        card.add(overdue);
        card.add(Box.createVerticalStrut(11));

        JPanel persistence = new JPanel(new BorderLayout(11, 0));
        persistence.setOpaque(true);
        persistence.setBackground(new Color(239, 251, 248));
        persistence.setBorder(new EmptyBorder(12, 12, 12, 12));
        JLabel ok = new JLabel("✓");
        ok.setOpaque(true);
        ok.setBackground(new Color(212, 244, 236));
        ok.setForeground(TEAL);
        ok.setHorizontalAlignment(SwingConstants.CENTER);
        ok.setFont(new Font("SansSerif", Font.BOLD, 18));
        ok.setPreferredSize(new Dimension(37, 37));
        JPanel persistText = new JPanel();
        persistText.setOpaque(false);
        persistText.setLayout(new BoxLayout(persistText, BoxLayout.Y_AXIS));
        persistText.add(makeLabel("Автоматты сақтау қосулы", 12, TEXT, true));
        persistText.add(Box.createVerticalStrut(4));
        persistText.add(makeLabel("Өзгерістер library-data.ser файлына сақталады.", 10, MUTED, false));
        persistence.add(ok, BorderLayout.WEST);
        persistence.add(persistText, BorderLayout.CENTER);
        persistence.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));
        card.add(persistence);
        card.add(Box.createVerticalGlue());
        return card;
    }

    private JPanel buildBooksPage() {
        JPanel page = pageBase();
        page.add(headerBlock("Кітап каталогы", "Кітап қорын басқарыңыз, іздеңіз және оқырманға беріңіз."));
        page.add(Box.createVerticalStrut(17));

        JPanel card = whitePanel();
        card.setLayout(new BorderLayout(0, 13));
        card.setBorder(new EmptyBorder(16, 17, 17, 17));
        JPanel toolbar = new JPanel(new BorderLayout(10, 8));
        toolbar.setOpaque(false);
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        left.setOpaque(false);
        searchField = new JTextField(18);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchField.setToolTipText("Кітаптың атауы, авторы немесе санаты бойынша іздеу");
        searchField.setPreferredSize(new Dimension(245, 37));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 231, 241)),
                new EmptyBorder(5, 10, 5, 10)));
        left.add(new JLabel("⌕"));
        left.add(searchField);
        left.add(makeLabel("Сұрыптау", 11, MUTED, false));
        sortCombo = new JComboBox<>(new BookSortStrategy[]{new ByTitleStrategy(), new ByAuthorStrategy(), new ByAvailabilityStrategy()});
        sortCombo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        sortCombo.setPreferredSize(new Dimension(155, 34));
        left.add(sortCombo);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 7, 0));
        actions.setOpaque(false);
        JButton add = primaryButton("＋ Кітап қосу");
        JButton issue = softButton("↗ Қарызға беру");
        JButton delete = dangerButton("Өшіру");
        add.addActionListener(e -> addBookDialog());
        issue.addActionListener(e -> issueSelectedBook());
        delete.addActionListener(e -> deleteSelectedBook());
        actions.add(add);
        actions.add(issue);
        actions.add(delete);
        toolbar.add(left, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);

        booksModel = new DefaultTableModel(new Object[]{"ID", "КІТАП АТАУЫ", "АВТОР", "САНАТ", "ЖЫЛ", "ҚОЛЖЕТІМДІ / БАРЛЫҒЫ", "КҮЙІ"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        booksTable = styleTable(new JTable(booksModel));
        booksTable.setRowHeight(42);
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(65);
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(190);
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(155);
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(130);
        booksTable.getColumnModel().getColumn(5).setPreferredWidth(135);
        JScrollPane scroll = new JScrollPane(booksTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(232, 235, 242)));
        scroll.getViewport().setBackground(WHITE);
        card.add(toolbar, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        page.add(card);
        page.add(Box.createVerticalGlue());

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshBooksTable(); }
            public void removeUpdate(DocumentEvent e) { refreshBooksTable(); }
            public void changedUpdate(DocumentEvent e) { refreshBooksTable(); }
        });
        sortCombo.addActionListener(e -> {
            if (sortCombo.getSelectedItem() instanceof BookSortStrategy strategy) {
                library.setSortStrategy(strategy);
                refreshBooksTable();
            }
        });
        return page;
    }

    private JPanel buildLoansPage() {
        JPanel page = pageBase();
        page.add(headerBlock("Берілімдер және айыппұлдар", "Кімнің қандай кітап алғанын, қайтару мерзімін және кешігу айыппұлын бақылаңыз."));
        page.add(Box.createVerticalStrut(17));

        JPanel summary = new JPanel(new GridLayout(1, 3, 12, 0));
        summary.setOpaque(false);
        JLabel loansStat = new JLabel("0");
        JLabel overdueStat = new JLabel("0");
        fineValue = new JLabel("0 ₸");
        summary.add(metricCard("Белсенді берілімдер", loansStat, "Қайтарылмаған кітап", BLUE, "↗"));
        summary.add(metricCard("Мерзімі өткен", overdueStat, "Кешіккен берілімдер", RED, "!"));
        summary.add(metricCard("Болжалды айыппұл", fineValue, "" + LibraryFacade.FINE_PER_DAY_KZT + " ₸ / кешіккен күн", AMBER, "₸"));
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        page.add(summary);
        page.add(Box.createVerticalStrut(16));

        JPanel card = whitePanel();
        card.setLayout(new BorderLayout(0, 12));
        card.setBorder(new EmptyBorder(16, 17, 17, 17));
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setOpaque(false);
        toolbar.add(makeLabel("Барлық кітап берілімдері", 14, TEXT, true), BorderLayout.WEST);
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttons.setOpaque(false);
        JButton returnBtn = primaryButton("✓ Кітапты қайтару");
        JButton refreshBtn = softButton("↻ Жаңарту");
        returnBtn.addActionListener(e -> returnSelectedLoan());
        refreshBtn.addActionListener(e -> refreshAll());
        buttons.add(returnBtn);
        buttons.add(refreshBtn);
        toolbar.add(buttons, BorderLayout.EAST);
        loansModel = new DefaultTableModel(new Object[]{"ID", "КІТАП", "ОҚЫРМАН", "БЕРІЛГЕН", "ҚАЙТАРУ МЕРЗІМІ", "МӘРТЕБЕСІ", "АЙЫППҰЛ"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        loansTable = styleTable(new JTable(loansModel));
        loansTable.setRowHeight(40);
        loansTable.getColumnModel().getColumn(1).setPreferredWidth(180);
        loansTable.getColumnModel().getColumn(2).setPreferredWidth(140);
        JScrollPane scroll = new JScrollPane(loansTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(232, 235, 242)));
        scroll.getViewport().setBackground(WHITE);
        card.add(toolbar, BorderLayout.NORTH);
        card.add(scroll, BorderLayout.CENTER);
        page.add(card);
        page.add(Box.createVerticalGlue());
        loansPageMetrics = new JLabel[]{loansStat, overdueStat};
        return page;
    }

    private JLabel[] loansPageMetrics;

    private JPanel pageBase() {
        JPanel page = new JPanel();
        page.setBackground(BG);
        page.setBorder(new EmptyBorder(25, 27, 19, 27));
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        return page;
    }

    private JPanel headerBlock(String title, String subtitle) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        JLabel titleLabel = makeLabel(title, 25, TEXT, true);
        JLabel subtitleLabel = makeLabel(subtitle, 12, MUTED, false);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        subtitleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        subtitleLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        block.add(titleLabel);
        block.add(Box.createVerticalStrut(6));
        block.add(subtitleLabel);
        block.setMaximumSize(new Dimension(Integer.MAX_VALUE, 63));
        block.setAlignmentX(Component.LEFT_ALIGNMENT);
        return block;
    }

    private JPanel metricCard(String title, JLabel value, String note, Color accent, String symbol) {
        JPanel card = whitePanel();
        card.setLayout(new BorderLayout(9, 6));
        card.setBorder(new EmptyBorder(15, 16, 14, 14));
        JPanel line = new JPanel(new BorderLayout(7, 0));
        line.setOpaque(false);
        JLabel titleLabel = makeLabel(title, 11, MUTED, false);
        JLabel icon = new JLabel(symbol);
        icon.setOpaque(true);
        icon.setBackground(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 25));
        icon.setForeground(accent);
        icon.setHorizontalAlignment(SwingConstants.CENTER);
        icon.setFont(new Font("SansSerif", Font.BOLD, 16));
        icon.setPreferredSize(new Dimension(32, 30));
        line.add(titleLabel, BorderLayout.WEST);
        line.add(icon, BorderLayout.EAST);
        value.setFont(new Font("SansSerif", Font.BOLD, 27));
        value.setForeground(TEXT);
        JLabel noteLabel = makeLabel(note, 10, MUTED, false);
        card.add(line, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);
        card.add(noteLabel, BorderLayout.SOUTH);
        card.setMinimumSize(new Dimension(150, 116));
        return card;
    }

    private JPanel whitePanel() {
        JPanel panel = new JPanel();
        panel.setBackground(WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(231, 235, 244)),
                new EmptyBorder(1, 1, 1, 1)));
        return panel;
    }

    private JLabel makeLabel(String text, int size, Color color, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", bold ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(color);
        return label;
    }

    private JButton primaryButton(String text) { return styleButton(text, BLUE, WHITE, new Color(72, 74, 213)); }
    private JButton softButton(String text) { return styleButton(text, new Color(239, 241, 255), new Color(76, 80, 196), new Color(227, 230, 255)); }
    private JButton dangerButton(String text) { return styleButton(text, new Color(255, 239, 242), RED, new Color(255, 225, 231)); }
    private JButton textButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.PLAIN, 11));
        button.setForeground(BLUE);
        button.setContentAreaFilled(false);
        button.setBorder(new EmptyBorder(4, 4, 4, 4));
        button.setFocusPainted(false);
        return button;
    }
    private JButton styleButton(String text, Color background, Color foreground, Color hover) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 11));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 13, 10, 13));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setMinimumSize(new Dimension(75, 36));
        button.setPreferredSize(new Dimension(button.getPreferredSize().width + 4, 36));
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { button.setBackground(hover); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { button.setBackground(background); }
        });
        return button;
    }

    private JTable styleTable(JTable table) {
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setForeground(TEXT);
        table.setBackground(WHITE);
        table.setGridColor(new Color(239, 241, 247));
        table.setSelectionBackground(new Color(236, 238, 255));
        table.setSelectionForeground(TEXT);
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(false);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 10));
        table.getTableHeader().setForeground(new Color(106, 117, 141));
        table.getTableHeader().setBackground(new Color(247, 248, 252));
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(233, 236, 244)));
        table.getTableHeader().setPreferredSize(new Dimension(0, 37));
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setBorder(new EmptyBorder(0, 9, 0, 8));
        renderer.setVerticalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        return table;
    }

    private void showPage(String page, int activeIndex) {
        pageLayout.show(pageHost, page);
        for (int i = 0; i < navButtons.size(); i++) {
            JButton button = navButtons.get(i);
            boolean active = i == activeIndex;
            button.setBackground(active ? NAVY_LIGHT : NAVY);
            button.setForeground(active ? WHITE : new Color(206, 215, 234));
        }
        refreshAll();
    }

    private void refreshAll() {
        if (totalCopiesValue != null) totalCopiesValue.setText(String.valueOf(library.getTotalBooks()));
        if (availableValue != null) availableValue.setText(String.valueOf(library.getAvailableCopies()));
        if (activeLoansValue != null) activeLoansValue.setText(String.valueOf(library.getActiveLoanCount()));
        if (overdueValue != null) overdueValue.setText(String.valueOf(library.getOverdueCount()));
        if (fineValue != null) fineValue.setText(String.format(Locale.US, "%,d ₸", library.getOutstandingFines()).replace(',', ' '));
        refreshBooksTable();
        refreshLoansTable();
        refreshRecentLoansTable();
        if (loansPageMetrics != null) {
            loansPageMetrics[0].setText(String.valueOf(library.getActiveLoanCount()));
            loansPageMetrics[1].setText(String.valueOf(library.getOverdueCount()));
        }
    }

    private void refreshBooksTable() {
        if (booksModel == null) return;
        booksModel.setRowCount(0);
        visibleBooks = new ArrayList<>();
        String query = searchField == null ? "" : searchField.getText().trim().toLowerCase(Locale.ROOT);
        for (Book book : library.getSortedBooks()) {
            String searchable = (book.getTitle() + " " + book.getAuthor() + " " + book.getCategory()).toLowerCase(Locale.ROOT);
            if (!searchable.contains(query)) continue;
            visibleBooks.add(book);
            booksModel.addRow(new Object[]{book.getId(), book.getTitle(), book.getAuthor(), book.getCategory(), book.getYear(),
                    book.getAvailableCopies() + " / " + book.getTotalCopies(), book.getAvailableCopies() > 0 ? "Қолжетімді" : "Беріліп кеткен"});
        }
    }

    private void refreshLoansTable() {
        if (loansModel == null) return;
        loansModel.setRowCount(0);
        visibleLoans = new ArrayList<>(library.getLoans());
        visibleLoans.sort((a, b) -> b.getIssueDate().compareTo(a.getIssueDate()));
        for (Loan loan : visibleLoans) {
            String state = loan.isReturned() ? "Қайтарылды" : (loan.isOverdue() ? "Мерзімі өткен" : "Берілген");
            int fine = loan.isReturned() ? loan.getFinePaid() : library.fineFor(loan);
            loansModel.addRow(new Object[]{loan.getId(), loan.getBookTitle(), loan.getReaderName(), loan.getIssueDate().format(DATE),
                    loan.getDueDate().format(DATE), state, String.format(Locale.US, "%,d ₸", fine).replace(',', ' ')});
        }
    }

    private void refreshRecentLoansTable() {
        // Refresh the dashboard preview table by locating its table model from the dashboard card.
        if (pageHost.getComponentCount() == 0) return;
        updateRecentLoanPreview(pageHost.getComponent(0));
    }

    private void updateRecentLoanPreview(Component component) {
        if (component instanceof JTable table) return;
        if (component instanceof JScrollPane pane) {
            Component view = pane.getViewport().getView();
            if (view instanceof JTable table && table.getColumnCount() == 3 && table.getColumnName(0).equals("КІТАП")) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                model.setRowCount(0);
                List<Loan> loans = new ArrayList<>(library.getLoans());
                loans.sort((a, b) -> b.getIssueDate().compareTo(a.getIssueDate()));
                int count = 0;
                for (Loan loan : loans) {
                    if (count++ >= 5) break;
                    model.addRow(new Object[]{loan.getBookTitle(), loan.getReaderName(), loan.getDueDate().format(DATE)});
                }
            }
        } else if (component instanceof Container container) {
            for (Component child : container.getComponents()) updateRecentLoanPreview(child);
        }
    }

    private void addBookDialog() {
        JTextField title = new JTextField();
        JTextField author = new JTextField();
        JTextField category = new JTextField("Жалпы әдебиет");
        JTextField year = new JTextField(String.valueOf(LocalDate.now().getYear()));
        JTextField copies = new JTextField("1");
        JPanel form = new JPanel(new GridLayout(0, 1, 6, 5));
        form.add(makeLabel("Кітап атауы", 12, TEXT, true)); form.add(title);
        form.add(makeLabel("Автор", 12, TEXT, true)); form.add(author);
        form.add(makeLabel("Санат", 12, TEXT, true)); form.add(category);
        form.add(makeLabel("Шыққан жылы", 12, TEXT, true)); form.add(year);
        form.add(makeLabel("Дана саны", 12, TEXT, true)); form.add(copies);
        int result = JOptionPane.showConfirmDialog(this, form, "Каталогқа кітап қосу", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            int bookYear = Integer.parseInt(year.getText().trim());
            int copyCount = Integer.parseInt(copies.getText().trim());
            if (bookYear < 0 || bookYear > LocalDate.now().getYear() + 1) throw new IllegalArgumentException("Кітаптың шыққан жылы дұрыс емес.");
            library.addBook(title.getText(), author.getText(), category.getText(), bookYear, copyCount);
        } catch (NumberFormatException ex) {
            showError("Жыл мен дана санына бүтін сан енгізіңіз.");
        } catch (IllegalArgumentException ex) { showError(ex.getMessage()); }
    }

    private void quickIssueDialog() {
        List<Book> available = new ArrayList<>();
        for (Book book : library.catalog()) if (book.getAvailableCopies() > 0) available.add(book);
        if (available.isEmpty()) { showError("Қазір қарызға беруге болатын бос кітап жоқ."); return; }

        JComboBox<Book> bookChoice = new JComboBox<>(available.toArray(new Book[0]));
        JTextField reader = new JTextField();
        JTextField due = new JTextField(LocalDate.now().plusDays(14).toString());
        JPanel form = new JPanel(new GridLayout(0, 1, 6, 5));
        form.add(makeLabel("Кітапты таңдаңыз", 12, TEXT, true)); form.add(bookChoice);
        form.add(makeLabel("Оқырманның аты-жөні", 12, TEXT, true)); form.add(reader);
        form.add(makeLabel("Қайтару мерзімі (YYYY-MM-DD)", 12, TEXT, true)); form.add(due);
        int result = JOptionPane.showConfirmDialog(this, form, "Кітапты қарызға беру", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try {
            library.issueBook((Book) bookChoice.getSelectedItem(), reader.getText(), LocalDate.parse(due.getText().trim()));
        } catch (DateTimeParseException ex) { showError("Мерзімді YYYY-MM-DD форматында енгізіңіз."); }
        catch (IllegalArgumentException | IllegalStateException ex) { showError(ex.getMessage()); }
    }

    private void issueSelectedBook() {
        int selected = booksTable == null ? -1 : booksTable.getSelectedRow();
        if (selected < 0 || selected >= visibleBooks.size()) {
            JOptionPane.showMessageDialog(this, "Алдымен каталогтан кітапты таңдаңыз.", "Кітап таңдалмады", JOptionPane.INFORMATION_MESSAGE);
            showPage("books", 1);
            return;
        }
        Book book = visibleBooks.get(selected);
        JTextField reader = new JTextField();
        JTextField due = new JTextField(LocalDate.now().plusDays(14).toString());
        JPanel form = new JPanel(new GridLayout(0, 1, 6, 5));
        form.add(makeLabel("Таңдалған кітап: " + book.getTitle(), 12, TEXT, true));
        form.add(makeLabel("Оқырманның аты-жөні", 12, TEXT, true)); form.add(reader);
        form.add(makeLabel("Қайтару мерзімі (YYYY-MM-DD)", 12, TEXT, true)); form.add(due);
        int result = JOptionPane.showConfirmDialog(this, form, "Кітапты қарызға беру", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;
        try { library.issueBook(book, reader.getText(), LocalDate.parse(due.getText().trim())); }
        catch (DateTimeParseException ex) { showError("Мерзімді YYYY-MM-DD форматында енгізіңіз."); }
        catch (IllegalArgumentException | IllegalStateException ex) { showError(ex.getMessage()); }
    }

    private void deleteSelectedBook() {
        if (booksTable == null) return;
        int selected = booksTable.getSelectedRow();
        if (selected < 0 || selected >= visibleBooks.size()) {
            showError("Өшіру үшін кітапты таңдаңыз."); return;
        }
        Book book = visibleBooks.get(selected);
        int answer = JOptionPane.showConfirmDialog(this, "«" + book.getTitle() + "» кітабын каталогтан өшіресіз бе?", "Растау", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try { library.removeBook(book); } catch (IllegalStateException ex) { showError(ex.getMessage()); }
        }
    }

    private void returnSelectedLoan() {
        if (loansTable == null) return;
        int selected = loansTable.getSelectedRow();
        if (selected < 0 || selected >= visibleLoans.size()) {
            showError("Қайтару үшін берілімді таңдаңыз."); return;
        }
        Loan loan = visibleLoans.get(selected);
        if (loan.isReturned()) { showError("Бұл кітап бұрын қайтарылған."); return; }
        int fine = library.fineFor(loan);
        String msg = "Кітап: " + loan.getBookTitle() + "\nОқырман: " + loan.getReaderName() + "\nЕсептелген айыппұл: " + fine + " ₸\n\nКітап қайтарылды деп белгілейсіз бе?";
        int answer = JOptionPane.showConfirmDialog(this, msg, "Кітапты қайтару", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                int paidFine = library.returnBook(loan);
                JOptionPane.showMessageDialog(this, "Кітап қайтарылды. Айыппұл: " + paidFine + " ₸", "Дайын", JOptionPane.INFORMATION_MESSAGE);
            } catch (IllegalArgumentException ex) { showError(ex.getMessage()); }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Назар аударыңыз", JOptionPane.WARNING_MESSAGE);
    }

    @Override public void onLibraryChanged(String message) {
        statusLabel.setText(message);
        refreshAll();
    }
}
