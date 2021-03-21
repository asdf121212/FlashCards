import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    private JPanel toolPanel;
    private JRadioButton degreesRadioButton = new JRadioButton("Scale Degrees");
    private JCheckBox triadsCheckbox = new JCheckBox("Triads");
    private JCheckBox basicSeventhsCheckbox = new JCheckBox("Basic 7ths");
    private JCheckBox rareSeventhsCheckbox = new JCheckBox("Less Common 7ths");
    private JCheckBox colorTonesCheckbox = new JCheckBox("Extended Chords");
    private JCheckBox inversionsCheckbox = new JCheckBox("Inversions");
    private JRadioButton timerModeRadioButton = new JRadioButton("Timer Mode");
    private JRadioButton spaceModeRadioButton = new JRadioButton("Manual Mode");
    private JSlider timeSlider =  new JSlider(JSlider.HORIZONTAL, 3, 15, 8);

    private JFrame frame;
    private JPanel mainPanel;
    private JLabel text;
    private JLabel inversionText;
    private Random rand = new Random();

    private String[] notes = { "C", "C♯", "D", "E♭", "E", "F", "F♯", "G", "A♭", "A", "B♭", "B" };

    private String[] inversions = { "1st inversion", "2nd inversion", "", "3rd inversion" };

    private String[] triads = { "min", "maj" };
    private String[] normalSevenths = { "maj7", "min7", "7" };
    private String[] rareSevenths = { "dim7", "ø", "min-maj7", "aug7", "7sus", "min7(♭5)" };
    private String[] availableTensions = { "maj9", "maj(♯11)", "maj13(♯11)", "min9", "min11", "min13", "9", "(♭9)", "(♯9)",
            "(♯11)", "(13)", "(♭13)" };

    private String[] degrees = { "2nd", "3rd", "4th", "5th", "6th", "7th" };

    private ArrayList<String> masterList = new ArrayList();

    private Timer cardTimer;
    private KeyAdapter spaceListener;
    private String previousCardString = "";
    private JLabel timerLabel = new JLabel("---");
    private int timerValue = 0;

    public Main() {
        frame = new JFrame("Flash Cards");
        Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(screenDimensions.width / 2 - 600, screenDimensions.height / 2 - 350);
        frame.setSize(1220, 700);
        frame.setPreferredSize(new Dimension(1220, 700));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        frame.setMinimumSize(new Dimension(600, 400));
        frame.setFocusable(true);
        frame.setFocusTraversalKeysEnabled(false);
        frame.requestFocus();

        toolPanel = new JPanel();
        frame.add(toolPanel, BorderLayout.NORTH);
        toolPanel.add(degreesRadioButton);
        toolPanel.add(triadsCheckbox);
        toolPanel.add(basicSeventhsCheckbox);
        toolPanel.add(rareSeventhsCheckbox);
        toolPanel.add(colorTonesCheckbox);
        toolPanel.add(inversionsCheckbox);
        toolPanel.add(timerModeRadioButton);
        toolPanel.add(timerLabel);
        toolPanel.add(timeSlider);
        toolPanel.add(spaceModeRadioButton);

        timeSlider.setMajorTickSpacing(2);
        timeSlider.setMinorTickSpacing(1);
        timeSlider.setPaintLabels(true);
        timeSlider.setPaintTicks(true);
        timeSlider.setSnapToTicks(true);
        timeSlider.setFocusable(false);

        degreesRadioButton.setFocusable(false);
        triadsCheckbox.setFocusable(false);
        basicSeventhsCheckbox.setFocusable(false);
        rareSeventhsCheckbox.setFocusable(false);
        colorTonesCheckbox.setFocusable(false);
        inversionsCheckbox.setFocusable(false);
        timerModeRadioButton.setFocusable(false);
        spaceModeRadioButton.setFocusable(false);

        inversionsCheckbox.setEnabled(false);

        mainPanel = new JPanel();
        text = new JLabel("---");
        frame.add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(text);
        mainPanel.setLayout(null);
        text.setBounds(380, 120, 850, 250);
        text.setFont(new Font("arial", Font.PLAIN, 200));

        inversionText = new JLabel();
        mainPanel.add(inversionText);
        inversionText.setBounds(550, 450, 100, 50);
        inversionText.setFont(new Font("arial", Font.PLAIN, 100));

        frame.pack();

        spaceModeRadioButton.setSelected(true);
        timerModeRadioButton.setSelected(false);

        spaceListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    displayRandomCard();
                }
            }
        };
        frame.addKeyListener(spaceListener);

        ActionListener updateListener = e -> {
            if (!triadsCheckbox.isSelected() && !basicSeventhsCheckbox.isSelected()
                    && !rareSeventhsCheckbox.isSelected() && !colorTonesCheckbox.isSelected()) {
                inversionsCheckbox.setSelected(false);
                inversionsCheckbox.setEnabled(false);
            } else if (!inversionsCheckbox.isEnabled()) {
                inversionsCheckbox.setEnabled(true);
            }
            updateModifierPack();
        };
        degreesRadioButton.addActionListener(e -> {
            if (degreesRadioButton.isSelected()) {
                triadsCheckbox.setEnabled(false);
                basicSeventhsCheckbox.setEnabled(false);
                rareSeventhsCheckbox.setEnabled(false);
                colorTonesCheckbox.setEnabled(false);
                inversionsCheckbox.setEnabled(false);
                triadsCheckbox.setSelected(false);
                basicSeventhsCheckbox.setSelected(false);
                rareSeventhsCheckbox.setSelected(false);
                colorTonesCheckbox.setSelected(false);
                inversionsCheckbox.setSelected(false);
                text.setFont(new Font("arial", Font.PLAIN, 125));
                inversionText.setText("");
            } else {
                triadsCheckbox.setEnabled(true);
                basicSeventhsCheckbox.setEnabled(true);
                rareSeventhsCheckbox.setEnabled(true);
                colorTonesCheckbox.setEnabled(true);
                inversionsCheckbox.setEnabled(true);
                text.setFont(new Font("arial", Font.PLAIN, 200));
            }
        });
        triadsCheckbox.addActionListener(updateListener);
        basicSeventhsCheckbox.addActionListener(updateListener);
        rareSeventhsCheckbox.addActionListener(updateListener);
        colorTonesCheckbox.addActionListener(updateListener);
        timerModeRadioButton.addActionListener(e -> {
            spaceModeRadioButton.setSelected(false);
            timerValue = timeSlider.getValue();
            timerLabel.setText(String.valueOf(timerValue));
            cardTimer = new Timer(1000, t -> {
                timerValue--;
                timerLabel.setText(String.valueOf(timerValue));
                if (timerValue == 0) {
                    displayRandomCard();
                    timerValue = timeSlider.getValue();
                    timerLabel.setText(String.valueOf(timerValue));
                }
            });
            cardTimer.start();
            frame.removeKeyListener(spaceListener);
        });
        spaceModeRadioButton.addActionListener(e -> {
            timerModeRadioButton.setSelected(false);
            cardTimer.stop();
            frame.addKeyListener(spaceListener);
            timerLabel.setText("---");
        });
    }

    private void addAll(ArrayList<String> arrayList, String[] array) {
        for (int i = 0; i < array.length; i++) {
            arrayList.add(array[i]);
        }
    }

    private void updateModifierPack() {
        masterList = new ArrayList<>();
        if (triadsCheckbox.isSelected()) {
            addAll(masterList, triads);
        }
        if (basicSeventhsCheckbox.isSelected()) {
            addAll(masterList, normalSevenths);
        }
        if (rareSeventhsCheckbox.isSelected()) {
            addAll(masterList, rareSevenths);
        }
        if (colorTonesCheckbox.isSelected()) {
            addAll(masterList, availableTensions);
        }
    }

    private void displayRandomCard() {
        if (degreesRadioButton.isSelected()) {
            String cardString = "";
            cardString += notes[rand.nextInt(notes.length)];
            cardString += triads[rand.nextInt(2)];
            cardString += " " + degrees[rand.nextInt(degrees.length)] + " deg";
            if (previousCardString.equals(cardString)) {
                displayRandomCard();
            } else {
                text.setText(cardString);
                int width = text.getGraphics().getFontMetrics().stringWidth(text.getText());
                text.setBounds(600 - width / 2, 120, width + 10, 250);
                previousCardString = cardString;
            }
        } else {
            String cardString = "";
            String note = notes[rand.nextInt(notes.length)];
            String modifier = "";
            if (masterList.size() > 0) {
                modifier = masterList.get(rand.nextInt(masterList.size()));
            }
            String inversion = "";
            if (inversionsCheckbox.isSelected()) {
                if (modifier.equals("maj") || modifier.equals("min")) {
                    inversion = inversions[rand.nextInt(3)];
                } else {
                    inversion = inversions[rand.nextInt(4)];
                }
                inversionText.setText(inversion);
                int width = inversionText.getGraphics().getFontMetrics().stringWidth(inversionText.getText());
                inversionText.setBounds(600 - width / 2, 450, width + 10, 100);
            } else {
                inversionText.setText("");
            }
            cardString += note;
            cardString += modifier;
            if (previousCardString.equals(cardString)) {
                displayRandomCard();
            } else {
                text.setText(cardString);
                int width = text.getGraphics().getFontMetrics().stringWidth(text.getText());
                text.setBounds(600 - width / 2, 120, width + 10, 250);
                previousCardString = cardString;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

}
