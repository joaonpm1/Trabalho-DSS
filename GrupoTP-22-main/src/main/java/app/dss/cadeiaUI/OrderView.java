package app.dss.cadeiaUI;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class OrderView implements View {

    private Controller controller;
    private Scanner scanner;

    public OrderView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() { return null; }

    public View run(String idFunc) {
        String idRest = controller.getRestId(idFunc);
        
        if (idRest == null) {
            System.out.println("Erro: Não está associado a nenhum restaurante.");
            pausa();
            return null;
        }

        boolean sair = false;
        while (!sair) {
            this.limparEcra();
            System.out.println("\n=======================================================");
            System.out.println("               GESTÃO DE PEDIDOS (POS)                 ");
            System.out.println("=======================================================");
            System.out.println("  Restaurante: " + idRest);
            System.out.println("-------------------------------------------------------");

            String pedidosPendentes = controller.getPedidosPendentes(idRest);
            System.out.println(pedidosPendentes);
            
            System.out.println("-------------------------------------------------------");
            System.out.println("  Escreva o ID do Pedido para gerir (ex: 'Manual1')");
            System.out.println("  ou '0' para Sair.");
            System.out.print("  > ");

            String input = scanner.nextLine();

            if (input.equals("0")) {
                sair = true;
            } else {
                if (input.trim().isEmpty()) {
                    System.out.println("  >> ID inválido.");
                    pausa();
                } else {
                    gerirPedido(idRest, input);
                }
            }
        }
        return null;
    }

    private void gerirPedido(String idRest, String orderId) {
        boolean voltar = false;
        
        while (!voltar) {
            this.limparEcra();
            System.out.println("\n--- GESTÃO DO PEDIDO: " + orderId + " ---");
            
            String resumo = controller.getResumoPedido(idRest, orderId);
            if (resumo != null && !resumo.isEmpty()) {
                System.out.println(resumo);
                System.out.println("-------------------------------------------------------");
            } else {
                System.out.println("  (Resumo indisponível ou pedido não encontrado)");
            }

            Map<Integer, String> mapaTarefas = new HashMap<>();
            
            List<String> tarefas = controller.getListaTarefas(idRest, orderId);

            if (tarefas == null || tarefas.isEmpty()) {
                System.out.println("  (Sem tarefas pendentes)");
            } else {
                int indice = 1;
                System.out.println("  Tarefas associadas:");
                
                for (String linha : tarefas) {
                    int indexSeparador = linha.indexOf(" - ");
                    
                    if (indexSeparador != -1) {
                        String prefixo = linha.substring(0, indexSeparador);
                        
                        if (prefixo.length() > 4) {
                            String idRealTarefa = prefixo.substring(4).trim();
                            

                            mapaTarefas.put(indice, idRealTarefa);
                            

                            System.out.println("    [" + indice + "] " + linha);
                            
                            indice++;
                        }
                    }
                }
            }
            
            System.out.println("\n  AÇÕES DISPONÍVEIS:");
            System.out.println("  1. Concluir Tarefa");
            System.out.println("  2. Adiar Pedido");
            System.out.println("  0. Voltar");
            System.out.print("  Opção: ");

            String op = scanner.nextLine();

            switch (op) {
                case "1":
                    concluirTarefa(idRest, orderId, mapaTarefas);
                    break;
                case "2":
                    adiarPedido(idRest, orderId);
                    break;
                case "0":
                    voltar = true;
                    break;
                default:
                    System.out.println("  Opção inválida.");
                    pausa();
            }
        }
    }

    private void concluirTarefa(String idRest, String orderId, Map<Integer, String> mapaTarefas) {
        if (mapaTarefas.isEmpty()) {
            System.out.println("  >> Não há tarefas para concluir.");
            pausa();
            return;
        }

        System.out.print("  Indique o NÚMERO da Tarefa a concluir (ex: 1): ");
        String input = scanner.nextLine();

        try {
            int escolha = Integer.parseInt(input);

            if (mapaTarefas.containsKey(escolha)) {
                String idRealTarefa = mapaTarefas.get(escolha);

                controller.concluirTarefa(idRest, orderId, idRealTarefa);
                
                System.out.println("  >> Comando enviado."); 
            } else {
                System.out.println("  >> Número inválido.");
            }
        } catch (NumberFormatException e) {
            System.out.println("  >> Por favor, insira um número válido.");
        }
        pausa();
    }

    private void adiarPedido(String idRest, String orderId) {
        System.out.println("  Para quando quer adiar?");
        System.out.println("  (Escreva os minutos a adicionar a partir de AGORA, ex: 15)");
        System.out.print("  Minutos: ");
        
        try {
            String minStr = scanner.nextLine();
            int minutos = Integer.parseInt(minStr);
            
            if (minutos < 0) {
                System.out.println("  >> Erro: Minutos não podem ser negativos.");
            } else {
                LocalDateTime novaHora = LocalDateTime.now().plusMinutes(minutos);
                controller.adiarPedido(idRest, orderId, novaHora);
                
                System.out.println("  >> Hora prevista atualizada para: " + 
                    novaHora.format(DateTimeFormatter.ofPattern("HH:mm")));
            }
        } catch (NumberFormatException e) {
            System.out.println("  >> Erro: Valor inválido.");
        }
        pausa();
    }

    private void pausa() {
        System.out.println("\n  (Pressione ENTER para continuar)");
        scanner.nextLine();
    }
}