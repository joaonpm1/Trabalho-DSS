package app.dss.cadeiaUI;

import app.dss.cadeiaLN.ICadeiaLN;

public class LoginController extends Controller{
    public LoginController (ICadeiaLN model){
        super(model);
    }

    public void Login(String num, String password, String userType){
        switch(userType){
            case "Client":
                break;
            case "Func":
                break;
        }
    }
}