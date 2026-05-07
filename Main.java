import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Simulador de Algoritmos de Substituicao de Paginas ===");
        System.out.print("Digite a sequencia de paginas separada por virgula: ");
        String entradaPaginas = scanner.nextLine();

        List<Integer> paginas = converterSequencia(entradaPaginas);

        if (paginas.isEmpty()) {
            System.out.println("Erro: a sequencia de paginas nao pode estar vazia.");
            scanner.close();
            return;
        }

        System.out.print("Digite a quantidade de quadros da memoria: ");

        if (!scanner.hasNextInt()) {
            System.out.println("Erro: a quantidade de quadros deve ser um numero inteiro.");
            scanner.close();
            return;
        }

        int quadros = scanner.nextInt();

        if (quadros <= 0) {
            System.out.println("Erro: a quantidade de quadros deve ser maior que zero.");
            scanner.close();
            return;
        }

        PageReplacementSimulator simulador = new PageReplacementSimulator();

        int faltasFifo = simulador.fifo(paginas, quadros);
        int faltasLru = simulador.lru(paginas, quadros);
        int faltasClock = simulador.clock(paginas, quadros);
        int faltasOtimo = simulador.otimo(paginas, quadros);

        System.out.println();
        System.out.println("FIFO - " + faltasFifo + " faltas de pagina");
        System.out.println("LRU - " + faltasLru + " faltas de pagina");
        System.out.println("Relogio - " + faltasClock + " faltas de pagina");
        System.out.println("Otimo - " + faltasOtimo + " faltas de pagina");

        exibirMelhorAlgoritmo(faltasFifo, faltasLru, faltasClock, faltasOtimo);

        scanner.close();
    }

    private static List<Integer> converterSequencia(String entrada) {
        List<Integer> paginas = new ArrayList<>();

        if (entrada == null || entrada.trim().isEmpty()) {
            return paginas;
        }

        String[] valores = entrada.split(",");

        for (String valor : valores) {
            try {
                paginas.add(Integer.parseInt(valor.trim()));
            } catch (NumberFormatException erro) {
                System.out.println("Aviso: valor invalido ignorado: " + valor.trim());
            }
        }

        return paginas;
    }

    private static void exibirMelhorAlgoritmo(int fifo, int lru, int clock, int otimo) {
        int menor = Math.min(Math.min(fifo, lru), Math.min(clock, otimo));
        List<String> melhores = new ArrayList<>();

        if (fifo == menor) {
            melhores.add("FIFO");
        }
        if (lru == menor) {
            melhores.add("LRU");
        }
        if (clock == menor) {
            melhores.add("Relogio");
        }
        if (otimo == menor) {
            melhores.add("Otimo");
        }

        System.out.println();

        if (melhores.size() == 1) {
            System.out.println("Algoritmo com menor numero de faltas de pagina: " + melhores.get(0));
        } else {
            System.out.println("Algoritmos com menor numero de faltas de pagina: " + String.join(", ", melhores));
        }
    }
}
