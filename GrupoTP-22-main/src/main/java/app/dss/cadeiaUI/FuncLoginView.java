package app.dss.cadeiaUI;

import java.util.Scanner;

public class FuncLoginView implements View {

    private Controller controller;
    private Scanner scanner;

    public FuncLoginView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() {
        this.limparEcra();

        while (true) {
            System.out.println("\n=========================================");
            System.out.println("      ACESSO RESERVADO A FUNCIONÁRIOS      ");
            System.out.println("=========================================");
            System.out.println("   [0] Cancelar e Voltar ao Menu Principal");
            System.out.println("-----------------------------------------");

            System.out.print("ID de Funcionário: ");
            String idInput = scanner.nextLine();

            if (idInput.equals("0")) {
                return null;
            }

            System.out.print("Password: ");
            String password = scanner.nextLine();

            if (password.equals("0")) {
                return null;
            }

            boolean loginSucesso = this.controller.getModel().login(idInput, password); 
            if (loginSucesso) {
                System.out.println("\nLogin efetuado com sucesso!");
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                
                System.out.println("[TODO] Redirecionar para MenuFuncionarioView...");
                return new FuncView(this.controller).run(idInput);
            } else {
                this.limparEcra();
                System.out.println("\n\u001B[31m(!) Credenciais incorretas.\u001B[0m Tente novamente.");
            }
        }
    }
}