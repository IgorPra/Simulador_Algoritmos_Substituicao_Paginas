import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class PageReplacementSimulator {

    public int fifo(List<Integer> paginas, int quadros) {
        Set<Integer> memoria = new LinkedHashSet<>();
        Queue<Integer> fila = new LinkedList<>();
        int faltas = 0;

        for (int pagina : paginas) {
            if (!memoria.contains(pagina)) {
                faltas++;

                if (memoria.size() == quadros) {
                    int paginaRemovida = fila.poll();
                    memoria.remove(paginaRemovida);
                }

                memoria.add(pagina);
                fila.add(pagina);
            }
        }

        return faltas;
    }

    public int lru(List<Integer> paginas, int quadros) {
        List<Integer> memoria = new ArrayList<>();
        int faltas = 0;

        for (int pagina : paginas) {
            if (memoria.contains(pagina)) {
                memoria.remove(Integer.valueOf(pagina));
                memoria.add(pagina);
            } else {
                faltas++;

                if (memoria.size() == quadros) {
                    memoria.remove(0);
                }

                memoria.add(pagina);
            }
        }

        return faltas;
    }

    public int clock(List<Integer> paginas, int quadros) {
        int[] memoria = new int[quadros];
        boolean[] bitsReferencia = new boolean[quadros];
        boolean[] ocupado = new boolean[quadros];
        int ponteiro = 0;
        int faltas = 0;

        for (int pagina : paginas) {
            int posicao = buscarPagina(memoria, ocupado, pagina);

            if (posicao != -1) {
                bitsReferencia[posicao] = true;
            } else {
                faltas++;

                while (ocupado[ponteiro] && bitsReferencia[ponteiro]) {
                    bitsReferencia[ponteiro] = false;
                    ponteiro = (ponteiro + 1) % quadros;
                }

                memoria[ponteiro] = pagina;
                ocupado[ponteiro] = true;
                bitsReferencia[ponteiro] = true;
                ponteiro = (ponteiro + 1) % quadros;
            }
        }

        return faltas;
    }

    public int otimo(List<Integer> paginas, int quadros) {
        List<Integer> memoria = new ArrayList<>();
        int faltas = 0;

        for (int i = 0; i < paginas.size(); i++) {
            int pagina = paginas.get(i);

            if (!memoria.contains(pagina)) {
                faltas++;

                if (memoria.size() < quadros) {
                    memoria.add(pagina);
                } else {
                    int indiceSubstituir = encontrarPaginaOtima(memoria, paginas, i + 1);
                    memoria.set(indiceSubstituir, pagina);
                }
            }
        }

        return faltas;
    }

    private int buscarPagina(int[] memoria, boolean[] ocupado, int pagina) {
        for (int i = 0; i < memoria.length; i++) {
            if (ocupado[i] && memoria[i] == pagina) {
                return i;
            }
        }

        return -1;
    }

    private int encontrarPaginaOtima(List<Integer> memoria, List<Integer> paginas, int inicioBusca) {
        int indiceSubstituir = 0;
        int maiorDistancia = -1;

        for (int i = 0; i < memoria.size(); i++) {
            int paginaAtual = memoria.get(i);
            int proximoUso = encontrarProximoUso(paginas, paginaAtual, inicioBusca);

            if (proximoUso == -1) {
                return i;
            }

            if (proximoUso > maiorDistancia) {
                maiorDistancia = proximoUso;
                indiceSubstituir = i;
            }
        }

        return indiceSubstituir;
    }

    private int encontrarProximoUso(List<Integer> paginas, int pagina, int inicioBusca) {
        for (int i = inicioBusca; i < paginas.size(); i++) {
            if (paginas.get(i) == pagina) {
                return i;
            }
        }

        return -1;
    }
}
