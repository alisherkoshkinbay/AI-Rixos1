import javax.swing.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class Main {
    private final WeatherStation station = new WeatherStation();
    private final CommandInvoker invoker = new CommandInvoker();
    private final History history = new History();
    private final JComboBox<String> cities = new JComboBox<>(new String[]{"Алматы", "Астана", "Қызылорда", "Шымкент"});
    private final JLabel status = label("Қолмен бақылау", 12, new Color(105, 120, 140));
    private final JLabel updated = label("", 12, new Color(105, 120, 140));
    private final JTextArea log = new JTextArea();
    private final JButton toggle = button("Бақылауды бастау", false);
    private final javax.swing.Timer timer;
    private final JPanel root = new JPanel(new BorderLayout());
    private static final Color INK = new Color(25, 40, 66);
    private static final Color MUTED = new Color(108, 123, 144);

    public Main() {
        root.setBackground(new Color(244, 247, 251));
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(new Color(18, 32, 54));
        sidebar.setBorder(BorderFactory.createEmptyBorder(34, 24, 24, 24));
        sidebar.setPreferredSize(new Dimension(218, 700));
        sidebar.add(label("ATMOS", 28, Color.WHITE));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(label("Ауа райын бақылау", 12, new Color(152, 170, 194)));
        sidebar.add(Box.createVerticalStrut(45));
        JLabel nav = label("●  Шолу панелі", 15, new Color(120, 205, 255));
        sidebar.add(nav);
        sidebar.add(Box.createVerticalStrut(25));
        sidebar.add(label("ҮЙ + СЫРТҚЫ ОРТА", 11, new Color(152, 170, 194)));
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(label("Екі температура көзі", 13, Color.WHITE));
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(label("DEMO MODE", 11, new Color(120, 205, 255)));
        sidebar.add(Box.createVerticalStrut(8));
        sidebar.add(label("Демо деректер", 11, new Color(152, 170, 194)));
        root.add(sidebar, BorderLayout.WEST);

        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        content.setBorder(BorderFactory.createEmptyBorder(28, 28, 22, 28));
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 6)); heading.setOpaque(false);
        heading.add(label("Бақылау панелі", 27, INK));
        heading.add(label("Бөлме мен сыртқы ортаның ауа райы бір экранда", 13, MUTED));
        header.add(heading, BorderLayout.WEST);
        cities.setFont(new Font("Dialog", Font.PLAIN, 14));
        cities.setPreferredSize(new Dimension(155, 38));
        JPanel cityWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT)); cityWrap.setOpaque(false);
        cityWrap.add(cities); header.add(cityWrap, BorderLayout.EAST);
        content.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 18)); center.setOpaque(false);
        WidgetGroup hero = new WidgetGroup(1, 2);
        hero.add(new TemperatureCard(true)); hero.add(new TemperatureCard(false));
        hero.view().setPreferredSize(new Dimension(700, 210));
        center.add(hero.view(), BorderLayout.NORTH);
        WidgetGroup metrics = new WidgetGroup(1, 3);
        metrics.add(new MetricWidget("Сырттағы ылғалдылық", d -> d.humidity() + " %"));
        metrics.add(new MetricWidget("Жел жылдамдығы", d -> d.wind() + " м/с"));
        metrics.add(new MetricWidget("Температура айырмасы", d -> Math.abs(d.indoor() - d.temperature()) + " °C"));
        WidgetGroup overview = new WidgetGroup(1, 1);
        overview.add(metrics);
        JPanel middle = new JPanel(new BorderLayout(0, 18)); middle.setOpaque(false);
        overview.view().setPreferredSize(new Dimension(700, 110));
        middle.add(overview.view(), BorderLayout.NORTH);
        TrendChart chart = new TrendChart();
        middle.add(chart.view(), BorderLayout.CENTER);
        center.add(middle, BorderLayout.CENTER);
        content.add(center, BorderLayout.CENTER);
        station.addObserver(hero); station.addObserver(overview); station.addObserver(chart);

        JPanel footer = new JPanel(new BorderLayout(0, 10)); footer.setOpaque(false);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); actions.setOpaque(false);
        JButton refresh = button("Жаңарту", true);
        JButton undo = button("Артқа қайтару", false);
        actions.add(refresh); actions.add(toggle); actions.add(undo);
        footer.add(actions, BorderLayout.NORTH);
        JPanel info = new JPanel(new BorderLayout()); info.setOpaque(false);
        info.add(status, BorderLayout.WEST); info.add(updated, BorderLayout.EAST);
        footer.add(info, BorderLayout.CENTER);
        log.setEditable(false); log.setFont(new Font("Dialog", Font.PLAIN, 11));
        log.setBackground(new Color(244, 247, 251)); log.setForeground(MUTED);
        JScrollPane scroll = new JScrollPane(log); scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setPreferredSize(new Dimension(700, 52));
        footer.add(scroll, BorderLayout.SOUTH);
        content.add(footer, BorderLayout.SOUTH);
        root.add(content, BorderLayout.CENTER);

        station.addObserver(d -> {
            String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String line = time + "  •  " + d.city() + "  |  Бөлме: " + d.indoor()
                + " °C  |  Сыртта: " + d.temperature() + " °C  |  " + d.condition();
            log.append(line + "\n"); log.setCaretPosition(log.getDocument().getLength());
            if (log.getLineCount() > 100) {
                try { log.replaceRange("", 0, log.getLineEndOffset(0)); } catch (Exception ignored) {}
            }
            updated.setText("Жаңартылды: " + time);
            cities.setSelectedItem(d.city());
            System.out.println(line);
        });
        timer = new javax.swing.Timer(4000, e -> refresh());
        refresh.addActionListener(e -> refresh());
        undo.addActionListener(e -> {
            if (!invoker.execute(new RestoreCommand(station, history)))
                JOptionPane.showMessageDialog(root, "Қайтарылатын алдыңғы деректер жоқ.");
        });
        toggle.addActionListener(e -> {
            invoker.execute(new ToggleCommand(station));
            if (station.isMonitoring()) timer.start(); else timer.stop();
            toggle.setText(station.isMonitoring() ? "Бақылауды тоқтату" : "Бақылауды бастау");
            status.setText(station.isMonitoring() ? "● Автоматты бақылау • әр 4 секунд" : "● Қолмен бақылау");
        });
        station.publish();
    }
    private void refresh() { invoker.execute(new RefreshCommand(station, history, (String)cities.getSelectedItem())); }
    static JLabel label(String text, int size, Color color) {
        JLabel label = new JLabel(text); label.setFont(new Font("Dialog", size >= 20 ? Font.BOLD : Font.PLAIN, size));
        label.setForeground(color); return label;
    }
    static JButton button(String text, boolean primary) {
        JButton b = new JButton(text); b.setFont(new Font("Dialog", Font.BOLD, 12));
        b.setForeground(primary ? Color.WHITE : INK);
        b.setBackground(primary ? new Color(44, 105, 222) : Color.WHITE);
        b.setOpaque(true); b.setFocusPainted(false); b.setBorderPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); return b;
    }
    private void open() {
        JFrame frame = new JFrame("ATMOS — Ауа райын бақылау");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); frame.setContentPane(root);
        frame.setSize(1200, 850); frame.setMinimumSize(new Dimension(1060, 780));
        frame.setLocationRelativeTo(null); frame.setVisible(true);
    }
    static void layoutTree(Container c) { c.doLayout(); for (Component x : c.getComponents()) if (x instanceof Container) layoutTree((Container)x); }
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--test")) { PatternChecks.run(); return; }
        SwingUtilities.invokeLater(() -> {
            Main app = new Main();
            if (args.length > 0 && args[0].equals("--preview")) {
                app.root.setSize(1200, 850); layoutTree(app.root);
                java.awt.image.BufferedImage image = new java.awt.image.BufferedImage(1200, 850, java.awt.image.BufferedImage.TYPE_INT_RGB);
                Graphics2D g = image.createGraphics(); app.root.printAll(g); g.dispose();
                try { javax.imageio.ImageIO.write(image, "png", new java.io.File("preview.png")); }
                catch (java.io.IOException ex) { throw new RuntimeException(ex); }
            } else app.open();
        });
    }
}

