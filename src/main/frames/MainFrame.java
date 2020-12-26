package main.frames;

import main.misc.Tasks;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;

public class MainFrame extends JFrame {
    private final JComboBox<String> combo = new JComboBox<>(Arrays.stream(Tasks.values()).map(Tasks::getName).toArray(String[]::new));
    private final JButton btn = new JButton("Go !");

    public MainFrame() {
        super();
        goManager();
        this.setTitle("Tasks manager");
        this.setPreferredSize(new Dimension(400, 150));
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new FlowLayout());

        JPanel topPanel = new JPanel();
        JLabel label = new JLabel("Select task to launch : ");
        topPanel.add(label);
        combo.setPreferredSize(new Dimension(200, 20));
        topPanel.add(combo);
        topPanel.setPreferredSize(new Dimension(400, 50));
        JPanel container = new JPanel();
        container.add(topPanel);
        container.add(btn);

        combo.addActionListener(e -> {
            String selectedTask = getCombo().getSelectedItem().toString();
            final Tasks task = Tasks.valueOf(selectedTask);
            btn.setEnabled(task.isClickableTask());
        });

        this.requestFocus();
        this.setContentPane(container);
        this.setVisible(true);
        this.pack();
    }

    private void goManager() {
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "Go");
        getRootPane().getActionMap().put("Go", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                String selectedTask = getCombo().getSelectedItem().toString();
                if (Tasks.valueOf(selectedTask).isClickableTask()) {
                    getBtn().doClick();
                }
            }
        });

        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "Quit");
        getRootPane().getActionMap().put("Quit", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    public JComboBox<String> getCombo() {
        return this.combo;
    }

    public JButton getBtn() {
        return this.btn;
    }

    public void resetCombo() {
        combo.setSelectedIndex(0);
        combo.requestFocus();
    }
}
