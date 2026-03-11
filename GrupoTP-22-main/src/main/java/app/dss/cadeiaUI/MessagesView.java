package app.dss.cadeiaUI;

import java.util.Scanner;
import java.util.Set;

public class MessagesView implements View {

    private Controller controller;
    private Scanner scanner;

    public MessagesView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() { return null; }

    public View run(String idFunc) {
        boolean sair = false;
        
        // 1. Verificar se é COO para mostrar a opção extra
        String role = controller.getModel().getRole(idFunc);
        boolean isCOO = "COO".equalsIgnoreCase(role);

        while (!sair) {
            this.limparEcra();
            System.out.println("\n=======================================================");
            System.out.println("              ÁREA PESSOAL / MENSAGENS                 ");
            System.out.println(this.controller.getModel().consulFuncionario(idFunc));
            System.out.println("-------------------------------------------------------");
            System.out.println("  1. Ver Caixa de Entrada (Recebidas)");
            System.out.println("  2. Ver Itens Enviados");
            System.out.println("  3. Escrever Nova Mensagem");
            System.out.println("  4. Alterar a minha Password");
            
            if (isCOO) {
                System.out.println("  5. Enviar Mensagem para Restaurante (Broadcast)");
            }
            
            System.out.println("  0. Voltar ao Menu Anterior");
            System.out.println("-------------------------------------------------------");
            System.out.print("  Opção: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    visualizarMensagens(idFunc, true); 
                    break;
                case "2":
                    visualizarMensagens(idFunc, false); 
                    break;
                case "3":
                    escreverMensagem(idFunc);
                    break;
                case "4":
                    alterarPassword(idFunc);
                    break;
                case "5":
                    if (isCOO) {
                        enviarBroadcastRestaurante(idFunc);
                    } else {
                        System.out.println("Opção inválida.");
                    }
                    break;
                case "0":
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
        return null;
    }

    private void visualizarMensagens(String idFunc, boolean recebidas) {
        this.limparEcra();
        System.out.println(recebidas ? "\n--- CAIXA DE ENTRADA ---" : "\n--- ITENS ENVIADOS ---");
        
        Set<String> msgs;
        if (recebidas) {
            msgs = controller.getMensagensRecebidas(idFunc);
        } else {
            msgs = controller.getMensagensEnviadas(idFunc);
        }
        
        if (msgs == null || msgs.isEmpty()) {
            System.out.println(" (Nenhuma mensagem encontrada)");
        } else {
            for (String m : msgs) {
                System.out.println(" " + m);
            }
        }
        
        pausa();
    }

    private void escreverMensagem(String idFunc) {
        System.out.println("\n--- NOVA MENSAGEM ---");
        
        System.out.print("ID do Destinatário: ");
        String dest = scanner.nextLine();
        
        if (dest.trim().isEmpty()) {
            System.out.println("Operação cancelada.");
            return;
        }

        System.out.print("Texto da Mensagem: ");
        String texto = scanner.nextLine();

        if (texto.trim().isEmpty()) {
            System.out.println("Mensagem vazia. Operação cancelada.");
            return;
        }

        controller.enviarMensagem(idFunc, dest, texto);
        
        System.out.println("Mensagem enviada a " + dest);
        pausa();
    }
    
    private void enviarBroadcastRestaurante(String idFunc) {
        System.out.println("\n--- MENSAGEM BROADCAST (COO) ---");
        System.out.println("Esta mensagem será enviada a TODOS os funcionários de um restaurante.");
        
        System.out.print("ID do Restaurante Alvo: ");
        String idRest = scanner.nextLine().trim();
        
        if (idRest.isEmpty()) {
            System.out.println("Operação cancelada.");
            pausa();
            return;
        }

        System.out.print("Texto da Mensagem: ");
        String texto = scanner.nextLine();

        if (texto.trim().isEmpty()) {
            System.out.println("Mensagem vazia. Operação cancelada.");
            pausa();
            return;
        }

        controller.contactRestaurant(idFunc, idRest, texto);
        
        System.out.println(">> Processo de envio concluído.");
        pausa();
    }

    private void alterarPassword(String idFunc) {
        System.out.println("\n--- ALTERAR PASSWORD ---");
        System.out.print("Nova Password: ");
        String novaPass = scanner.nextLine();
        
        if (novaPass.length() < 3) {
            System.out.println("Erro: Password muito curta.");
        } else {
            boolean sucesso = controller.alterarPassword(idFunc, novaPass);
            
            if (sucesso) {
                System.out.println(">> Sucesso: Password alterada.");
            } else {
                System.out.println(">> Erro: Não foi possível alterar a password (pode ser igual à anterior).");
            }
        }
        pausa();
    }
    
    public void limparEcra() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private void pausa() {
        System.out.println("\n(Pressione ENTER para continuar)");
        scanner.nextLine();
    }
}