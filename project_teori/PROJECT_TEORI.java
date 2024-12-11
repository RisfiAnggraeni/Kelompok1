package project_teori;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

abstract class KalkulatorBase {
    private String nama;
    private String versi;

    public KalkulatorBase(String nama, String versi) {
        this.nama = nama;
        this.versi = versi;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getVersi() {
        return versi;
    }

    public void setVersi(String versi) {
        this.versi = versi;
    }

    public abstract void tampilkanJenis();
}

class KalkulatorSederhana extends KalkulatorBase {
    public KalkulatorSederhana(String nama, String versi) {
        super(nama, versi);
    }

    @Override
    public void tampilkanJenis() {
        System.out.println("Ini adalah Kalkulator Sederhana.");
    }
}

class KalkulatorLanjutan extends KalkulatorBase {
    public KalkulatorLanjutan(String nama, String versi) {
        super(nama, versi);
    }

    @Override
    public void tampilkanJenis() {
        System.out.println("Ini adalah Kalkulator Lanjutan.");
    }
}

public class Kalkulator extends JFrame implements ActionListener {
    private JTextField display;
    private StringBuilder input;
    private static int operasiDihitung = 0;

    public Kalkulator() {
        input = new StringBuilder();
        setTitle("Kalkulator");
        setSize(400, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.BOLD, 24));
        display.setHorizontalAlignment(JTextField.RIGHT);
        add(display, BorderLayout.NORTH);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 4, 5, 5));

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

        add(panel, BorderLayout.CENTER);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();

        try {
            if (command.equals("C")) {
                input.setLength(0);
                display.setText("");
            } else if (command.equals("=")) {
                if (input.length() == 0) {
                    display.setText("No Input");
                    return;
                }

                double result = eval(input.toString());
                if (result == (int) result) {
                    display.setText(String.valueOf((int) result));
                } else {
                    display.setText(String.valueOf(result));
                }

                input.setLength(0);
                input.append(result);
                operasiDihitung++;
            } else {
                input.append(command);
                display.setText(input.toString());
            }
        } catch (ArithmeticException ex) {
            display.setText("Math Error");
            input.setLength(0);
        } catch (RuntimeException ex) {
            display.setText("Invalid Input");
            input.setLength(0);
        }
    }

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
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }
        }.parse();
    }

    public static int getOperasiDihitung() {
        return operasiDihitung;
    }

    public static void main(String[] args) {
        try {
            SwingUtilities.invokeLater(() -> {
                Kalkulator kalkulator = new Kalkulator();
                kalkulator.setVisible(true);
            });

            KalkulatorBase kalkulator1 = new KalkulatorSederhana("Kalkulator Sederhana", "1.0");
            kalkulator1.tampilkanJenis();

            KalkulatorBase kalkulator2 = new KalkulatorLanjutan("Kalkulator Lanjutan", "2.0");
            kalkulator2.tampilkanJenis();

            System.out.println("Total operasi dihitung: " + getOperasiDihitung());
        } catch (Exception e) {
            System.out.println("Terjadi kesalahan: " + e.getMessage());
        }
    }
}
  