class TemperatureCard extends JPanel implements DashboardWidget {
    private final boolean indoor;
    private WeatherData data;
    TemperatureCard(boolean indoor) { this.indoor = indoor; setOpaque(false); }
    public JComponent view() { return this; }
    public void update(WeatherData data) { this.data = data; repaint(); }
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D)graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Color a = indoor ? new Color(234, 249, 244) : new Color(44, 99, 211);
        Color b = indoor ? new Color(216, 241, 233) : new Color(69, 140, 239);
        g.setPaint(new GradientPaint(0, 0, a, getWidth(), getHeight(), b));
        g.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
        Color ink = indoor ? new Color(25, 92, 76) : Color.WHITE;
        g.setColor(ink); g.setFont(new Font("Dialog", Font.BOLD, 12));
        g.drawString(indoor ? "БӨЛМЕ ІШІНДЕ" : "СЫРТҚЫ ОРТА", 24, 32);
        g.setFont(new Font("Dialog", Font.BOLD, 58));
        g.drawString(data == null ? "—" : (indoor ? data.indoor() : data.temperature()) + "°", 24, 108);
        g.setFont(new Font("Dialog", Font.PLAIN, 13));
        String desc = data == null ? "" : (indoor ? "Қонақ бөлме • ішкі датчик үлгісі" : data.city() + " • " + data.condition());
        g.drawString(desc, 24, 140);
        g.setFont(new Font("Dialog", Font.PLAIN, 12));
        String detail = data == null ? "" : (indoor ? (data.indoor() >= 20 && data.indoor() <= 25 ? "Жайлылық деңгейі: қалыпты" : "Температураны реттеу ұсынылады") : "Жел: " + data.wind() + " м/с  •  Ылғал: " + data.humidity() + "%");
        g.drawString(detail, 24, 175);
        int x = getWidth() - 78;
        g.setStroke(new BasicStroke(3));
        if (indoor) {
            g.drawLine(x - 18, 65, x + 6, 43); g.drawLine(x + 6, 43, x + 30, 65);
            g.drawRect(x - 10, 65, 32, 28); g.drawRect(x + 1, 77, 10, 16);
        } else {
            g.drawOval(x - 9, 57, 28, 28);
            for (int i = 0; i < 8; i++) {
                double angle = i * Math.PI / 4;
                g.drawLine(x + 5 + (int)(23*Math.cos(angle)), 71 + (int)(23*Math.sin(angle)),
                    x + 5 + (int)(30*Math.cos(angle)), 71 + (int)(30*Math.sin(angle)));
            }
        }
        g.dispose();
    }
}

