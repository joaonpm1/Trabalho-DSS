package app.dss.cadeiaUI;

import java.util.Scanner;

public class RegistarEntradaStockView implements View {

    private Controller controller;
    private Scanner scanner;

    public RegistarEntradaStockView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() { return null; }

    public View run(String idRest) {
        boolean sair = false;

        while (!sair) {
            this.limparEcra();
            System.out.println("\n=======================================================");
            System.out.println("            REGISTAR CHEGADA DE ENCOMENDAS             ");
            System.out.println("=======================================================");
            System.out.println("  Restaurante: " + idRest);
            System.out.println("-------------------------------------------------------");

            String pendentes = controller.getEncomendasPendentes(idRest);
            System.out.println(pendentes);
            
            System.out.println("-------------------------------------------------------");
            System.out.println("  Indique o ID da encomenda que acabou de receber.");
            System.out.println("  (Escreva '0' para voltar)");
            System.out.print("  ID Encomenda: ");

            String input = scanner.nextLine();

            if (input.equals("0")) {
                sair = true;
            } else {
                if (input.trim().isEmpty()) {
                    System.out.println("  >> ID inválido.");
                } else {
                    controller.registarRecebimentoStock(idRest, input);
                    
                    System.out.println("\n  >> Stock atualizado com sucesso!");
                    System.out.println("  (Pressione ENTER para continuar)");
                    scanner.nextLine();
                }
            }
        }
        return null;
    }
}