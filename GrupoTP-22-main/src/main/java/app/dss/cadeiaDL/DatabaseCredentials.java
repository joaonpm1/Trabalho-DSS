package app.dss.cadeiaDL;

public class DatabaseCredentials {
    // Padrão: jdbc:mysql://<host>:<porta>/<nome_da_bd>
    
    private static final String url = "jdbc:mysql://localhost:3306/dss_cadeia_db";
    
    private static final String username = "dss_user";
    private static final String password = "dss2526";
    
    public static String getUrl() { return url; }
    public static String getUsername() { return username; }
    public static String getPassword() { return password; }
}