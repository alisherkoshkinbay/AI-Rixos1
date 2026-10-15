package kz.edu.lms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) { }
            new LMSFrame().setVisible(true);
        });
    }
}

// ===================== DOMAIN MODEL =====================
interface LMSObserver { void update(String message); }

interface LMSComponent {
    String getTitle();
    String getType();
}

class Assignment implements LMSComponent {
    private final String title;
    private final String type;
    private int score;
    private boolean completed;

    Assignment(String title, String type) { this.title = title; this.type = type; }
    public String getTitle() { return title; }
    public String getType() { return type; }
    public int getScore() { return score; }
    public boolean isCompleted() { return completed; }
    public void setScore(int score) { this.score = Math.max(0, Math.min(100, score)); this.completed = true; }
    public String toString() { return title + "  •  " + type + (completed ? "  •  " + score + "%" : "  •  орындалмаған"); }
}

class Module implements LMSComponent {
    private final String title;
    private final List<LMSComponent> children = new java.util.ArrayList<>();
    Module(String title) { this.title = title; }
    public String getTitle() { return title; }
    public String getType() { return "Модуль"; }
    public void add(LMSComponent item) { children.add(item); }
    public List<LMSComponent> getChildren() { return children; }
}

class Course implements LMSComponent {
    private final String title;
    private final List<LMSComponent> children = new java.util.ArrayList<>();
    private final List<LMSObserver> observers = new java.util.ArrayList<>();
    Course(String title) { this.title = title; }
    public String getTitle() { return title; }
    public String getType() { return "Курс"; }
    public void add(LMSComponent item) { children.add(item); }
    public List<LMSComponent> getChildren() { return children; }
    public void attach(LMSObserver observer) { observers.add(observer); }
    public void notifyObservers(String msg) { observers.forEach(o -> o.update(msg)); }
}

// ===================== STRATEGY =====================
interface GradingStrategy { String grade(int score); }
class PercentageStrategy implements GradingStrategy {
    public String grade(int score) { return score >= 90 ? "A" : score >= 75 ? "B" : score >= 60 ? "C" : "F"; }
}
class RubricStrategy implements GradingStrategy {
    public String grade(int score) { return score >= 85 ? "Өте жақсы" : score >= 70 ? "Жақсы" : score >= 50 ? "Қанағаттанарлық" : "Қайта тапсыру"; }
}
class PassFailStrategy implements GradingStrategy {
    public String grade(int score) { return score >= 50 ? "PASS" : "FAIL"; }
}

// ===================== OBSERVER =====================
class DashboardPanel implements LMSObserver {
    private final JLabel label;
    DashboardPanel(JLabel label) { this.label = label; }
    public void update(String message) { label.setText("Dashboard  •  " + message); }
}
class NotificationPanel implements LMSObserver {
    private final DefaultListModel<String> model;
    NotificationPanel(DefaultListModel<String> model) { this.model = model; }
    public void update(String message) { model.add(0, "🔔  " + message); }
}

// ===================== FACTORY METHOD =====================
abstract class AssignmentCreator {
    public abstract Assignment create(String title);
}
class QuizCreator extends AssignmentCreator {
    public Assignment create(String title) { return new Assignment(title, "Quiz"); }
}
class CodingCreator extends AssignmentCreator {
    public Assignment create(String title) { return new Assignment(title, "Coding"); }
}
class EssayCreator extends AssignmentCreator {
    public Assignment create(String title) { return new Assignment(title, "Essay"); }
}

// ===================== TEMPLATE METHOD =====================
abstract class AssignmentWorkflow {
    public final String submit(Assignment assignment, int score) {
        validate(assignment);
        saveAnswer(assignment);
        assignment.setScore(score);
        return finish(assignment);
    }
    protected void validate(Assignment a) { if (a == null) throw new IllegalArgumentException("Тапсырма таңдалмаған"); }
    protected abstract void saveAnswer(Assignment a);
    protected String finish(Assignment a) { return a.getTitle() + " тапсырмасы жіберілді"; }
}
class StandardAssignmentWorkflow extends AssignmentWorkflow {
    protected void saveAnswer(Assignment a) { /* LMS дерекқорына сақтау имитациясы */ }
}

