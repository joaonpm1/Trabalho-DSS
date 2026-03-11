package app.dss.cadeiaUI;

import java.util.Scanner;


public class LoginView implements View{

    private Controller loginController;
    private Scanner scanner;
    private View nextView;

    public LoginView(Controller c) {
        this.loginController = c;
        this.scanner = new Scanner(System.in);
        this.nextView = null;
    }

    public View run() {
        boolean sair = false;


        this.limparEcra();

        while (!sair) {
            System.out.println("\n=== Bem-vindo ao Sistema ===");
            System.out.println("1. Acesso Funcionário");
            System.out.println("2. POS Cliente");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    System.out.println("\n[Info] Redirecionando para área de Funcionário...");
                    this.nextView = new FuncLoginView(this.loginController);
                    sair = true;
                    break;

                case "2":
                    new ClientPOSView(this.loginController).run();
                    sair = true;
                    break;

                case "0":
                    System.out.println("A encerrar a aplicação...");
                    sair = true;
                    break;

                default:
                    System.out.println("Opção inválida. Por favor, tente novamente.");
                    break;
            }
        }
        return this.nextView;
    }
}