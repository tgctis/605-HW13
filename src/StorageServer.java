import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
public class StorageServer extends Thread{
    private ServerSocket serverSocket;
    private static Bucket bucket;

    /**
     * This parameterized constructor is used to create new instances of the server
     * so multiple clients can connect.
     * @param port the port (usually 0, first available) to be used.
     */
    public StorageServer(int port){
        try{
            serverSocket = new ServerSocket(port);
        }catch(Exception e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Primary Constructor - Initializes the storage
     * @param port
     * @param initialCapacity
     */
    public StorageServer(int port, int initialCapacity){
        try{
            serverSocket = new ServerSocket(port);
            bucket = new Bucket(initialCapacity);
        }catch(Exception e){
            System.err.println(e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Gets the capacity of the bucket
     * @return bucket size
     */
    private int getCapacity(){
        return bucket.capacity;
    }

    /**
     * Returns the local port used for connecting to the client
     * @return local port of this instance of server
     */
    private int getLocalPort(){
        return serverSocket.getLocalPort();
    }

    /**
     * Returns a usage message
     * @return usage message string
     */
    private static String usageMessage(){
        return "Usage: java StorageServer [CONNECTION_PORT][STORAGE_CAPACITY]";
    }

    /**
     * Listens on a specific port, and assigns connections to different ports to allow for simultaneous connections
     */
    public void listen(){
        try {
            for (; ; ) {
                Socket client = serverSocket.accept();
                System.out.println("New connection from : " + client.toString());
                StorageServer aServer = new StorageServer(0);
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                aServer.start();
                writer.println(aServer.getLocalPort());
                client.close();
            }
        }catch(SocketException se){
            System.out.println("Client has disconnected.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Runs the instance of the server, one per client
     */
    public void run(){
        try{
            Socket client = serverSocket.accept();
//            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            while(true){
                synchronized (bucket){
                    bucket.notify();
                    String message = reader.readLine();

//                    writer.println("GO");
                    if(message.equalsIgnoreCase("PRODUCE")){
                        int type = Integer.parseInt(reader.readLine());
                        int amount = Integer.parseInt(reader.readLine());
                        bucket.produce(type, amount);
                        System.out.println("Total Produced = " + bucket.produced);
                    }else if(message.equalsIgnoreCase("CONSUME")){
                        int numTypes = 3;
                        int type;
                        int amount;
                        int[] types = new int[numTypes];
                        int[] amounts = new int[numTypes];
                        for(int index = 0; index < numTypes; index++) {
                            type = Integer.parseInt(reader.readLine());
                            amount = Integer.parseInt(reader.readLine());
                            types[index] = type;
                            amounts[index] = amount;
                        }
                        bucket.multiConsume(types, amounts);
                        System.out.println("Total Consumed = " + bucket.consumed);
                    }

                    bucket.wait();
                }
            }
        }catch(SocketException se){
            System.out.println("Client has disconnected.");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Main, parses the command line args and then let's the server run
     * @param args
     */
    public static void main(String[] args){
        int port;
        int capacity;
        StorageServer server = null;

        if(args.length != 2){
            System.err.println(usageMessage());
            System.exit(-1);
        }

        try {
            port = Integer.parseInt(args[0]);
            capacity = Integer.parseInt(args[1]);
            server = new StorageServer(port, capacity);
        }catch(Exception e){
            e.printStackTrace();
            System.exit(-1);
        }

        server.listen();
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
        private synchronized void produce(int type, int amount){
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
        private synchronized void consume(int type, int amount){
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
