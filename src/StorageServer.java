import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * StorageServer.java
 *
 * Version:
 * $Id$
 *
 * Revisions:
 * $Log$
 *
 * Handles storage for ProducerConsumer
 *
 * @author  Timothy Chisholm
 * @author  Jake Groszewski
 *
 *
 */
public class StorageServer extends UnicastRemoteObject
        implements SteamHammer {
    private static Bucket bucket;
    private static final String version = "Version 0.3";

    /**
     * Primary Constructor - Initializes the storage
     * @param initialCapacity
     */
    public StorageServer(int initialCapacity) throws RemoteException {
        try{
            bucket = new Bucket(initialCapacity);
        }catch(Exception e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Returns a usage message
     * @return usage message string
     */
    private static String usageMessage(){
        return "Usage: java StorageServer [CONNECTION_PORT][STORAGE_CAPACITY]";
    }

    /**
     * Gets the version number, for RMI purposes
     * @return the version number string.
     */
    public String version(){
        return version;
    }

    public void consume(int[] types, int[] amounts){
        synchronized (bucket){
            bucket.multiConsume(types, amounts);
        }
    }

    public void produce(int type, int amount){
        synchronized (bucket){
            bucket.produce(type, amount);
        }
    }

    public int produced(){
        synchronized (bucket) {
            return bucket.produced;
        }
    }

    public int consumed(){
        synchronized (bucket) {
            return bucket.consumed;
        }
    }

    /**
     * Main, parses the command line args and then let's the server run
     * @param args
     */
    public static void main(String[] args){
        int capacity;

        if(args.length != 1){
            System.err.println(usageMessage());
            System.exit(-1);
        }

        try{
            capacity = Integer.parseInt(args[0]);
            SteamHammer storage = new StorageServer(capacity);
            Naming.rebind("//localhost/SteamHammerStorage", storage);
            System.out.println("SteamHammer at pressure.");
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Generic object that's created and consumed by producers/consumers
     */
    public class Widget{
        int type;

        private Widget(int type){
            this.type = type;
        }
    }

    /**
     * Generic storage object
     */
    public class Bucket{
        private int capacity;
        private int current = 0;
        private int consumed = 0;
        private int produced = 0;
        private ArrayList<Widget> container = new ArrayList<>();

        /**
         * Generate a storage area with limited capacity
         * @param initialCapacity max capacity
         */
        public Bucket(int initialCapacity){
            capacity = initialCapacity;
        }

        /**
         * Produces if possible
         * @param type type of widget to make
         * @param amount amount of widget to make
         */
        public synchronized void produce(int type, int amount){
            if(canProduce(type, amount)){
                for(int count = 0; count < amount; count++){
                    container.add(new Widget(type));
                    produced++;
                    current++;
                }
            }
        }

        /**
         * Ensures that production can succeed
         * @param type type of widgets to produce
         * @param amount amount of widget produced
         * @return true if can produce
         */
        private synchronized boolean canProduce(int type, int amount){
            return current + amount < capacity && numOfType(type) < (capacity/3);
        }

        /**
         * Consumes multiple types in varying amounts
         * @param types array of integer, types to consume
         * @param amounts array of integer, amounts of each type to consume
         */
        private synchronized void multiConsume(int[] types, int[] amounts){
            assert(types.length == amounts.length);

            boolean canConsume = true;
            for(int index = 0; index < types.length; index++){
                int type = types[index];
                int amount = amounts[index];

                canConsume &= canConsume(type, amount);
            }

            if(canConsume){
                for(int index = 0; index < types.length; index++){
                    int type = types[index];
                    int amount = amounts[index];
                    consume(type, amount);
                }
            }

        }

        /**
         * Does the actual consumption
         * @param type type of widget to eat
         * @param amount amount to consume
         */
        public synchronized void consume(int type, int amount){
            if(canConsume(type, amount)){
                ArrayList<Widget> tmp = new ArrayList<>(container);
                container.clear();
                int tmpConsumed = 0;
                for(Widget widget : tmp){
                    if(widget.type == type && tmpConsumed < amount){
                        tmpConsumed++;
                        consumed++;
                        current--;
                    }else{
                        container.add(widget);
                    }
                }
            }
        }

        /**
         * Checks if consumption will succeed
         * @param type type of widget to eat
         * @param amount amount to eat
         * @return true if able to consume
         */
        private synchronized boolean canConsume(int type, int amount){
            //Check for count of types
            return numOfType(type) > amount;
        }

        /**
         * Returns how many of a certain type of widget are in the storage
         * @param type type of widget
         * @return integer amount of widgets of passed in type
         */
        private synchronized int numOfType(int type){
            int numOfType = 0;

            for(Widget widget : container){
                if(widget.type == type){
                    numOfType++;
                }
            }

            return numOfType;
        }
    }
}