class TrendChart extends JPanel implements DashboardWidget {
    private final List<WeatherData> points = new ArrayList<>();
    TrendChart() { setOpaque(false); setPreferredSize(new Dimension(700, 200)); }
    public JComponent view() { return this; }
    public void update(WeatherData data) { points.add(data); if (points.size() > 20) points.remove(0); repaint(); }
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics); Graphics2D g = (Graphics2D)graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(Color.WHITE); g.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
        g.setColor(new Color(25, 40, 66)); g.setFont(new Font("Dialog", Font.BOLD, 16));
        g.drawString("Температура динамикасы", 22, 30);
        g.setFont(new Font("Dialog", Font.PLAIN, 11));
        g.setColor(new Color(40, 152, 117)); g.drawString("Бөлме", getWidth()-175, 29);
        g.setColor(new Color(44, 105, 222)); g.drawString("Сыртта", getWidth()-90, 29);
        int left = 45, top = 55, width = getWidth()-70, height = getHeight()-85;
        for (int i=0; i<=4; i++) {
            int y=top+i*height/4;
            g.setColor(new Color(232, 238, 245)); g.drawLine(left,y,left+width,y);
            g.setColor(new Color(108,123,144)); g.drawString((40-i*15)+"°", 12,y+4);
        }
        for (int series=0; series<2; series++) {
            g.setColor(series==0 ? new Color(40,152,117) : new Color(44,105,222)); g.setStroke(new BasicStroke(2.5f));
            for (int i=0; i<points.size(); i++) {
                int x=left+i*width/19;
                int value=series==0 ? points.get(i).indoor() : points.get(i).temperature();
                int y=top+(40-value)*height/60;
                if(i>0) { int prev=series==0 ? points.get(i-1).indoor() : points.get(i-1).temperature();
                    g.drawLine(left+(i-1)*width/19,top+(40-prev)*height/60,x,y); }
                g.fillOval(x-3,y-3,6,6);
            }
        }
        g.setColor(new Color(108,123,144)); g.drawString("Соңғы 20 бақылау оқиғасы • жаңарту және қалпына келтіру", 22,getHeight()-12);
        g.dispose();
    }
}

