import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * ConsumerClient.java
 *
 * Version:
 * $Id$
 *
 * Revisions:
 * $Log$
 *
 * Handles consumption for ProducerConsumer
 *
 * @author  Timothy Chisholm
 * @author  Jake Groszewski
 *
 *
 */
public class ConsumerClient extends Thread{
    private int port = 32010;
    private String host = "localhost";
    private static Object o = new Object();
    private static int numClients = 0;
    private int id;

    /**
     * Consumer Constructor
     * @param hostName host of the storage server
     * @param newPort port of the storage server
     */
    public ConsumerClient(String hostName, int newPort){
        this.id = ++numClients;
        System.out.println("Consumer #" + this.id );
        this.port = newPort;
        this.host = hostName;
    }

    /**
     * This will get a new port from the server then begin communication
     * Communication consists of spamming CONSUME to the server and let the storage
     * figure out what it should do.
     */
    public void run(){
        try{
            int newPort;
//            String message;
            //Connect with the default port, get a connecting port to work with
            Socket sock = new Socket(this.host, this.port);
            BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            newPort = Integer.parseInt(reader.readLine());
            reader.close();
            sock.close();

            System.out.println("---> #" + this.id + " is Consuming\nAnd is connecting to "
                    + this.host + " on original port: " + this.port + " redirected to port: " + newPort);

            //open the new port and set up the streams
            sock = new Socket(this.host, newPort);
//            reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);
            while(true){
                synchronized (o) {
                    o.notify();
                    writer.println("CONSUME");
                    writer.println("1");//type
                    writer.println("3");//amount

                    writer.println("2");//type
                    writer.println("5");//amount

                    writer.println("3");//type
                    writer.println("2");//amount
                    if(numClients > 1)
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
     * Basic useage string
     * @return how to use the program
     */
    public static String usageString(){
        return "Usage java ConsumerClient [SERVER][PORT][NUM_CONSUMERS]";
    }

    public static void main(String[] args) {
        int clients = 3;
        int port = 0;
        String hostName = "";
        if(args.length < 3){
            System.err.println(usageString());
            System.exit(-1);
        }else{
            try{
                hostName = args[0];
                port = Integer.parseInt(args[1]);
                clients = Integer.parseInt(args[2]);
            }catch(Exception e){
                System.err.println(e);
            }
        }

        ConsumerClient[] allClients = new ConsumerClient[clients];

        for(int count = 0; count < clients; count++){
            allClients[count] = new ConsumerClient(hostName, port);
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
