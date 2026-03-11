package app.dss;

import app.dss.cadeiaLN.ICadeiaLN;
import app.dss.cadeiaLN.cadeiaLNFacade;
import app.dss.cadeiaUI.LoginView;
import app.dss.cadeiaUI.LoginController;
import app.dss.cadeiaUI.View;

public class App{
    public static void main(String[] args){
        ICadeiaLN cadeiaDB = new cadeiaLNFacade();
        
        LoginController controller = new LoginController(cadeiaDB);
        View nextView = new LoginView(controller);

        do{
            nextView = nextView.run();
        } while (nextView != null);
    }
}