// ===================== UI =====================
class LMSFrame extends JFrame {
    private final Color navy = new Color(13, 20, 40);
    private final Color panel = new Color(24, 34, 61);
    private final Color blue = new Color(72, 156, 255);
    private final Color purple = new Color(159, 105, 255);
    private final Color text = new Color(235, 241, 255);
    private final Color muted = new Color(154, 168, 196);

    private final Course course = buildCourse();
    private final DefaultListModel<String> notificationModel = new DefaultListModel<>();
    private final DefaultListModel<Assignment> assignmentModel = new DefaultListModel<>();
    private final JLabel dashboardStatus = new JLabel("Dashboard  •  Жүйе дайын");
    private final JLabel selectedTitle = new JLabel("Тапсырманы таңдаңыз");
    private final JLabel selectedMeta = new JLabel(" ");
    private final JLabel selectedScore = new JLabel("—");
    private final JList<Assignment> assignmentList = new JList<>(assignmentModel);
    private final JComboBox<String> strategyBox = new JComboBox<>(new String[]{"Percentage Strategy", "Rubric Strategy", "Pass / Fail Strategy"});
    private Assignment selectedAssignment;
    private GradingStrategy strategy = new PercentageStrategy();

    LMSFrame() {
        setTitle("LMS • Оқу платформасы");
        setSize(1360, 820);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        course.attach(new DashboardPanel(dashboardStatus));
        course.attach(new NotificationPanel(notificationModel));
        buildUI();
        loadAssignments((Module) course.getChildren().get(0));
    }

    private static Course buildCourse() {
        Course c = new Course("Java Design Patterns");
        Module m1 = new Module("1-модуль • ООП негіздері");
        m1.add(new QuizCreator().create("Quiz 01: Кластар және интерфейстер"));
        m1.add(new CodingCreator().create("Практика: Observer паттерні"));
        Module m2 = new Module("2-модуль • Дизайн паттерндері");
        m2.add(new EssayCreator().create("Эссе: Strategy және Factory Method"));
        m2.add(new CodingCreator().create("Практика: LMS архитектурасы"));
        c.add(m1); c.add(m2);
        return c;
    }

    private void buildUI() {
        JPanel root = new JPanel(new BorderLayout(0, 0)); root.setBackground(navy);
        root.add(header(), BorderLayout.NORTH);
        root.add(sidebar(), BorderLayout.WEST);
        root.add(center(), BorderLayout.CENTER);
        root.add(rightPanel(), BorderLayout.EAST);
        setContentPane(root);
    }

    private JPanel header() {
        JPanel p = new JPanel(new BorderLayout()); p.setBackground(new Color(18, 27, 52)); p.setBorder(new EmptyBorder(18, 24, 18, 24));
        JLabel brand = new JLabel("◈  LMS <span>STUDY</span>"); brand.setFont(new Font("SansSerif", Font.BOLD, 25)); brand.setForeground(text);
        brand.setText("◈  LMS  |  STUDY");
        JLabel user = new JLabel("Нұрқасым Арулан   •   ИС 23-10К   ◉"); user.setForeground(muted); user.setFont(new Font("SansSerif", Font.PLAIN, 14));
        p.add(brand, BorderLayout.WEST); p.add(user, BorderLayout.EAST); return p;
    }

