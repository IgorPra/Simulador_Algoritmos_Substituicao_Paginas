import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class SimuladorSwing extends JFrame {
    private final JTextField campoPaginas;
    private final JTextField campoQuadros;
    private final JComboBox<ModeloExecucao> comboModelos;
    private final JTextArea areaResultado;
    private final GraficoBarrasPanel graficoPanel;
    private final PageReplacementSimulator simulador;

    public SimuladorSwing() {
        simulador = new PageReplacementSimulator();

        setTitle("Simulador de Substituicao de Paginas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(780, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        campoPaginas = new JTextField();
        campoQuadros = new JTextField();
        comboModelos = new JComboBox<>();
        areaResultado = new JTextArea();
        graficoPanel = new GraficoBarrasPanel();

        carregarModelos();
        configurarAreaResultado();

        add(criarPainelEntrada(), BorderLayout.NORTH);
        add(new JScrollPane(areaResultado), BorderLayout.CENTER);
        add(graficoPanel, BorderLayout.SOUTH);

        preencherCamposPeloModelo();
    }

    private JPanel criarPainelEntrada() {
        JPanel painel = new JPanel(new BorderLayout(8, 8));
        painel.setBorder(BorderFactory.createEmptyBorder(12, 12, 4, 12));

        // Organiza a parte superior da tela em tres blocos: modelos, campos e botao.
        painel.add(criarPainelModelos(), BorderLayout.NORTH);
        painel.add(criarPainelCampos(), BorderLayout.CENTER);
        painel.add(criarPainelBotoes(), BorderLayout.SOUTH);

        return painel;
    }

    private JPanel criarPainelModelos() {
        JPanel painel = new JPanel(new BorderLayout(6, 6));
        painel.setBorder(BorderFactory.createTitledBorder("Modelos de Execução"));

        // Ao escolher um modelo, os campos sao preenchidos automaticamente.
        comboModelos.addActionListener(evento -> preencherCamposPeloModelo());

        painel.add(new JLabel("Selecione um modelo:"), BorderLayout.WEST);
        painel.add(comboModelos, BorderLayout.CENTER);

        return painel;
    }

    private JPanel criarPainelCampos() {
        JPanel painel = new JPanel(new GridLayout(2, 2, 8, 8));
        painel.setBorder(BorderFactory.createTitledBorder("Dados da Simulacao"));

        // Campos livres: o usuario pode alterar os valores mesmo depois de selecionar um modelo.
        painel.add(new JLabel("Sequencia de paginas:"));
        painel.add(campoPaginas);
        painel.add(new JLabel("Quantidade de quadros:"));
        painel.add(campoQuadros);

        return painel;
    }

    private JPanel criarPainelBotoes() {
        JPanel painel = new JPanel(new GridLayout(1, 1, 8, 8));
        JButton botaoSimular = new JButton("Simular");

        botaoSimular.addActionListener(evento -> simular());
        painel.add(botaoSimular);

        return painel;
    }

    private void carregarModelos() {
        comboModelos.addItem(new ModeloExecucao(
                "Modelo 1",
                "7,0,1,2,0,3,0,4,2,3,0,3,2",
                3
        ));
        comboModelos.addItem(new ModeloExecucao(
                "Modelo 2",
                "1,2,3,4,1,2,5,1,2,3,4,5",
                4
        ));
        comboModelos.addItem(new ModeloExecucao(
                "Modelo 3",
                "4,5,8,3,6,1,4",
                2
        ));
        comboModelos.addItem(new ModeloExecucao(
                "Modelo 4",
                "2,3,2,1,5,2,4,5,3,2,5,2",
                3
        ));
    }

    private void preencherCamposPeloModelo() {
        ModeloExecucao modelo = (ModeloExecucao) comboModelos.getSelectedItem();

        if (modelo == null) {
            return;
        }

        campoPaginas.setText(modelo.getSequencia());
        campoQuadros.setText(String.valueOf(modelo.getQuadros()));
    }

    private void configurarAreaResultado() {
        areaResultado.setEditable(false);
        areaResultado.setFont(new Font("Monospaced", Font.PLAIN, 14));
        areaResultado.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    private void simular() {
        List<Integer> paginas = converterSequencia(campoPaginas.getText());

        if (paginas.isEmpty()) {
            JOptionPane.showMessageDialog(this, "A sequencia de paginas nao pode estar vazia.");
            return;
        }

        int quadros = lerQuantidadeQuadros();

        if (quadros <= 0) {
            return;
        }

        int[] valores = {
            simulador.fifo(paginas, quadros),
            simulador.lru(paginas, quadros),
            simulador.clock(paginas, quadros),
            simulador.otimo(paginas, quadros)
        };
        String[] nomes = {"FIFO", "LRU", "Relogio", "Otimo"};

        // Atualiza a area textual e o grafico com os resultados calculados.
        areaResultado.setText(montarTextoResultado(nomes, valores));
        graficoPanel.atualizarDados(nomes, valores);
    }

    private int lerQuantidadeQuadros() {
        try {
            int quadros = Integer.parseInt(campoQuadros.getText().trim());

            if (quadros <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade de quadros deve ser maior que zero.");
                return -1;
            }

            return quadros;
        } catch (NumberFormatException erro) {
            JOptionPane.showMessageDialog(this, "A quantidade de quadros deve ser um numero inteiro.");
            return -1;
        }
    }

    private List<Integer> converterSequencia(String entrada) {
        List<Integer> paginas = new ArrayList<>();

        if (entrada == null || entrada.trim().isEmpty()) {
            return paginas;
        }

        String[] valores = entrada.split(",");

        for (String valor : valores) {
            String texto = valor.trim();

            if (texto.isEmpty()) {
                continue;
            }

            try {
                paginas.add(Integer.parseInt(texto));
            } catch (NumberFormatException erro) {
                JOptionPane.showMessageDialog(this, "Valor invalido na sequencia: " + texto);
                return new ArrayList<>();
            }
        }

        return paginas;
    }

    private String montarTextoResultado(String[] nomes, int[] valores) {
        StringBuilder texto = new StringBuilder();

        texto.append("FIFO - ").append(valores[0]).append(" faltas de pagina\n");
        texto.append("LRU - ").append(valores[1]).append(" faltas de pagina\n");
        texto.append("Relogio - ").append(valores[2]).append(" faltas de pagina\n");
        texto.append("Otimo - ").append(valores[3]).append(" faltas de pagina\n\n");
        texto.append(montarTextoMelhorAlgoritmo(nomes, valores));

        return texto.toString();
    }

    private String montarTextoMelhorAlgoritmo(String[] nomes, int[] valores) {
        int menor = valores[0];

        for (int valor : valores) {
            if (valor < menor) {
                menor = valor;
            }
        }

        List<String> melhores = new ArrayList<>();

        for (int i = 0; i < valores.length; i++) {
            if (valores[i] == menor) {
                melhores.add(nomes[i]);
            }
        }

        if (melhores.size() == 1) {
            return "Algoritmo com menor numero de faltas de pagina: " + melhores.get(0);
        }

        return "Algoritmos com menor numero de faltas de pagina: " + String.join(", ", melhores);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SimuladorSwing janela = new SimuladorSwing();
            janela.setVisible(true);
        });
    }
}

