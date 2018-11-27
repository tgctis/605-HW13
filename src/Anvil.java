import java.io.InterruptedIOException;
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

            System.out.println("Anvil #" + anvilID + " is working as a Producer making type " +
                    type + " objects at a rate of " + productionRate + " per cycle with " + obj.version() + "\n");

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
        if(args.length < 5){
            System.err.println("Usage: java Anvil [ID][Type][Rate][Server][Rmi Obj Name]");
            System.exit(-1);
        }
        try{
            String id = args[0];
            int type = Integer.parseInt(args[1]);
            int amount = Integer.parseInt(args[2]);
            String server = args[3];
            String objName = args[4];

            new Anvil(id, type, amount, server, objName).press();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