record WeatherData(String city, int temperature, int indoor, int humidity, int wind, String condition) {}

// OBSERVER: дерек өзгергенде барлық жазылған бақылаушылар хабар алады.
interface WeatherObserver { void update(WeatherData data); }
class WeatherStation {
    private WeatherData data = new WeatherData("Алматы", 16, 23, 55, 3, "Ашық");
    private final List<WeatherObserver> observers = new ArrayList<>();
    private MonitoringState state = new PausedState();
    private final Random random = new Random();
    public void addObserver(WeatherObserver observer) { observers.add(observer); }
    public void publish() { for (WeatherObserver observer : observers) observer.update(data); }
    public void refresh(String city) {
        String[] conditions = {"Ашық", "Бұлтты", "Жаңбыр", "Қар"};
        String condition = conditions[random.nextInt(conditions.length)];
        int temperature = condition.equals("Қар") ? random.nextInt(11) - 10 : random.nextInt(31) + 1;
        data = new WeatherData(city, temperature, Math.max(18, Math.min(29, data.indoor() + random.nextInt(3) - 1)), random.nextInt(71) + 25, random.nextInt(16), condition);
        publish();
    }
    public WeatherData current() { return data; }
    // MEMENTO: сақталатын күйдің құрылымын станцияның өзі басқарады.
    public Memento save() { return new Memento(data); }
    public void restore(Memento saved) { data = saved.data; publish(); }
    public static final class Memento {
        private final WeatherData data;
        private Memento(WeatherData data) { this.data = data; }
    }
    public void toggle() { state.toggle(this); }
    void setState(MonitoringState state) { this.state = state; }
    public boolean isMonitoring() { return state.active(); }
    public String stateName() { return state.name(); }
}
class History {
    private final Deque<WeatherStation.Memento> snapshots = new ArrayDeque<>();
    public void push(WeatherStation.Memento snapshot) { snapshots.push(snapshot); if (snapshots.size() > 100) snapshots.removeLast(); }
    public WeatherStation.Memento pop() { return snapshots.pollFirst(); }
}

// STATE: режим ауысу әрекеті жеке күй объектілеріне берілген.
interface MonitoringState {
    void toggle(WeatherStation station);
    boolean active();
    String name();
}
class Monitoring implements MonitoringState {
    public void toggle(WeatherStation station) { station.setState(new PausedState()); }
    public boolean active() { return true; }
    public String name() { return "Автоматты бақылау (әр 4 секунд)"; }
}
class PausedState implements MonitoringState {
    public void toggle(WeatherStation station) { station.setState(new Monitoring()); }
    public boolean active() { return false; }
    public String name() { return "Тоқтатылған — қолмен жаңартуға болады"; }
}