class GraficoBarrasPanel extends JPanel {
    private String[] nomes = {"FIFO", "LRU", "Relogio", "Otimo"};
    private int[] valores = {0, 0, 0, 0};
    private final Color[] cores = {
        new Color(54, 112, 199),
        new Color(39, 151, 92),
        new Color(221, 151, 55),
        new Color(154, 87, 184)
    };

    public GraficoBarrasPanel() {
        setPreferredSize(new Dimension(780, 250));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Grafico comparativo de faltas de pagina"));
    }

    public void atualizarDados(String[] nomes, int[] valores) {
        this.nomes = nomes;
        this.valores = valores;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        // O grafico e desenhado manualmente com Graphics2D, sem bibliotecas externas.
        Graphics2D g2d = (Graphics2D) graphics;
        int largura = getWidth();
        int altura = getHeight();
        int margemEsquerda = 60;
        int margemDireita = 30;
        int margemSuperior = 35;
        int margemInferior = 55;
        int larguraUtil = largura - margemEsquerda - margemDireita;
        int alturaUtil = altura - margemSuperior - margemInferior;
        int maiorValor = encontrarMaiorValor();

        g2d.setColor(Color.DARK_GRAY);
        g2d.drawLine(margemEsquerda, margemSuperior, margemEsquerda, altura - margemInferior);
        g2d.drawLine(margemEsquerda, altura - margemInferior, largura - margemDireita, altura - margemInferior);

        if (maiorValor == 0) {
            g2d.drawString("Clique em Simular para gerar o grafico.", margemEsquerda + 10, margemSuperior + 25);
            return;
        }

        int espacoPorBarra = larguraUtil / valores.length;
        int larguraBarra = Math.max(35, espacoPorBarra / 2);

        for (int i = 0; i < valores.length; i++) {
            int alturaBarra = (int) ((valores[i] / (double) maiorValor) * alturaUtil);
            int x = margemEsquerda + (i * espacoPorBarra) + (espacoPorBarra - larguraBarra) / 2;
            int y = altura - margemInferior - alturaBarra;

            g2d.setColor(cores[i]);
            g2d.fillRect(x, y, larguraBarra, alturaBarra);

            g2d.setColor(Color.DARK_GRAY);
            g2d.drawRect(x, y, larguraBarra, alturaBarra);
            g2d.drawString(String.valueOf(valores[i]), x + larguraBarra / 2 - 5, y - 6);
            g2d.drawString(nomes[i], x + 3, altura - margemInferior + 22);
        }
    }

    private int encontrarMaiorValor() {
        int maior = 0;

        for (int valor : valores) {
            if (valor > maior) {
                maior = valor;
            }
        }

        return maior;
    }
}
