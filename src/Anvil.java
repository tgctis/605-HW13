import java.rmi.Naming;

public class Anvil {

    private String anvilID;
    private String server;
    private String rmiName;
    private int type;
    private int productionRate;

    public Anvil(String anvilID, int type, int productionRate, String server, String rmiName){
        this.anvilID = anvilID;
        this.server = server;
        this.rmiName = rmiName;
        this.type = type;
        this.productionRate = productionRate;
    }

    public void press(){
        try {
            int runs = 0;
            SteamHammer obj = (SteamHammer) Naming.lookup("//"+server+"/"+rmiName);

            System.out.println("Anvil #" + anvilID + " is working as a Producer making" +
                    "\n with " + obj.version() + "\n");

            while(runs < 100000){
                obj.produce(type, productionRate);
                runs++;
            }

            System.out.println("Status when Anvil #"+anvilID+" completed - Produced: "
                    + obj.produced() + " & Consumed: " + obj.consumed() + "\n");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){

        new Anvil(args[0], args[1], args[2], args[3], args[4]).press();
    }
}
