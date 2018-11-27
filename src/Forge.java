import java.rmi.Naming;

public class Forge {

    public static void press(boolean isConsumer){
        try {
            String type;
            int runs = 0;
            SteamHammer obj = (SteamHammer) Naming.lookup("//localhost/SteamHammerStorage");
            if(isConsumer){
                type = "Consumer";
            }else{
                type = "Producer";
            }
            System.out.println("Press is working as a " + type + " with " + obj.version());
            if(!isConsumer) {
                while(runs < 100000){
                    obj.produce(1, 10);
                    runs++;
                }
            }else {
                while(runs < 100000){
                    obj.consume(new int[]{1}, new int[]{5});
                    runs++;
                }
            }

            System.out.println("Results");
            System.out.println("Produced: " + obj.produced() + " & Consumed: " + obj.consumed());
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        press(args.length > 0);
    }
}
