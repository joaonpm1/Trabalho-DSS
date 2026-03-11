package app.dss.cadeiaUI;

import java.util.Scanner;

public class FuncView implements View {

    private Controller controller;
    private Scanner scanner;

    public FuncView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run(String idFunc) {
        String role = this.controller.getModel().getRole(idFunc); 
        
        if (role == null) {
            System.out.println("Erro: Utilizador não tem permissões.");
            return null;
        }

        switch (role) {
            case "COO":
                return menuCOO(idFunc);
            case "Chefe":
                return menuChefe(idFunc);
            case "Operador":
                return menuOperador(idFunc, false);
            default:
                System.out.println("Perfil não reconhecido: " + role);
                return null;
        }
    }

    private View menuOperador(String idFunc, boolean isSubMenu) {
        boolean sair = false;
        while (!sair) {
            this.limparEcra();
            if (isSubMenu) {
                System.out.println("=== Painel de Operador (Chefe) ===");
            } else {
                System.out.println("=== Painel de Operador ===");
            }
            
            System.out.println("--------------------------");
            System.out.println("1. Gestão de Pedidos (POS)");
            System.out.println("2. Consultar Stock Local");
            System.out.println("3. Registar Entrada de Produtos"); 
            System.out.println("4. Consultar Ementa");
            System.out.println("5. Área Pessoal / Mensagens"); 
            System.out.println("--------------------------");
            
            if (isSubMenu) {
                System.out.println("0. Voltar ao Menu de Chefe");
            } else {
                System.out.println("0. Logout");
            }
            System.out.print("Opção: ");
            
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    new OrderView(controller).run(idFunc);
                    break;
                case "2":
                    String idLoja = controller.getModel().getRestID(idFunc); 
                    if (idLoja != null) {
                        new StockConsultView(controller).run(idLoja);
                    } else {
                        System.out.println("Erro: Não está alocado a nenhuma loja.");
                        pausa();
                    }
                    break;
                case "3":
                    String idRestOp = controller.getModel().getRestID(idFunc);
                    if (idRestOp != null) {
                        new RegistarEntradaStockView(controller).run(idRestOp);
                    } else {
                        System.out.println("Erro: Não está alocado a nenhuma loja.");
                        pausa();
                    }
                    break;
                case "4":
                    new MenuView(controller).run();
                    break;
                case "5":
                    new MessagesView(controller).run(idFunc);
                    break;
                case "0":
                    return null;
                default:
                    System.out.println("Opção inválida.");
                    pausa();
            }
        }
        return null;
    }

    private View menuChefe(String idFunc) {
        boolean sair = false;
        while (!sair) {
            this.limparEcra();
            String idRest = controller.getModel().getRestID(idFunc);
            System.out.println("=== Gestão de Loja (Chefe) ===");
            System.out.println("Loja: " + (idRest != null ? idRest : "N/A"));
            System.out.println("------------------------------");
            System.out.println("1. Gestão de Encomendas (Stock)");
            System.out.println("2. Analisar Performance Restaurante");
            System.out.println("3. Gestão de Funcionários");
            System.out.println("4. Área Pessoal / Mensagens");
            System.out.println("5. Aceder ao Painel de Operador"); 
            System.out.println("0. Logout");
            System.out.print("Opção: ");
            
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    menuGestaoStock(idFunc);
                    break;
                case "2":
                    new ConsultReportView(controller).run(idFunc);
                    pausa();
                    break;
                case "3":
                    new GestFuncView(controller).run(idFunc);
                    break;
                case "4":
                    new MessagesView(controller).run(idFunc);
                    break;
                case "5":
                    menuOperador(idFunc, true); 
                    break;
                case "0":
                    return null;
                default:
                    System.out.println("Opção inválida.");
                    pausa();
            }
        }
        return null;
    }

    private View menuCOO(String idFunc) {
        boolean sair = false;
        while (!sair) {
            this.limparEcra();
            System.out.println("=== Administração Global (COO) ===");
            System.out.println("----------------------------------");
            System.out.println("1. Analisar Performance Global");
            System.out.println("2. Analisar Performance de Restaurante");
            System.out.println("3. Consultar Ementa");
            System.out.println("4. Gestão de Funcionários");
            System.out.println("5. Área Pessoal / Mensagens");
            System.out.println("0. Logout");
            System.out.print("Opção: ");
            
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    new ConsultReportView(controller).run(idFunc);
                    pausa();
                    break;
                case "2":
                    new ConsultReportView(controller).runCOO_ModoRestaurante(idFunc);
                    pausa();
                    break;
                case "3":
                    new MenuView(controller).run();
                    break;
                case "4":
                    new GestFuncView(controller).run(idFunc);
                    break;
                case "5":
                    new MessagesView(controller).run(idFunc);
                    break;
                case "0":
                    return null;
                default:
                    System.out.println("Opção inválida.");
                    pausa();
            }
        }
        return null;
    }

    private void menuGestaoStock(String idFunc) {
        String idRest = controller.getModel().getRestID(idFunc);
        if (idRest == null) {
            System.out.println("Erro: Utilizador não associado a uma loja.");
            pausa();
            return;
        }

        boolean sair = false;
        
        while(!sair) {
            this.limparEcra();
            System.out.println("=== Gestão de Stock e Encomendas ===");
            System.out.println("1. Realizar Nova Encomenda");
            System.out.println("2. Registar Chegada de Encomenda");
            System.out.println("0. Voltar");
            System.out.print("Opção: ");
            
            String op = scanner.nextLine();
            switch(op) {
                case "1":
                    new RealizarEncomendaView(controller).run(idRest);
                    break;
                case "2":
                    new RegistarEntradaStockView(controller).run(idRest);
                    break;
                case "0":
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida.");
                    pausa();
            }
        }
    }

    private void pausa() {
        System.out.println("\n(Pressione ENTER para continuar)");
        scanner.nextLine();
    }
}