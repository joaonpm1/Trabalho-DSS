package app.dss.cadeiaUI;

import java.util.Scanner;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class StockConsultView implements View {

    private Controller controller;
    private Scanner scanner;

    public StockConsultView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() { return null; }

    public View run(String idRest) {
        while (true) {
            this.limparEcra();
            
            System.out.println("\n=======================================================");
            System.out.println("            GESTÃO DE STOCK LOCAL (REMOVER)            ");
            System.out.println("=======================================================");
            System.out.println("  Restaurante: " + idRest);
            System.out.println("-------------------------------------------------------");

            // 1. Pedir ao Controller o mapa de ingredientes (Nome -> Quantidade)
            // O Controller vai ter de converter a String do Model num Mapa
            Map<String, Integer> stockMap = controller.getStockItems(idRest);
            List<String> indexList = new ArrayList<>();

            if (stockMap == null || stockMap.isEmpty()) {
                System.out.println("  (Sem stock disponível ou erro na leitura)");
            } else {
                // 2. Criar Índice Virtual e Listar
                indexList.addAll(stockMap.keySet());
                for (int i = 0; i < indexList.size(); i++) {
                    String nome = indexList.get(i);
                    Integer qtd = stockMap.get(nome);
                    System.out.printf("  %d. %-20s [Qtd Atual: %d]\n", (i + 1), nome, qtd);
                }
            }

            System.out.println("=======================================================");
            System.out.println("0. Voltar ao Menu Anterior");
            System.out.println("Indique o NÚMERO do ingrediente para abater stock.");
            System.out.print("Opção: ");
            
            String input = scanner.nextLine();

            if (input.equals("0")) {
                break;
            }

            try {
                int index = Integer.parseInt(input);

                // 3. Validar Índice
                if (index > 0 && index <= indexList.size()) {
                    String itemSelecionado = indexList.get(index - 1);
                    
                    System.out.println(">> Selecionado: " + itemSelecionado);
                    System.out.print("Quantidade a remover: ");
                    int qtdRemover = Integer.parseInt(scanner.nextLine());

                    if (qtdRemover > 0) {
                        // 4. Chamar Controller para remover (usando o método do Model)
                        controller.removerStock(idRest, itemSelecionado, qtdRemover);
                        System.out.println(">> Sucesso: Stock atualizado!");
                    } else {
                        System.out.println(">> Erro: A quantidade deve ser maior que 0.");
                    }
                } else {
                    System.out.println(">> Erro: Índice inválido.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">> Erro: Insira um número válido.");
            } catch (Exception e) {
                System.out.println(">> Erro ao remover stock: " + e.getMessage());
            }

            System.out.println("(Pressione ENTER para continuar)");
            scanner.nextLine();
        }
        
        return null;
    }

    public void limparEcra() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}