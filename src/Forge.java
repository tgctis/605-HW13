import java.rmi.Naming;

public class Forge {

    String forgeID;
    String server;
    String rmiName;

    public Forge(String forgeID, String server, String rmiName){
        this.forgeID = forgeID;
        this.server = server;
        this.rmiName = rmiName;
    }

    public void smelt(){
        try {
            int runs = 0;
            SteamHammer obj = (SteamHammer) Naming.lookup("//"+this.server+"/"+this.rmiName);

            System.out.println("Forge #" + forgeID + " is working as a Consumer with " + obj.version() + "\n");
            while(runs < 100000){
                obj.consume(new int[]{1, 2, 3}, new int[]{3, 5, 2});
                runs++;
            }

            System.out.println("Status when Forge #"+forgeID+" Completed -  Produced: "
                    + obj.produced() + " & Consumed: " + obj.consumed() + "\n");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        new Forge(args[0], args[1], args[2]).smelt();
    }
}
