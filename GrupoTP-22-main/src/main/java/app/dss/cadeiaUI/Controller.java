package app.dss.cadeiaUI;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import app.dss.cadeiaLN.ICadeiaLN;

public class Controller{
    private ICadeiaLN model;

    public Controller (ICadeiaLN m){
        this.model = m;
    }

    public ICadeiaLN getModel(){ return this.model; }

    public List<String> getListaMenus() {
        return buildFormattedList(this.model.getMenuNames());
    }

    public List<String> getListaTarefas(String idRest, String orderId){
        return this.model.getOrderTasks(idRest, orderId);
    }

    public List<String> getListaRefeicoes() {
        return buildFormattedList(this.model.getMealNames());
    }

    public List<String> getListaBebidas() {
        return buildFormattedList(this.model.getDrinkNames());
    }

    private List<String> buildFormattedList(Set<String> names) {
        List<String> result = new ArrayList<>();
        if (names == null) return result;

        for (String name : names) {
            String details = this.model.getItemDetails(name); 
            
            if (details != null) {
                result.add(name + ";" + details);
            }
        }
        return result;
    }

   public Set<String> getMensagensRecebidas(String idFunc) {
        return this.model.getReceivedMessages(idFunc);
    }

    public Set<String> getMensagensEnviadas(String idFunc) {
        return this.model.getSentMessages(idFunc);
    }

    public void enviarMensagem(String idSender, String idDest, String texto) {
        this.model.sendMessage(idSender, idDest, texto);
    }

    public boolean alterarPassword(String idFunc, String novaPass) {
        return this.model.changePassword(idFunc, novaPass);
    }

    public String getStockLocal(String idRest) {
        return this.model.listRestIngredients(idRest);
    }

    public void realizarEncomendaStock(String idRest, String ingrediente, int qtd) {
        this.model.createIngredientOrder(idRest, ingrediente, qtd);
    }

    public String getEncomendasPendentes(String idRest) {
        return this.model.listPendingIngredientOrders(idRest);
    }

    public void registarRecebimentoStock(String idRest, String orderID) {
        this.model.receiveStockOrder(idRest, orderID);
    }

    public String getRestId(String idFunc) {
        return this.model.getRestID(idFunc);
    }

    public String getPedidosPendentes(String idRest) {
        return this.model.getNextOrders(idRest);
    }

    public String getResumoPedido(String idRest, String orderId) {
        return this.model.getOrderResume(idRest, orderId);
    }

    public void concluirTarefa(String idRest, String orderId, String taskId) {
        this.model.changeTaskState(idRest, orderId, taskId);
    }

    public void adiarPedido(String idRest, String orderId, LocalDateTime novaHora) {
        this.model.delayOrder(idRest, orderId, novaHora);
    }

    public void adicionarFuncionario(String idRest, String name, String email, String phone, String role) {
        this.model.addFunc(idRest, name, email, phone, role);
    }

    public void removerFuncionario(String idFunc) {
        this.model.removeFunc(idFunc);
    }

    public void relocarFuncionario(String idFunc, String novoRole) {
        this.model.relocateFun(idFunc, novoRole);
    }

    public void adicionarRestaurante(String id, String nome, String localizacao) {
        this.model.addRestaurant(id, nome, localizacao);
    }

    public boolean existeRestaurante(String idRest) {
        Set<String> restaurantes = this.model.listRestaurants(); 
        if (restaurantes == null) return false;
        
        for (String r : restaurantes) {
            if (r.equalsIgnoreCase(idRest)) return true;
        }
        return false;
    }

    public List<String> listarFuncionarios(String idRest) {
        return this.model.listEmployees(idRest);
    }   

    public String consultarRelatorioCadeia(LocalDate start, LocalDate end, List<String> metricas) {
        return this.model.consultChainReport(start, end, metricas);
    }

    public String consultarRelatorioRestaurante(String idRest, LocalDate start, LocalDate end, List<String> metricas) {
        return this.model.consultRestaurantReport(idRest, start, end, metricas);
    }


    public boolean isItemDisponivel(String idRest, String nomeItem) {
        Map<String, Integer> teste = new HashMap<>();
        teste.put(nomeItem, 1);
        return this.model.hasOrderStock(idRest, teste);
    }

    public boolean checkStockQuantidade(String idRest, String nomeItem, int qtd) {
        Map<String, Integer> teste = new HashMap<>();
        teste.put(nomeItem, qtd);
        return this.model.hasOrderStock(idRest, teste);
    }

    public void registarPedidoCliente(String idRest, Map<String, Integer> carrinho, String tipoPedido, String notas) {
        List<String> itemsList = new ArrayList<>();
        
        for (Map.Entry<String, Integer> entry : carrinho.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                itemsList.add(entry.getKey());
            }
        }
        
        String finalNotes = (notas == null || notas.trim().isEmpty()) 
                          ? "Cliente POS (" + tipoPedido + ")" 
                          : notas + " (" + tipoPedido + ")";

        this.model.createOrder(idRest, tipoPedido, itemsList, finalNotes, LocalDateTime.now());
    }

    public String consultarTopVendas(String idRest, LocalDate inicio, LocalDate fim, int n) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- TOP ").append(n).append(" VENDAS ---\n");
        
        List<Map.Entry<String, Integer>> topList = this.model.getTopNSales(idRest, inicio, fim, n);
        
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topList) {
            sb.append(rank++).append(". ")
              .append(entry.getKey()).append(" - ")
              .append(entry.getValue()).append(" un.\n");
        }

        return sb.toString();
    }

    public void contactRestaurant(String idFunc, String idRest, String text) {
        this.model.contactRest(idFunc, idRest, text);
    }

    public void removerStock(String idRest, String ingrediente, int qtd) {
        this.model.deleteIngredientStock(idRest, ingrediente, qtd);
    }

    public Map<String, Integer> getStockItems(String idRest) {
        String rawReport = this.model.listRestIngredients(idRest);
        Map<String, Integer> map = new HashMap<>();
        
        if (rawReport == null || rawReport.isEmpty()) return map;

        String[] lines = rawReport.split("\n");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("=")) continue; // Ignora separadores

            try {
                String nome;
                int qtd = 0;

                if (line.contains(":")) {
                    String[] parts = line.split(":");
                    int splitIndex = line.indexOf(":");
                    if (line.contains("(")) {
                         int parenIndex = line.indexOf("(");
                         if (parenIndex < splitIndex) splitIndex = parenIndex;
                    }
                    
                    nome = line.substring(0, splitIndex).replace("-", "").trim();
                    
                    String numberPart = line.substring(splitIndex).replaceAll("[^0-9]", "");
                    if (!numberPart.isEmpty()) {
                        qtd = Integer.parseInt(numberPart);
                    }
                    
                    if (!nome.isEmpty()) {
                        map.put(nome, qtd);
                    }
                }
            } catch (Exception e) {
                System.err.println("Erro ao processar linha de stock: " + line);
            }
        }
        return map;
    }
}