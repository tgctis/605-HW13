import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Anvil extends UnicastRemoteObject implements SteamHammer{
    public Anvil() throws RemoteException{

    }
    public String version(){
        return "Version 0.01";
    }
}
