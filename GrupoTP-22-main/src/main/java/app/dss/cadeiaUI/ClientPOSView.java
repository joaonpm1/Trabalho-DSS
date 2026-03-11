package app.dss.cadeiaUI;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class ClientPOSView implements View {

    private Controller controller;
    private Scanner scanner;
    
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String CYAN = "\u001B[36m";
    private static final String WHITE_BOLD = "\u001B[1;37m";

    public ClientPOSView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() { 
        this.limparEcra();
        System.out.println("=== CONFIGURAÇÃO DE SISTEMA POS (Acesso Reservado) ===");
        System.out.print("Indique o ID da Loja onde este terminal está instalado: ");
        String idRest = scanner.nextLine();
        
        if (!controller.existeRestaurante(idRest)) {
            System.out.println("Erro: Loja não existe na base de dados.");
            pausa();
            return null;
        }
        boolean sair = true;
        while (true) {
            runClienteSession(idRest, sair);
            System.out.println("\n\n(A reiniciar sistema para o próximo cliente...)");
            try { Thread.sleep(1500); } catch (Exception e) {}
        }
    }

    public void runClienteSession(String idRest, boolean sair) {
        this.limparEcra();
        System.out.println("\n=======================================================");
        System.out.println("              BEM-VINDO AO RESTAURANTE                 ");
        System.out.println("=======================================================");
        System.out.println("\nPor favor, selecione o tipo de pedido:");
        System.out.println("  1. Comer Aqui (Local)");
        System.out.println("  2. Levar para Fora (Take Away)");
        System.out.println("  0. Cancelar");
        System.out.println("-------------------------------------------------------");
        System.out.print("  Opção: ");
        
        String op = scanner.nextLine();
        String tipoPedido;
        
        switch (op) {
            case "1": tipoPedido = "Local"; break;
            case "2": tipoPedido = "TakeAway"; break;
            case "0": sair = false; return; 
            default: return; 
        }

        fazerPedido(idRest, tipoPedido);
    }

    private void fazerPedido(String idRest, String tipoPedido) {
        Map<String, Integer> carrinho = new HashMap<>();
        boolean pagar = false;

        while (!pagar) {
            this.limparEcra();
            Map<Integer, String> catalogoNumerico = new HashMap<>();
            int indexCounter = 1;

            System.out.println("\n==============================================================================");
            System.out.println("                     NOVO PEDIDO - " + tipoPedido.toUpperCase());
            System.out.println("==============================================================================");
            
            mostrarCarrinho(carrinho);
            
            System.out.println("\n--- EMENTA (Os itens a " + RED + "VERMELHO" + RESET + " estão esgotados) ---");
            
            System.out.println("\n " + WHITE_BOLD + "[ MENUS COMBINADOS ]" + RESET);
            indexCounter = imprimirTabelaCategoria(idRest, controller.getListaMenus(), indexCounter, catalogoNumerico);

            System.out.println("\n " + WHITE_BOLD + "[ PRATOS ]" + RESET);
            indexCounter = imprimirTabelaCategoria(idRest, controller.getListaRefeicoes(), indexCounter, catalogoNumerico);

            System.out.println("\n " + WHITE_BOLD + "[ BEBIDAS ]" + RESET);
            indexCounter = imprimirTabelaCategoria(idRest, controller.getListaBebidas(), indexCounter, catalogoNumerico);

            System.out.println("\n==============================================================================");
            System.out.println("  Digite o " + WHITE_BOLD + "NÚMERO" + RESET + " do item para adicionar.");
            System.out.println("  Digite " + GREEN + "'PAGAR'" + RESET + " para finalizar ou '0' para Cancelar.");
            System.out.print("  > ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                return; 
            } 
            else if (input.equalsIgnoreCase("PAGAR")) {
                if (carrinho.isEmpty()) {
                    System.out.println(">> Carrinho vazio! Adicione produtos primeiro.");
                    pausa();
                } else {
                    System.out.println("\n-------------------------------------------------------");
                    System.out.println("  Deseja adicionar observações? (ex: 'Sem gelo')");
                    System.out.println("  (Pressione ENTER para ignorar)");
                    System.out.print("  Notas: ");
                    String notas = scanner.nextLine().trim();
                    
                    finalizarCompra(idRest, carrinho, tipoPedido, notas);
                    pagar = true;
                }
            } 
            else {
                try {
                    int escolha = Integer.parseInt(input);
                    if (catalogoNumerico.containsKey(escolha)) {
                        String nomeProduto = catalogoNumerico.get(escolha);
                        adicionarAoCarrinho(idRest, carrinho, nomeProduto);
                    } else {
                        System.out.println(">> Número inválido.");
                        pausa();
                    }
                } catch (NumberFormatException e) {
                    System.out.println(">> Por favor, introduza o número do item.");
                    pausa();
                }
            }
        }
    }

    private int imprimirTabelaCategoria(String idRest, List<String> itens, int startIndex, Map<Integer, String> map) {
        if (itens == null || itens.isEmpty()) return startIndex;

        String border = "  +-----+----------------------------------------------------+------------+";
        System.out.println(border);
        System.out.printf("  |  #  | %-50s | %-10s |%n", "Artigo", "Preço");
        System.out.println(border);

        for (String linha : itens) {
            try {
                String[] partes = linha.split(";"); 
                String nome = partes[0];
                String preco = partes[1];
                
                String nomeDisplay = nome;
                if (nomeDisplay.length() > 48) {
                    nomeDisplay = nomeDisplay.substring(0, 45) + "...";
                }

                boolean disponivel = controller.isItemDisponivel(idRest, nome);
                String cor = disponivel ? GREEN : RED;
                String resetCor = RESET;
                
                if (!disponivel) {
                    nomeDisplay += " (Esgotado)";
                }

                System.out.printf("  | %3d | %s%-50s%s | %9s€ |%n", 
                    startIndex, 
                    cor, nomeDisplay, resetCor, 
                    preco);

                map.put(startIndex, nome);
                startIndex++;

            } catch (Exception e) {}
        }
        System.out.println(border);
        return startIndex;
    }

    private void adicionarAoCarrinho(String idRest, Map<String, Integer> carrinho, String nomeItem) {
        int qtdAtual = carrinho.getOrDefault(nomeItem, 0);
        
        if (controller.checkStockQuantidade(idRest, nomeItem, qtdAtual + 1)) {
            carrinho.put(nomeItem, qtdAtual + 1);
            System.out.println(GREEN + ">> " + nomeItem + " adicionado!" + RESET);
            try { Thread.sleep(400); } catch (Exception e) {} 
        } else {
            System.out.println(RED + ">> Desculpe, stock insuficiente para: " + nomeItem + RESET);
            pausa();
        }
    }

    private void mostrarCarrinho(Map<String, Integer> carrinho) {
        if (carrinho.isEmpty()) {
            System.out.println("  [Carrinho Vazio]");
        } else {
            System.out.println("  SEU PEDIDO ATUAL:");
            int totalItens = 0;
            for (Map.Entry<String, Integer> entry : carrinho.entrySet()) {
                System.out.println("   " + CYAN + entry.getValue() + "x " + entry.getKey() + RESET);
                totalItens += entry.getValue();
            }
            System.out.println("   Total de itens: " + totalItens);
        }
    }

    private void finalizarCompra(String idRest, Map<String, Integer> carrinho, String tipoPedido, String notas) {
        this.limparEcra();
        System.out.println("\n=======================================================");
        System.out.println("                MÉTODO DE PAGAMENTO                    ");
        System.out.println("=======================================================");
        System.out.println("  1. Dinheiro (Pagar no Balcão)");
        System.out.println("  2. Multibanco (Cartão)");
        System.out.println("  3. MB WAY");
        System.out.println("-------------------------------------------------------");
        
        String metodo = "";
        boolean valido = false;
        
        while (!valido) {
            System.out.print("  Selecione a opção: ");
            String op = scanner.nextLine().trim();
            
            switch (op) {
                case "1": metodo = "Dinheiro"; valido = true; break;
                case "2": metodo = "Multibanco"; valido = true; break;
                case "3": metodo = "MB WAY"; valido = true; break;
                default:
                    System.out.println(RED + "  >> Opção inválida. Tente novamente." + RESET);
            }
        }

        System.out.println("\n  >> A processar pagamento via " + YELLOW + metodo + RESET + "...");
        
        try { Thread.sleep(1500); } catch (Exception e) {}
        
        String notasFinais = notas + " [Pagamento: " + metodo + "]";
        controller.registarPedidoCliente(idRest, carrinho, tipoPedido, notasFinais);
        
        System.out.println(GREEN + "\n  >> PAGAMENTO ACEITE! OBRIGADO PELA PREFERÊNCIA." + RESET);
        System.out.println("  O seu pedido (" + tipoPedido + ") seguiu para a cozinha.");
        pausa();
    }

    private void pausa() {
        System.out.println("\n(Enter para continuar)");
        scanner.nextLine();
    }
}