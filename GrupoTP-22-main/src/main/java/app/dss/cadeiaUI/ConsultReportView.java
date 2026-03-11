package app.dss.cadeiaUI;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ConsultReportView implements View {

    private Controller controller;
    private Scanner scanner;

    public ConsultReportView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() { return null; }

    public View run(String idFunc) {
        return menuRelatorios(idFunc, null);
    }

    public View runCOO_ModoRestaurante(String idFunc) {
        this.limparEcra();
        System.out.println("\n--- SELEÇÃO DE RESTAURANTE ---");
        System.out.print("Indique o ID do Restaurante (ex: LOJA_01): ");
        String targetRest = scanner.nextLine().trim();

        if (targetRest.isEmpty()) return null;


        if (!controller.existeRestaurante(targetRest)) {
            System.out.println(">> Erro: Restaurante não encontrado.");
            pausa();
            return null;
        }

        return menuRelatorios(idFunc, targetRest);
    }


    private View menuRelatorios(String idFunc, String forcedRestId) {
        String role = controller.getModel().getRole(idFunc);
        String myRestId = controller.getRestId(idFunc);
        boolean isCOO = "COO".equalsIgnoreCase(role);

        String contextRestId = (forcedRestId != null) ? forcedRestId : (isCOO ? null : myRestId);
        String contextLabel = (contextRestId == null) ? "GLOBAL (Cadeia)" : "LOCAL (" + contextRestId + ")";

        boolean sair = false;
        while (!sair) {
            this.limparEcra();
            System.out.println("\n=======================================================");
            System.out.println("               CONSULTA DE RELATÓRIOS                  ");
            System.out.println("=======================================================");
            System.out.println("  Contexto: " + contextLabel);
            System.out.println("-------------------------------------------------------");
            
            System.out.println("  1. Relatório de Vendas (Sales)");
            System.out.println("  2. Relatório de Stock (Consumo)");
            System.out.println("  3. Relatório de Tempos de Atendimento");
            System.out.println("  4. Relatório Completo");
            System.out.println("  5. Top Itens Mais Vendidos (Top N)"); // NOVA OPÇÃO
            System.out.println("  0. Voltar");
            System.out.println("-------------------------------------------------------");
            System.out.print("  Opção: ");

            String input = scanner.nextLine();
            List<String> metricas = new ArrayList<>();

            // Variável de controle para saber se é um relatório normal ou o Top N
            boolean isTopSales = false; 

            switch (input) {
                case "1": metricas.add("Sales"); break;
                case "2": metricas.add("Stock"); break;
                case "3": metricas.add("Attendance"); break;
                case "4": metricas.addAll(Arrays.asList("Sales", "Stock", "Attendance")); break;
                case "5": isTopSales = true; break; // Flag para lógica especial
                case "0": sair = true; break;
                default: System.out.println("Opção inválida."); continue;
            }

            if (!sair) {
                
                // LÓGICA ESPECIAL PARA O TOP N SALES
                if (isTopSales) {
                    System.out.print("  Quantos itens deseja visualizar no Top (N)? ");
                    int n;
                    try {
                        n = Integer.parseInt(scanner.nextLine());
                        if (n <= 0) throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        System.out.println("  >> Número inválido. Insira um inteiro positivo.");
                        pausa();
                        continue;
                    }

                    DatasIntervalo datas = escolherDatas();
                    if (datas != null) {
                        System.out.println("\n  >> A calcular Top " + n + " Vendas...");
                        
                        // Nota: Precisas de adicionar este método ao teu Controller
                        String relatorioTop = controller.consultarTopVendas(contextRestId, datas.inicio, datas.fim, n);
                        
                        System.out.println("\n" + relatorioTop);
                        pausa();
                    }

                } else {
                    // LÓGICA PADRÃO PARA RELATÓRIOS EXISTENTES (1-4)
                    DatasIntervalo datas = escolherDatas();
                    
                    if (datas != null) {
                        System.out.println("\n  >> A gerar relatório...");
                        String relatorio;

                        if (contextRestId == null) {
                            relatorio = controller.consultarRelatorioCadeia(datas.inicio, datas.fim, metricas);
                        } else {
                            relatorio = controller.consultarRelatorioRestaurante(contextRestId, datas.inicio, datas.fim, metricas);
                        }

                        System.out.println("\n" + relatorio);
                        pausa();
                    }
                }
            }
        }
        return null;
    }
    
    // --- Métodos Auxiliares Mantidos ---

    private DatasIntervalo escolherDatas() {
        System.out.println("\n--- PERÍODO DE ANÁLISE ---");
        System.out.println("  1. Hoje");
        System.out.println("  2. Este Mês");
        System.out.println("  3. Personalizado");
        System.out.print("  Opção: ");
        
        String op = scanner.nextLine();
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now();

        switch (op) {
            case "1": break;
            case "2":
                start = YearMonth.now().atDay(1);
                end = LocalDate.now();
                break;
            case "3":
                System.out.print("  Início (AAAA-MM-DD): ");
                start = lerData();
                if (start == null) return null;
                System.out.print("  Fim    (AAAA-MM-DD): ");
                end = lerData();
                if (end == null) return null;
                break;
            default: return null;
        }
        return new DatasIntervalo(start, end);
    }

    private LocalDate lerData() {
        try {
            return LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            System.out.println("  >> Data inválida.");
            return null;
        }
    }
    
    public void limparEcra() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void pausa() {
        System.out.println("\n(Pressione ENTER para continuar)");
        scanner.nextLine();
    }

    private class DatasIntervalo {
        LocalDate inicio; LocalDate fim;
        public DatasIntervalo(LocalDate i, LocalDate f) { this.inicio = i; this.fim = f; }
    }
}