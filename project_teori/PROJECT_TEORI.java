package project_teori;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Kalkulator extends JFrame implements ActionListener {
    private JTextField display;
    private StringBuilder input;

    public Kalkulator() {
        input = new StringBuilder();

        // Mengatur frame
        setTitle("Kalkulator Sederhana");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Membuat display (layar tampilan)
        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        add(display, BorderLayout.NORTH);

        // Membuat panel tombol
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4, 5, 5));

        // Menambahkan tombol
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", "C", "=", "+"
        };

        for (String b : buttons) {
            JButton button = new JButton(b);
            button.setFont(new Font("Arial", Font.BOLD, 20));
            button.addActionListener(this);
            panel.add(button);
        }

        // Menambahkan panel ke frame
        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        if (command.equals("C")) {
            // Clear input
            input.setLength(0);
            display.setText("");
        } else if (command.equals("=")) {
            // Evaluasi ekspresi
            try {
                if (input.length() == 0) {
                    display.setText("No Input");
                    return;
                }
                double result = eval(input.toString());
                display.setText(String.valueOf(result));
                input.setLength(0);
                input.append(result);
            } catch (ArithmeticException ex) {
                display.setText("Math Error");
                input.setLength(0);
            } catch (RuntimeException ex) {
                display.setText("Invalid Expression");
                input.setLength(0);
            } catch (Exception ex) {
                display.setText("Error");
                input.setLength(0);
            }
        } else {
            // Menambahkan input ke layar
            input.append(command);
            display.setText(input.toString());
        }
    }

    // Fungsi untuk evaluasi ekspresi matematika
    private double eval(String expression) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm(); // Penjumlahan
                    else if (eat('-')) x -= parseTerm(); // Pengurangan
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor(); // Perkalian
                    else if (eat('/')) x /= parseFactor(); // Pembagian
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor(); // Unary plus
                if (eat('-')) return -parseFactor(); // Unary minus

                double x;
                int startPos = this.pos;
                if (eat('(')) { // Kurung
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') { // Angka
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }
        }.parse();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Kalkulator kalkulator = new Kalkulator();
            kalkulator.setVisible(true);
        });
    }
}
