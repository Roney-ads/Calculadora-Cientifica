import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class CalculadoraMinimalista extends JFrame {
    private JTextField display;
    private double num1 = 0, num2 = 0;
    private String operador = "";
    private boolean novoNumero = true;

    public CalculadoraMinimalista() {
        setTitle("Calculadora");
        setSize(380, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(false);
        
        // Painel principal com cor de fundo
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 240, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Display
        display = new JTextField("0");
        display.setFont(new Font("Arial", Font.BOLD, 36));
        display.setHorizontalAlignment(JTextField.RIGHT);
        display.setEditable(false);
        display.setBackground(Color.WHITE);
        display.setForeground(new Color(50, 50, 50));
        display.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        display.setPreferredSize(new Dimension(340, 80));
        mainPanel.add(display, BorderLayout.NORTH);
        
        // Painel de botões
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 4, 10, 10));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        // Botões
        String[] botoes = {
            "C", "←", "x²", "√",
            "7", "8", "9", "÷",
            "4", "5", "6", "×",
            "1", "2", "3", "-",
            "0", ".", "=", "+",
            "Primo", "", "", ""
        };
        
        for (String texto : botoes) {
            if (texto.isEmpty()) {
                JPanel empty = new JPanel();
                empty.setBackground(new Color(240, 240, 245));
                buttonPanel.add(empty);
                continue;
            }
            
            JButton btn = criarBotao(texto);
            buttonPanel.add(btn);
            
            btn.addActionListener(e -> processarBotao(texto));
        }
        
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
        
        // Força a janela a aparecer
        pack();
        setSize(380, 550);
    }
    
    private JButton criarBotao(String texto) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Cor de fundo do botão
                Color corFundo;
                if (getModel().isPressed()) {
                    corFundo = getCorBotao(getText()).darker();
                } else if (getModel().isRollover()) {
                    corFundo = getCorBotao(getText()).brighter();
                } else {
                    corFundo = getCorBotao(getText());
                }
                
                g2.setColor(corFundo);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                
                // Texto
                g2.setColor(getCorTexto(getText()));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2.drawString(getText(), x, y);
                
                g2.dispose();
            }
        };
        
        btn.setFont(new Font("Arial", Font.BOLD, 18));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(70, 55));
        
        return btn;
    }
    
    private Color getCorBotao(String texto) {
        if (texto.equals("=")) {
            return new Color(76, 175, 80); // Verde
        } else if (texto.equals("C") || texto.equals("←")) {
            return new Color(244, 67, 54); // Vermelho
        } else if (texto.equals("Primo")) {
            return new Color(103, 58, 183); // Roxo
        } else if ("÷×-+x²√".contains(texto)) {
            return new Color(255, 152, 0); // Laranja
        } else {
            return Color.WHITE;
        }
    }
    
    private Color getCorTexto(String texto) {
        if ("0123456789.".contains(texto)) {
            return new Color(50, 50, 50);
        }
        return Color.WHITE;
    }
    
    private void processarBotao(String texto) {
        try {
            switch (texto) {
                case "C":
                    display.setText("0");
                    num1 = num2 = 0;
                    operador = "";
                    novoNumero = true;
                    break;
                    
                case "←":
                    String atual = display.getText();
                    if (atual.length() > 1) {
                        display.setText(atual.substring(0, atual.length() - 1));
                    } else {
                        display.setText("0");
                    }
                    break;
                    
                case "√":
                    double val = Double.parseDouble(display.getText());
                    if (val < 0) {
                        display.setText("Erro");
                    } else {
                        display.setText(formatarResultado(Math.sqrt(val)));
                    }
                    novoNumero = true;
                    break;
                    
                case "x²":
                    val = Double.parseDouble(display.getText());
                    display.setText(formatarResultado(Math.pow(val, 2)));
                    novoNumero = true;
                    break;
                    
                case "Primo":
                    int numero = (int) Double.parseDouble(display.getText());
                    if (ehPrimo(numero)) {
                        JOptionPane.showMessageDialog(this, numero + " é PRIMO!", 
                            "Verificação de Primo", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, numero + " NÃO é primo", 
                            "Verificação de Primo", JOptionPane.INFORMATION_MESSAGE);
                    }
                    break;
                    
                case "+":
                case "-":
                case "×":
                case "÷":
                    num1 = Double.parseDouble(display.getText());
                    operador = texto;
                    novoNumero = true;
                    break;
                    
                case "=":
                    num2 = Double.parseDouble(display.getText());
                    double resultado = calcular();
                    display.setText(formatarResultado(resultado));
                    num1 = resultado;
                    novoNumero = true;
                    break;
                    
                case ".":
                    if (!display.getText().contains(".")) {
                        display.setText(display.getText() + ".");
                    }
                    novoNumero = false;
                    break;
                    
                default: // Números
                    if (novoNumero || display.getText().equals("0")) {
                        display.setText(texto);
                        novoNumero = false;
                    } else {
                        display.setText(display.getText() + texto);
                    }
                    break;
            }
        } catch (Exception e) {
            display.setText("Erro");
            novoNumero = true;
        }
    }
    
    private double calcular() {
        switch (operador) {
            case "+": return num1 + num2;
            case "-": return num1 - num2;
            case "×": return num1 * num2;
            case "÷": return num2 != 0 ? num1 / num2 : 0;
            default: return num2;
        }
    }
    
    private String formatarResultado(double valor) {
        if (valor == (long) valor) {
            return String.format("%d", (long) valor);
        }
        return String.format("%.8f", valor).replaceAll("0*$", "").replaceAll("\\.$", "");
    }
    
    private boolean ehPrimo(int n) {
        if (n <= 1) return false;
        if (n <= 3) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        
        for (int i = 5; i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) {
                return false;
            }
        }
        return true;
    }
    
    public static void main(String[] args) {
        // Configura o look and feel do sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Cria e exibe a calculadora na thread do Swing
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                CalculadoraMinimalista calc = new CalculadoraMinimalista();
                calc.setVisible(true);
            }
        });
    }
}