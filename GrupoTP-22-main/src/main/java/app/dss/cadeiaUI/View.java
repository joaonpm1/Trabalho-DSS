package app.dss.cadeiaUI;

public interface View {
    
    default View run() {
        return null;
    }

    default View run(String idFunc) {
        return null;
    }

    default void limparEcra() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}