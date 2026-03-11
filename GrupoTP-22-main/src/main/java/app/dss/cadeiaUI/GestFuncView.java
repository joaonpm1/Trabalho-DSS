package app.dss.cadeiaUI;

import java.util.Scanner;
import java.util.List;

public class GestFuncView implements View {

    private Controller controller;
    private Scanner scanner;

    public GestFuncView(Controller c) {
        this.controller = c;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public View run() { return null; }

    public View run(String idLogado) {
        String roleLogado = controller.getModel().getRole(idLogado);
        String restLogado = controller.getRestId(idLogado);
        
        boolean isCOO = "COO".equalsIgnoreCase(roleLogado);

        boolean sair = false;
        while (!sair) {
            this.limparEcra();
            System.out.println("\n=======================================================");
            System.out.println("           GESTÃO DE RECURSOS HUMANOS (RH)             ");
            System.out.println("=======================================================");
            System.out.println("  Utilizador: " + idLogado + " [" + roleLogado + "]");
            if (!isCOO) System.out.println("  Restaurante: " + restLogado);
            System.out.println("-------------------------------------------------------");
            
            System.out.println("  1. Consultar Ficha de Funcionário");
            System.out.println("  2. Contratar Novo Funcionário");
            System.out.println("  3. Alterar Cargo / Relocar");
            System.out.println("  4. Despedir Funcionário");
            System.out.println("  5. Listar Funcionários"); // NOVA OPÇÃO
            
            if (isCOO) {
                System.out.println("  6. Registar Novo Restaurante");
            }
            
            System.out.println("  0. Voltar");
            System.out.println("-------------------------------------------------------");
            System.out.print("  Opção: ");

            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    consultarFuncionario(isCOO, restLogado);
                    break;
                case "2":
                    contratarFuncionario(isCOO, restLogado);
                    break;
                case "3":
                    alterarCargo(isCOO, restLogado);
                    break;
                case "4":
                    despedirFuncionario(isCOO, restLogado);
                    break;
                case "5":
                    listarFuncionarios(isCOO ? null : restLogado);
                    break;
                case "6":
                    if (isCOO) registarRestaurante();
                    else System.out.println("Opção inválida.");
                    break;
                case "0":
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida.");
                    pausa();
            }
        }
        return null;
    }

    
    private void listarFuncionarios(String filtroRestaurante) {
        this.limparEcra();
        String titulo = (filtroRestaurante == null) ? "TODOS OS FUNCIONÁRIOS DA CADEIA" : "FUNCIONÁRIOS DE " + filtroRestaurante;
        
        System.out.println("\n--- " + titulo + " ---");
        
        List<String> lista = controller.listarFuncionarios(filtroRestaurante);
        
        if (lista.isEmpty()) {
            System.out.println("  (Nenhum funcionário encontrado)");
        } else {
            for (String linha : lista) {
                System.out.println("  " + linha);
            }
        }
        System.out.println("-------------------------------------------------------");
        pausa();
    }


    private void consultarFuncionario(boolean isCOO, String meuRestId) {
        System.out.print("\n  Indique o ID do funcionário: ");
        String targetId = scanner.nextLine();
        if (targetId.trim().isEmpty()) return;

        if (!isCOO && !validarAcesso(targetId, meuRestId)) return;

        String ficha = controller.getModel().consulFuncionario(targetId);
        System.out.println("\n" + ficha);
        pausa();
    }