    private JPanel sidebar() {
        JPanel p = new JPanel(); p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS)); p.setBackground(new Color(10, 16, 33)); p.setBorder(new EmptyBorder(24, 16, 24, 16)); p.setPreferredSize(new Dimension(235, 0));
        JLabel nav = label("MAIN MENU", 11, muted); p.add(nav); p.add(Box.createVerticalStrut(16));
        addNavButton(p, "▣  Dashboard", true); addNavButton(p, "▤  Менің курстарым", false); addNavButton(p, "✓  Тапсырмалар", false); addNavButton(p, "▥  Бағалар", false); addNavButton(p, "⚙  Баптаулар", false);
        p.add(Box.createVerticalGlue());
        JLabel pattern = label("PATTERNS ACTIVE", 11, muted); p.add(pattern); p.add(Box.createVerticalStrut(10));
        for (String s : new String[]{"Composite", "Strategy", "Observer", "Template Method", "Factory Method"}) { JLabel x = label("●  " + s, 12, new Color(111, 205, 170)); x.setBorder(new EmptyBorder(5, 8, 5, 0)); p.add(x); }
        return p;
    }

    private void addNavButton(JPanel p, String title, boolean active) {
        JButton b = button(title, active ? blue : new Color(10,16,33), active ? Color.WHITE : muted);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setMaximumSize(new Dimension(205, 43));
        b.addActionListener(e -> {
            String section = title.replaceAll("^[^ ]+\\s*", "").trim();
            dashboardStatus.setText("Dashboard  •  " + section + " ашылды");
            course.notifyObservers(section + " бөлімі таңдалды");
        });
        p.add(b); p.add(Box.createVerticalStrut(7));
    }

    private JPanel center() {
        JPanel wrap = new JPanel(new BorderLayout(0, 14)); wrap.setBackground(navy); wrap.setBorder(new EmptyBorder(26, 25, 22, 22));
        JPanel title = new JPanel(new BorderLayout()); title.setOpaque(false);
        JLabel h = label("Сәлем, Арулан!", 28, text); h.setFont(new Font("SansSerif", Font.BOLD, 28));
        JLabel sub = label("Бүгінгі оқу прогресіңіз", 14, muted); JPanel tp = new JPanel(); tp.setOpaque(false); tp.setLayout(new BoxLayout(tp, BoxLayout.Y_AXIS)); tp.add(h); tp.add(Box.createVerticalStrut(4)); tp.add(sub);
        tp.add(dashboardStatus);
        title.add(tp, BorderLayout.WEST);
        JButton refresh = button("↻  Жаңарту", new Color(41,57,91), text);
        refresh.addActionListener(e -> refreshDashboard());
        title.add(refresh, BorderLayout.EAST); wrap.add(title, BorderLayout.NORTH);
        JPanel content = new JPanel(new BorderLayout(0, 14)); content.setOpaque(false); content.add(stats(), BorderLayout.NORTH); content.add(coursePanel(), BorderLayout.CENTER); wrap.add(content, BorderLayout.CENTER); return wrap;
    }

    private JPanel stats() {
        JPanel p = new JPanel(new GridLayout(1,3,12,0)); p.setOpaque(false);
        p.add(stat("КУРСТАР", "04", "↑ 2 жаңа курс", blue)); p.add(stat("ОРТАША БАҒА", "86%", "↑ 8% осы апта", new Color(111,205,170))); p.add(stat("PROGRESS", "64%", "12 / 18 тапсырма", purple)); return p;
    }
    private JPanel stat(String a, String b, String c, Color accent) { JPanel p = panel(); p.setLayout(new BorderLayout()); p.setBorder(new EmptyBorder(16,18,14,18)); JLabel x=label(a,11,muted); JLabel y=label(b,28,text); y.setFont(new Font("SansSerif",Font.BOLD,28)); JLabel z=label(c,12,accent); p.add(x,BorderLayout.NORTH); p.add(y,BorderLayout.CENTER); p.add(z,BorderLayout.SOUTH); return p; }

    private JPanel coursePanel() {
        JPanel p=panel(); p.setLayout(new BorderLayout(0,14)); p.setBorder(new EmptyBorder(18,18,18,18));
        JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false); JLabel h=label("Менің курсым",20,text); h.setFont(new Font("SansSerif",Font.BOLD,20)); top.add(h,BorderLayout.WEST); JButton addCourse = button("+  Курс қосу", purple, Color.WHITE);
        addCourse.addActionListener(e -> addCourseAction());
        top.add(addCourse,BorderLayout.EAST); p.add(top,BorderLayout.NORTH);
        JPanel courseCard = new JPanel(new BorderLayout(16,0)); courseCard.setBackground(new Color(32,46,82)); courseCard.setBorder(new EmptyBorder(14,16,14,16));
        JLabel icon=label("◉",38,blue); courseCard.add(icon,BorderLayout.WEST); JPanel info=new JPanel(); info.setOpaque(false); info.setLayout(new BoxLayout(info,BoxLayout.Y_AXIS)); JLabel name=label(course.getTitle(),18,text); name.setFont(new Font("SansSerif",Font.BOLD,18)); info.add(name); info.add(label("Java • 2 модуль • Design Patterns",13,muted)); info.add(Box.createVerticalStrut(11)); JProgressBar bar=new JProgressBar(0,100); bar.setValue(64); bar.setForeground(blue); bar.setBackground(new Color(52,66,103)); bar.setStringPainted(true); bar.setString("64% аяқталды"); info.add(bar); courseCard.add(info,BorderLayout.CENTER); JButton open = button("Ашу  →", blue, Color.WHITE);
        open.addActionListener(e -> openCourseAction());
        courseCard.add(open,BorderLayout.EAST); p.add(courseCard,BorderLayout.NORTH);
        JPanel lower=new JPanel(new BorderLayout(0,8)); lower.setOpaque(false); lower.add(label("Тапсырмалар тізімі",14,text),BorderLayout.NORTH); assignmentList.setBackground(new Color(19,29,55)); assignmentList.setForeground(text); assignmentList.setFont(new Font("SansSerif",Font.PLAIN,14)); assignmentList.setFixedCellHeight(39); assignmentList.setBorder(new EmptyBorder(5,8,5,8)); assignmentList.addListSelectionListener(e -> { if(!e.getValueIsAdjusting()){ selectedAssignment=assignmentList.getSelectedValue(); showSelected(); }}); lower.add(new JScrollPane(assignmentList),BorderLayout.CENTER); p.add(lower,BorderLayout.CENTER); return p;
    }

    private JPanel rightPanel() {
        JPanel p=new JPanel(new BorderLayout(0,14)); p.setBackground(new Color(10,16,33)); p.setBorder(new EmptyBorder(26,10,22,20)); p.setPreferredSize(new Dimension(310,0));
        JPanel detail=panel(); detail.setLayout(new BorderLayout(0,12)); detail.setBorder(new EmptyBorder(17,17,17,17)); JLabel hd=label("Тапсырма бөлшектері",17,text); hd.setFont(new Font("SansSerif",Font.BOLD,17)); detail.add(hd,BorderLayout.NORTH); JPanel d=new JPanel(); d.setOpaque(false); d.setLayout(new BoxLayout(d,BoxLayout.Y_AXIS)); selectedTitle.setForeground(text); selectedTitle.setFont(new Font("SansSerif",Font.BOLD,18)); selectedMeta.setForeground(muted); selectedMeta.setFont(new Font("SansSerif",Font.PLAIN,13)); selectedScore.setForeground(blue); selectedScore.setFont(new Font("SansSerif",Font.BOLD,34)); d.add(selectedTitle); d.add(Box.createVerticalStrut(7)); d.add(selectedMeta); d.add(Box.createVerticalStrut(18)); d.add(label("НӘТИЖЕ",11,muted)); d.add(selectedScore); d.add(Box.createVerticalStrut(12)); d.add(label("Бағалау стратегиясы",12,muted)); strategyBox.setMaximumSize(new Dimension(250,34)); strategyBox.addActionListener(e -> changeStrategy()); d.add(strategyBox); detail.add(d,BorderLayout.CENTER); JPanel actions=new JPanel(new GridLayout(3,1,0,8)); actions.setOpaque(false); actions.add(button("▶  Тапсырманы орындау",blue,Color.WHITE)); actions.add(button("✓  Автоматты бағалау",new Color(52,75,119),Color.WHITE)); actions.add(button("＋  Жаңа тапсырма",purple,Color.WHITE)); detail.add(actions,BorderLayout.SOUTH); p.add(detail,BorderLayout.NORTH);
        JPanel notify=panel(); notify.setLayout(new BorderLayout(0,8)); notify.setBorder(new EmptyBorder(15,15,15,15)); notify.add(label("Хабарламалар",15,text),BorderLayout.NORTH); JList<String> list=new JList<>(notificationModel); list.setBackground(new Color(19,29,55)); list.setForeground(muted); list.setFont(new Font("SansSerif",Font.PLAIN,12)); list.setVisibleRowCount(6); notify.add(new JScrollPane(list),BorderLayout.CENTER); JButton clear=button("Тазалау",new Color(41,57,91),muted); clear.addActionListener(e->notificationModel.clear()); notify.add(clear,BorderLayout.SOUTH); p.add(notify,BorderLayout.CENTER); return p;
    }

    private void loadAssignments(Module module) { assignmentModel.clear(); for(LMSComponent c: module.getChildren()) assignmentModel.addElement((Assignment)c); if(assignmentModel.size()>0) assignmentList.setSelectedIndex(0); }
    private void showSelected() { if(selectedAssignment==null)return; selectedTitle.setText(selectedAssignment.getTitle()); selectedMeta.setText(selectedAssignment.getType()+"  •  Java Design Patterns"); selectedScore.setText(selectedAssignment.isCompleted()? selectedAssignment.getScore()+"%" : "—"); }
    private void changeStrategy() {
        int i=strategyBox.getSelectedIndex();
        strategy=i==0?new PercentageStrategy():i==1?new RubricStrategy():new PassFailStrategy();
        course.notifyObservers("Бағалау стратегиясы өзгертілді: "+strategyBox.getSelectedItem());
    }

    private void refreshDashboard() {
        assignmentList.repaint();
        showSelected();
        dashboardStatus.setText("Dashboard  •  Мәліметтер жаңартылды");
        course.notifyObservers("Dashboard жаңартылды");
    }

    private void openCourseAction() {
        dashboardStatus.setText("Dashboard  •  Java Design Patterns курсы ашылды");
        course.notifyObservers("Java Design Patterns курсы ашылды");
        if (assignmentModel.size() > 0) assignmentList.setSelectedIndex(0);
    }

    private void addCourseAction() {
        String name = JOptionPane.showInputDialog(this, "Жаңа курс атауын енгізіңіз:", "Курс қосу", JOptionPane.PLAIN_MESSAGE);
        if (name != null && !name.trim().isEmpty()) {
            dashboardStatus.setText("Dashboard  •  Жаңа курс қосылды: " + name.trim());
            course.notifyObservers("Жаңа курс қосылды: " + name.trim());
        }
    }

    private void submitAssignment() {
        if (selectedAssignment == null) { JOptionPane.showMessageDialog(this, "Алдымен тапсырманы таңдаңыз."); return; }
        String result = new StandardAssignmentWorkflow().submit(selectedAssignment, 80);
        selectedScore.setText("80%");
        assignmentList.repaint();
        dashboardStatus.setText("Dashboard  •  Тапсырма жіберілді");
        course.notifyObservers(result + " | Нәтиже: " + strategy.grade(80));
    }

    private void gradeAssignment() {
        if (selectedAssignment == null) { JOptionPane.showMessageDialog(this, "Бағалау үшін тапсырманы таңдаңыз."); return; }
        String input = JOptionPane.showInputDialog(this, "Ұпайды енгізіңіз (0–100):", "Автоматты бағалау", JOptionPane.PLAIN_MESSAGE);
        if (input == null) return;
        try {
            int score = Integer.parseInt(input.trim());
            if (score < 0 || score > 100) throw new NumberFormatException();
            selectedAssignment.setScore(score);
            selectedScore.setText(score + "%");
            assignmentList.repaint();
            dashboardStatus.setText("Dashboard  •  Баға есептелді: " + strategy.grade(score));
            course.notifyObservers(selectedAssignment.getTitle() + " бағаланды: " + score + "% → " + strategy.grade(score));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "0 мен 100 аралығындағы бүтін сан енгізіңіз.", "Қате", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addAssignment() {
        String[] types = {"Quiz", "Coding", "Essay"};
        String type = (String) JOptionPane.showInputDialog(this, "Тапсырма түрін таңдаңыз:", "Factory Method", JOptionPane.PLAIN_MESSAGE, null, types, types[0]);
        if (type == null) return;
        String title = JOptionPane.showInputDialog(this, "Тапсырма атауын енгізіңіз:", "Жаңа тапсырма", JOptionPane.PLAIN_MESSAGE);
        if (title == null || title.trim().isEmpty()) return;
        AssignmentCreator creator = type.equals("Quiz") ? new QuizCreator() : type.equals("Coding") ? new CodingCreator() : new EssayCreator();
        Assignment created = creator.create(title.trim());
        assignmentModel.addElement(created);
        assignmentList.setSelectedValue(created, true);
        course.notifyObservers("Factory Method арқылы жаңа " + type + " тапсырмасы қосылды");
    }

    private JPanel panel(){ JPanel p=new JPanel(); p.setBackground(panel); p.setBorder(BorderFactory.createLineBorder(new Color(43,58,93))); return p; }
    private JLabel label(String s,int size,Color c){ JLabel l=new JLabel(s); l.setFont(new Font("SansSerif",Font.PLAIN,size)); l.setForeground(c); return l; }
    private JButton button(String s,Color bg,Color fg){ JButton b=new JButton(s); b.setFont(new Font("SansSerif",Font.BOLD,13)); b.setForeground(fg); b.setBackground(bg); b.setFocusPainted(false); b.setBorder(new EmptyBorder(10,14,10,14)); b.setCursor(new Cursor(Cursor.HAND_CURSOR)); return b; }
}
