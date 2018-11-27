import java.rmi.Naming;

public class Forge {

    public static void press(){
        try {
            SteamHammer obj = (SteamHammer) Naming.lookup("//localhost/TheSteamHammer");
            System.out.println(obj.version());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        press();
    }
}