    private void contratarFuncionario(boolean isCOO, String meuRestId) {
        System.out.println("\n--- NOVA CONTRATAÇÃO ---");
        System.out.print("  Nome: ");
        String nome = scanner.nextLine();
        if (nome.trim().isEmpty()) { System.out.println(">> Cancelado."); pausarCurto(); return; }
        System.out.print("  Email: ");
        String email = scanner.nextLine();
        System.out.print("  Telemóvel: ");
        String phone = scanner.nextLine();

        System.out.println("  Cargos: [1] Operador  [2] Chefe  " + (isCOO ? "[3] COO" : ""));
        System.out.print("  Escolha o Cargo: ");
        String opCargo = scanner.nextLine();
        
        String role = "Operador";
        if (opCargo.equals("2")) role = "Chefe";
        else if (opCargo.equals("3") && isCOO) role = "COO";

        String targetRest;
        if (isCOO) {
            if (role.equals("COO")) targetRest = "SEDE";
            else {
                while (true) {
                    System.out.print("  ID da Loja de Alocação: ");
                    targetRest = scanner.nextLine().trim();
                    if (targetRest.isEmpty()) return;
                    if (controller.existeRestaurante(targetRest) || targetRest.equalsIgnoreCase("SEDE")) break;
                    System.out.println(">> Erro: Restaurante não existe.");
                }
            }
        } else {
            targetRest = meuRestId;
        }

        controller.adicionarFuncionario(targetRest, nome, email, phone, role);
        System.out.println("\n>> Sucesso! " + role + " " + nome + " alocado a " + targetRest + ".");
        pausa();
    }

private void alterarCargo(boolean isCOO, String meuRestId) {
        System.out.println("\n--- ALTERAR CARGO / RELOCAR ---");
        System.out.print("  ID do Funcionário: ");
        String targetId = scanner.nextLine();
        
        if (!isCOO && !validarAcesso(targetId, meuRestId)) return;

        String novoRole = "";
        boolean cargoValido = false;

        while (!cargoValido) {
            System.out.println("  Cargos disponíveis: Operador, Chefe" + (isCOO ? ", COO" : ""));
            System.out.print("  Escreva o novo Cargo: ");
            novoRole = scanner.nextLine().trim();

            if (!validarCargo(novoRole)) {
                System.out.println(">> Erro: Cargo '" + novoRole + "' não reconhecido. Tente novamente.");
                continue;
            }

            if (novoRole.equalsIgnoreCase("COO") && !isCOO) {
                System.out.println(">> Erro: Apenas o COO pode atribuir este cargo. Escolha outro.");
                continue;
            }

            cargoValido = true;
        }

        controller.relocarFuncionario(targetId, novoRole);
        System.out.println(">> Sucesso: Funcionário " + targetId + " agora é " + novoRole + ".");
        pausa();
    }

    private void despedirFuncionario(boolean isCOO, String meuRestId) {
        System.out.println("\n--- DESPEDIR FUNCIONÁRIO ---");
        System.out.print("  ID do Funcionário: ");
        String targetId = scanner.nextLine();
        if (!isCOO && !validarAcesso(targetId, meuRestId)) return;

        System.out.print("  Tem a certeza? (S/N): ");
        if (scanner.nextLine().equalsIgnoreCase("S")) {
            controller.removerFuncionario(targetId);
            System.out.println(">> Funcionário removido.");
        }
        pausa();
    }

    private void registarRestaurante() {
        System.out.println("\n--- REGISTAR NOVO RESTAURANTE ---");
        System.out.print("  ID do Restaurante: ");
        String id = scanner.nextLine().trim();
        if (controller.existeRestaurante(id)) { System.out.println(">> Já existe."); pausarCurto(); return; }
        
        System.out.print("  Nome: ");
        String nome = scanner.nextLine();
        System.out.print("  Localização: ");
        String loc = scanner.nextLine();
        
        controller.adicionarRestaurante(id, nome, loc);
        System.out.println(">> Sucesso!");
        pausa();
    }

    private boolean validarAcesso(String targetId, String meuRestId) {
        String targetRest = controller.getRestId(targetId);
        if (targetRest == null) { System.out.println(">> Não encontrado."); pausarCurto(); return false; }
        if (!targetRest.equals(meuRestId)) { System.out.println(">> Permissão negada (Outra loja)."); pausarCurto(); return false; }
        return true;
    }

    private boolean validarCargo(String cargo){
        return cargo.equals("COO") || cargo.equals("Chefe") || cargo.equals("Operador");
    }

    private void pausa() {
        System.out.println("\n  (Pressione ENTER para continuar)");
        scanner.nextLine();
    }
    
    private void pausarCurto() {
        try { Thread.sleep(1000); } catch (Exception e) {}
    }
}