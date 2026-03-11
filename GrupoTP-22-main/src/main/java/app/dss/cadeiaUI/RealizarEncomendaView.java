package app.dss.cadeiaUI;

import java.util.Scanner;

public class RealizarEncomendaView implements View {

    private Controller controller;
    private Scanner scanner;

    public RealizarEncomendaView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() { return null; }

    public View run(String idRest) {
        boolean continuar = true;

        while (continuar) {
            this.limparEcra();
            System.out.println("\n=======================================================");
            System.out.println("             NOVA ENCOMENDA DE STOCK                   ");
            System.out.println("=======================================================");
            System.out.println("  Restaurante: " + idRest);
            System.out.println("-------------------------------------------------------");
            
            System.out.println("  (Escreva '0' no nome para cancelar)");
            System.out.print("  Nome do Ingrediente: ");
            String nome = scanner.nextLine();

            if (nome.equals("0") || nome.trim().isEmpty()) {
                return null;
            }

            int qtd = -1;
            while (qtd <= 0) {
                System.out.print("  Quantidade a encomendar: ");
                try {
                    String qtdStr = scanner.nextLine();
                    qtd = Integer.parseInt(qtdStr);
                    if (qtd <= 0) System.out.println("  >> Erro: A quantidade deve ser positiva.");
                } catch (NumberFormatException e) {
                    System.out.println("  >> Erro: Introduza um número válido.");
                }
            }

            controller.realizarEncomendaStock(idRest, nome, qtd);
            System.out.println("\n  >> Encomenda de " + qtd + "x " + nome + " registada com sucesso!");

            System.out.println("-------------------------------------------------------");
            System.out.print("  Deseja encomendar outro ingrediente? (S/N): ");
            String resp = scanner.nextLine();
            
            if (!resp.equalsIgnoreCase("S")) {
                continuar = false;
            }
        }
        
        return null;
    }
}