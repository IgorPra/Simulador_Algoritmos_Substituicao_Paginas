public class ModeloExecucao {
    private final String nome;
    private final String sequencia;
    private final int quadros;

    public ModeloExecucao(String nome, String sequencia, int quadros) {
        this.nome = nome;
        this.sequencia = sequencia;
        this.quadros = quadros;
    }

    public String getSequencia() {
        return sequencia;
    }

    public int getQuadros() {
        return quadros;
    }

    @Override
    public String toString() {
        return nome;
    }
}