// COMMAND: интерфейс батырмалары командаларды invoker арқылы орындайды.
interface Command { boolean execute(); }
class CommandInvoker {
    public boolean execute(Command command) { return command.execute(); }
}
class RefreshCommand implements Command {
    private final WeatherStation station;
    private final History history;
    private final String city;
    RefreshCommand(WeatherStation station, History history, String city) {
        this.station = station; this.history = history; this.city = city;
    }
    public boolean execute() {
        history.push(station.save());
        station.refresh(city);
        return true;
    }
}
class RestoreCommand implements Command {
    private final WeatherStation station;
    private final History history;
    RestoreCommand(WeatherStation station, History history) { this.station = station; this.history = history; }
    public boolean execute() {
        WeatherStation.Memento snapshot = history.pop();
        if (snapshot == null) return false;
        station.restore(snapshot);
        return true;
    }
}
class ToggleCommand implements Command {
    private final WeatherStation station;
    ToggleCommand(WeatherStation station) { this.station = station; }
    public boolean execute() { station.toggle(); return true; }
}

// COMPOSITE: жеке виджет пен топ бірдей интерфейсті қолданады.
interface DashboardWidget extends WeatherObserver { JComponent view(); }
class MetricWidget implements DashboardWidget {
    private final JPanel panel = new JPanel(new GridLayout(2, 1));
    private final JLabel value = new JLabel("—", SwingConstants.CENTER);
    private final Function<WeatherData, String> formatter;
    MetricWidget(String title, Function<WeatherData, String> formatter) {
        this.formatter = formatter;
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(211, 224, 240)),
            BorderFactory.createEmptyBorder(20, 12, 20, 12)));
        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Dialog", Font.PLAIN, 16));
        value.setFont(new Font("Dialog", Font.BOLD, 26));
        value.setForeground(new Color(35, 75, 133));
        panel.add(label); panel.add(value);
    }
    public void update(WeatherData data) { value.setText(formatter.apply(data)); }
    public JComponent view() { return panel; }
}
class WidgetGroup implements DashboardWidget {
    private final List<DashboardWidget> children = new ArrayList<>();
    private final JPanel panel;
    WidgetGroup(int rows, int columns) {
        panel = new JPanel(new GridLayout(rows, columns, 12, 12));
        panel.setOpaque(false);
    }
    public void add(DashboardWidget widget) { children.add(widget); panel.add(widget.view()); }
    public void update(WeatherData data) { for (DashboardWidget child : children) child.update(data); }
    public JComponent view() { return panel; }
}

// Терезесіз тексеру: java Main.java --test
class PatternChecks {
    static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }
    public static void run() {
        WeatherStation station = new WeatherStation();
        History history = new History();
        CommandInvoker invoker = new CommandInvoker();
        int[] calls = {0};
        station.addObserver(d -> calls[0]++);
        WeatherData initial = station.current();
        invoker.execute(new RefreshCommand(station, history, "Астана"));
        WeatherData second = station.current();
        invoker.execute(new RefreshCommand(station, history, "Шымкент"));
        require(calls[0] == 2, "Observer хабарламалары");
        require(invoker.execute(new RestoreCommand(station, history)), "Бірінші undo");
        require(station.current().equals(second), "Алдыңғы деректерді қалпына келтіру");
        invoker.execute(new RestoreCommand(station, history));
        require(station.current().equals(initial), "Бастапқы деректерді қалпына келтіру");
        require(station.current().indoor() == initial.indoor(), "Бөлме температурасы қайтарылды");
        require(!invoker.execute(new RestoreCommand(station, history)), "Бос тарих");
        invoker.execute(new ToggleCommand(station));
        require(station.isMonitoring(), "Бақылауды бастау");
        invoker.execute(new ToggleCommand(station));
        require(!station.isMonitoring(), "Бақылауды тоқтату");
        WidgetGroup parent = new WidgetGroup(1, 1);
        WidgetGroup child = new WidgetGroup(1, 1);
        int[] nested = {0};
        child.add(new DashboardWidget() {
            public void update(WeatherData d) { nested[0]++; }
            public JComponent view() { return new JPanel(); }
        });
        parent.add(child); parent.update(initial);
        require(nested[0] == 1, "Composite ішкі топқа хабар берді");
        System.out.println("Command, Memento, Observer, State, Composite: тексерулер сәтті.");
    }
}
