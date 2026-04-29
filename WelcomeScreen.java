import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class WelcomeScreen extends JFrame {

    private JPanel cardPanel;
    private CardLayout cardLayout;
    private JButton prevBtn, nextBtn;
    private JLabel pageIndicator;
    private int currentPage = 0;
    private static final int TOTAL_PAGES = 3;

    private Runnable onStartGame;

    public WelcomeScreen(Runnable onStartGame) {
        this.onStartGame = onStartGame;
        setTitle("Island Ecosystem Survival");
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.add(buildPage1(), "page1");
        cardPanel.add(buildPage2(), "page2");
        cardPanel.add(buildPage3(), "page3");
        add(cardPanel, BorderLayout.CENTER);

        add(buildNavBar(), BorderLayout.SOUTH);
        updateNav();
    }

    // ── Page 1: Story intro ──────────────────────────────────────────────────

    private JPanel buildPage1() {
        JPanel p = makePagePanel();

        p.add(makeTag("Welcome"));
        p.add(makeTitle("Island Ecosystem Survival"));
        p.add(Box.createVerticalStrut(8));
        p.add(makeBody("Your ship has wrecked. You are the last leader standing,\n" +
                "and your group is stranded on a resource-rich island.\n" +
                "Every night you must make a decision to survive —\n" +
                "but every decision comes at a cost."));

        p.add(Box.createVerticalStrut(16));

        JPanel cards = new JPanel(new GridLayout(1, 2, 12, 0));
        cards.setOpaque(false);
        cards.setMaximumSize(new Dimension(560, 110));
        cards.add(makeInfoCard("The catch",
                "Every survival action drives a species extinct. When a\n" +
                "species dies, its predators slowly starve — 3 nights later."));
        cards.add(makeInfoCard("The end",
                "When all consumers are gone the food web collapses.\n" +
                "Death is inevitable — delay it as long as you can."));
        p.add(cards);

        return p;
    }

    // ── Page 2: Gameplay ────────────────────────────────────────────────────

    private JPanel buildPage2() {
        JPanel p = makePagePanel();

        p.add(makeTag("Gameplay"));
        p.add(makeTitle("How each night works"));
        p.add(Box.createVerticalStrut(8));
        p.add(makeBody("Each night plays out across three popup windows.\n" +
                "Choose wisely — repeat a dead-end path and you pay the price."));

        p.add(Box.createVerticalStrut(16));

        JPanel steps = new JPanel(new GridLayout(1, 3, 12, 0));
        steps.setOpaque(false);
        steps.setMaximumSize(new Dimension(560, 100));
        steps.add(makeInfoCard("Step 1", "Choose a survival action: build traps, go fishing, or scavenge."));
        steps.add(makeInfoCard("Step 2", "Choose a location. Each spot targets a different species."));
        steps.add(makeInfoCard("Step 3", "See results: which species went extinct and what cascades next."));
        p.add(steps);

        p.add(Box.createVerticalStrut(16));

        JPanel scoring = new JPanel(new GridLayout(1, 2, 12, 0));
        scoring.setOpaque(false);
        scoring.setMaximumSize(new Dimension(560, 70));
        scoring.add(makeScoreCard("+100", "per night survived", new Color(234, 243, 222), new Color(59, 109, 17)));
        scoring.add(makeScoreCard("−50", "if you revisit an extinct path — you go hungry", new Color(250, 236, 231), new Color(153, 60, 29)));
        p.add(scoring);

        p.add(Box.createVerticalStrut(10));
        JLabel chain = new JLabel("  Species extinct  →  3 nights later  →  predators starve");
        chain.setFont(new Font("Arial", Font.PLAIN, 13));
        chain.setForeground(Color.GRAY);
        chain.setAlignmentX(Component.CENTER_ALIGNMENT);
        p.add(chain);

        return p;
    }

    // ── Page 3: Species list ─────────────────────────────────────────────────

    private JPanel buildPage3() {
        JPanel p = makePagePanel();

        p.add(makeTag("The Food Web"));
        p.add(makeTitle("Species on the island"));
        p.add(Box.createVerticalStrut(8));
        p.add(makeBody("11 species stand between you and starvation.\n" +
                "Producers never die out — everything else is fair game."));

        p.add(Box.createVerticalStrut(16));

        JPanel grid = new JPanel(new GridLayout(3, 2, 10, 10));
        grid.setOpaque(false);
        grid.setMaximumSize(new Dimension(560, 180));

        grid.add(makeTierCard("Apex predator", "American Crocodile", new Color(250, 236, 231)));
        grid.add(makeTierCard("Tertiary consumers", "Bobcat · Wild Boar", new Color(250, 236, 231)));
        grid.add(makeTierCard("Secondary consumers", "Raccoon · Pelican · Sea Star · Sea Turtle", new Color(230, 241, 251)));
        grid.add(makeTierCard("Primary consumers", "Clam · Armadillo · Blue Crab · Fiddler Crab", new Color(230, 241, 251)));

        // Producers span both columns via a wrapper
        JPanel producerWrapper = new JPanel(new GridLayout(1, 1));
        producerWrapper.setOpaque(false);
        producerWrapper.add(makeTierCard("Producers (unkillable base)", "Mangroves · Sea Oats · Wildflowers", new Color(234, 243, 222)));
        
        // Fake second cell so the grid stays even
        JPanel empty = new JPanel();
        empty.setOpaque(false);

        grid.add(producerWrapper);
        grid.add(empty);

        p.add(grid);

        return p;
    }

    // ── Nav bar ──────────────────────────────────────────────────────────────

    private JPanel buildNavBar() {
        JPanel nav = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        nav.setBackground(UIManager.getColor("Panel.background"));

        prevBtn = new JButton("←");
        nextBtn = new JButton("→");
        pageIndicator = new JLabel("1 / 3");
        pageIndicator.setFont(new Font("Arial", Font.PLAIN, 13));
        pageIndicator.setForeground(Color.GRAY);

        styleNavBtn(prevBtn, e -> navigate(-1));
        styleNavBtn(nextBtn, e -> navigate(1));

        nav.add(prevBtn);
        nav.add(pageIndicator);
        nav.add(nextBtn);
        return nav;
    }

    private void styleNavBtn(JButton btn, ActionListener al) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setFocusPainted(false);
        btn.addActionListener(al);
    }

    private void navigate(int dir) {
        currentPage = Math.max(0, Math.min(TOTAL_PAGES - 1, currentPage + dir));

        if (currentPage == TOTAL_PAGES - 1 && dir == 1) {
            // On the last page's forward press, transition to game
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Ready to begin your fight for survival?",
                    "Start Game", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                setVisible(false);
                dispose();
                onStartGame.run();
                return;
            }
        }

        cardLayout.show(cardPanel, "page" + (currentPage + 1));
        updateNav();
    }

    private void updateNav() {
        prevBtn.setEnabled(currentPage > 0);
        nextBtn.setText(currentPage == TOTAL_PAGES - 1 ? "Play!" : "→");
        pageIndicator.setText((currentPage + 1) + " / " + TOTAL_PAGES);
    }

    // ── Component helpers ────────────────────────────────────────────────────

    private JPanel makePagePanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(32, 48, 20, 48));
        p.setAlignmentX(Component.CENTER_ALIGNMENT);
        return p;
    }

    private JLabel makeTag(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("Arial", Font.PLAIN, 11));
        l.setForeground(Color.GRAY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JLabel makeTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("Arial", Font.BOLD, 20));
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        return l;
    }

    private JTextArea makeBody(String text) {
        JTextArea ta = new JTextArea(text);
        ta.setEditable(false);
        ta.setOpaque(false);
        ta.setFont(new Font("Arial", Font.PLAIN, 14));
        ta.setForeground(Color.DARK_GRAY);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        ta.setAlignmentX(Component.CENTER_ALIGNMENT);
        ta.setMaximumSize(new Dimension(520, 80));
        return ta;
    }

    private JPanel makeInfoCard(String title, String body) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        JLabel t = new JLabel(title.toUpperCase());
        t.setFont(new Font("Arial", Font.BOLD, 10));
        t.setForeground(Color.GRAY);
        card.add(t);
        card.add(Box.createVerticalStrut(5));

        JTextArea b = new JTextArea(body);
        b.setEditable(false);
        b.setOpaque(false);
        b.setFont(new Font("Arial", Font.PLAIN, 13));
        b.setLineWrap(true);
        b.setWrapStyleWord(true);
        card.add(b);

        return card;
    }

    private JPanel makeScoreCard(String number, String desc, Color bg, Color fg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(fg.brighter(), 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)));

        JLabel num = new JLabel(number);
        num.setFont(new Font("Arial", Font.BOLD, 24));
        num.setForeground(fg);
        num.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(num);

        JTextArea d = new JTextArea(desc);
        d.setEditable(false);
        d.setOpaque(false);
        d.setFont(new Font("Arial", Font.PLAIN, 12));
        d.setForeground(fg);
        d.setLineWrap(true);
        d.setWrapStyleWord(true);
        d.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(d);

        return card;
    }

    private JPanel makeTierCard(String tier, String species, Color bg) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        JLabel t = new JLabel(tier.toUpperCase());
        t.setFont(new Font("Arial", Font.BOLD, 10));
        t.setForeground(Color.DARK_GRAY);
        card.add(t);
        card.add(Box.createVerticalStrut(3));

        JLabel s = new JLabel("<html>" + species + "</html>");
        s.setFont(new Font("Arial", Font.PLAIN, 13));
        s.setForeground(Color.DARK_GRAY);
        card.add(s);

        return card;
    }
}