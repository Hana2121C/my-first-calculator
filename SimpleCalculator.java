import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleCalculator extends JFrame implements ActionListener {
    private final JTextField display = new JTextField("0");
    private double result = 0;
    private String lastOp = "=";
    private boolean startNewNumber = true;

    public SimpleCalculator() {
        setTitle("Simple Calculator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 400);
        setLocationRelativeTo(null);

        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setFont(new Font("SansSerif", Font.PLAIN, 28));
        display.setEditable(false);
        display.setBackground(Color.WHITE);

        JPanel buttonPanel = new JPanel(new GridLayout(5, 4, 5, 5));
        String[] buttons = {
            "C", "⌫", "/", "*",
            "7", "8", "9", "-",
            "4", "5", "6", "+",
            "1", "2", "3", "=",
            "0", "0", ".", "=" // spacer 0 repeated to make layout symmetric
        };

        // create buttons (we'll add correct actions below)
        for (String b : buttons) {
            if (b.equals("0")) { // first "0" will be real, second is spacer
                JButton btn = new JButton("0");
                btn.setFont(new Font("SansSerif", Font.PLAIN, 20));
                btn.addActionListener(this);
                buttonPanel.add(btn);
                // switch to treat next "0" as spacer
                buttons = replaceNextZeroWithSpacer(buttons);
            } else if (b.equals("=") && buttonPanel.getComponentCount() >= 15) {
                // place one "=" at its proper grid spot, others were placeholder
                JButton btn = new JButton("=");
                btn.setFont(new Font("SansSerif", Font.PLAIN, 20));
                btn.addActionListener(this);
                buttonPanel.add(btn);
            } else {
                JButton btn = new JButton(b);
                btn.setFont(new Font("SansSerif", Font.PLAIN, 20));
                btn.addActionListener(this);
                buttonPanel.add(btn);
            }
        }

        // layout
        JPanel main = new JPanel(new BorderLayout(5,5));
        main.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));
        main.add(display, BorderLayout.NORTH);
        main.add(buttonPanel, BorderLayout.CENTER);

        add(main);
        setVisible(true);
    }

    // helper to avoid duplicate '0' creation for grid arrangement
    private String[] replaceNextZeroWithSpacer(String[] arr) {
        boolean replaced = false;
        for (int i = 0; i < arr.length; i++) {
            if (!replaced && arr[i].equals("0")) {
                arr[i] = ""; // spacer marker
                replaced = true;
                break;
            }
        }
        return arr;
    }

    public void actionPerformed(ActionEvent e) {
        String cmd = ((JButton)e.getSource()).getText();

        if ("0123456789.".contains(cmd)) {
            if (startNewNumber) {
                display.setText(cmd.equals(".") ? "0." : cmd);
                startNewNumber = false;
            } else {
                if (cmd.equals(".") && display.getText().contains(".")) return;
                display.setText(display.getText() + cmd);
            }
        } else if (cmd.equals("C")) {
            display.setText("0");
            result = 0;
            lastOp = "=";
            startNewNumber = true;
        } else if (cmd.equals("⌫")) {
            if (!startNewNumber) {
                String t = display.getText();
                if (t.length() <= 1) {
                    display.setText("0");
                    startNewNumber = true;
                } else {
                    display.setText(t.substring(0, t.length()-1));
                }
            }
        } else { // operators and =
            try {
                double x = Double.parseDouble(display.getText());
                calculate(x);
                lastOp = cmd.equals("") ? lastOp : cmd;
                startNewNumber = true;
                display.setText(removeTrailingDotZero(result));
            } catch (NumberFormatException ex) {
                display.setText("Error");
                startNewNumber = true;
            }
        }
    }

    private void calculate(double x) {
        switch (lastOp) {
            case "+": result += x; break;
            case "-": result -= x; break;
            case "*": result *= x; break;
            case "/":
                if (x == 0) { display.setText("Divide by 0"); startNewNumber = true; result = 0; lastOp = "="; return; }
                result /= x; break;
            case "=": result = x; break;
        }
    }

    private String removeTrailingDotZero(double v) {
        if (v == (long)v) return String.format("%d", (long)v);
        else return String.valueOf(v);
    }

    public static void main(String[] args) {
        // Ensure GUI created on Event Dispatch Thread
        SwingUtilities.invokeLater(SimpleCalculator::new);
    }
}
