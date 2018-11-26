import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * ProducerClient.java
 *
 * Version:
 * $Id$
 *
 * Revisions:
 * $Log$
 *
 * Handles production for ProducerConsumer
 *
 * @author  Timothy Chisholm
 * @author  Jake Groszewski
 *
 *
 */
public class ProducerClient extends Thread{
    private int port = 32010;
    private String host = "localhost";
    private static Object o = new Object();
    private static int numClients = 0;
    private int type;
    private int amount;
    private int id;

    public ProducerClient(int type, int rate, String hostName, int newPort){
        this.id = ++numClients;
        System.out.println("Worker #" + this.id + " making Type #" + type);
        this.type = type;
        this.amount = rate;
        this.port = newPort;
        this.host = hostName;
    }

    /**
     * Gets an operational socket and then spams the storage server with
     * PRODUCE and lets the storage server figure out what it should do
     */
    public void run(){
        try{
            int newPort;
            Socket sock = new Socket(this.host, this.port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            newPort = Integer.parseInt(reader.readLine());
            reader.close();
            sock.close();

            System.out.println("---> #" + this.id + " is making type #"+ this.type +"\nAnd is connecting to "
                    + this.host + " on original port: " + this.port + " redirected to port: " + newPort);

            //open the new port and set up the streams
            sock = new Socket(this.host, newPort);
            PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
            while(true){
                synchronized (o) {
                    o.notify();
                    writer.println("PRODUCE");
                    writer.println(this.type);//type
                    writer.println(this.amount);//amount
                    o.wait();
                }
            }
        }catch(SocketException se){
            System.out.println("You have been disconnected from the server.");
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            numClients--;
        }
    }

    /**
     * Basic usage string
     * @return string explaining how to use the program
     */
    public static String usageString(){
        return "Usage java ProducerClient [SERVER][PORT][NUM_PRODUCERS][PRODUCTION_RATE]";
    }

    public static void main(String[] args){
        int clients = 3;
        int productionRate = 5;
        int port = 0;
        String hostName = "";
        if(args.length < 4){
            System.err.println(usageString());
            System.exit(-1);
        }else{
            try{
                hostName = args[0];
                port = Integer.parseInt(args[1]);
                clients = Integer.parseInt(args[2]);
                productionRate = Integer.parseInt(args[3]);
            }catch(Exception e){
                System.err.println(e);
            }
        }

        ProducerClient[] allClients = new ProducerClient[clients];

        for(int count = 0; count < clients; count++){
            allClients[count] = new ProducerClient(count+1, productionRate, hostName, port);
        }

        for(int count = 0; count < clients; count++){
            allClients[count].start();
        }

        for(int count = 0; count < clients; count++){
            try{
                allClients[count].join();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
