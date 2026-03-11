package app.dss.cadeiaUI;

import java.util.Scanner;
import java.util.List;

public class MenuView implements View {

    private Controller controller;
    private Scanner scanner;

    public MenuView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() {
        this.limparEcra();
        
        System.out.println("\n===================================================================================================================================================================================");
        System.out.println("                                                                     EMENTA DO RESTAURANTE                                                                                         ");
        System.out.println("===================================================================================================================================================================================");

        System.out.println("\n [ MENUS COMBINADOS ]");
        imprimirTabela(controller.getListaMenus());

        System.out.println("\n [ PRATOS / REFEIÇÕES ]");
        imprimirTabela(controller.getListaRefeicoes());

        System.out.println("\n [ BEBIDAS ]");
        imprimirTabela(controller.getListaBebidas());

        System.out.println("\n===================================================================================================================================================================================");
        System.out.println("0. Voltar ao Menu Principal");
        
        while (true) {
            System.out.print("Escolha uma opção: ");
            String input = scanner.nextLine();
            if (input.equals("0")) {
                break;
            }
        }

        return null; 
    }

    private void imprimirTabela(List<String> itens) {
        if (itens == null || itens.isEmpty()) {
            System.out.println("  (Nenhum item disponível nesta categoria)");
            return;
        }

        String linhaSeparadora = "  +-------------------------------------+------------+--------------+------------------------------------------------------------------------------------------------------------------------+";
        
        System.out.println(linhaSeparadora);
        System.out.printf("  | %-35s | %-10s | %-12s | %-118s |%n", "Nome do Artigo", "Preço (€)", "Prep.(min)", "Composição / Receita");
        System.out.println(linhaSeparadora);

        for (String linha : itens) {
            try {
                String[] partes = linha.split(";");
                
                if (partes.length >= 3) {
                    String nome = partes[0];
                    if (nome.length() > 35) nome = nome.substring(0, 32) + "...";
                    
                    String preco = partes[1];
                    String tempo = partes[2];
                    
                    String receita = (partes.length > 3) ? partes[3] : "";
                    
                    if (receita.length() > 118) {
                        receita = receita.substring(0, 115) + "...";
                    }

                    System.out.printf("  | %-35s | %9s€ | %8s min | %-118s |%n", nome, preco, tempo, receita);
                }
            } catch (Exception e) {
            }
        }
        System.out.println(linhaSeparadora);
    }
}