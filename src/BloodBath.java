import java.rmi.Naming;

public class BloodBath {
    public static void main(String[] args) {
        try{
            SteamHammer obj = new Anvil();
            Naming.rebind("//localhost/TheSteamHammer", obj);
            System.out.println("SteamHammer bound.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
