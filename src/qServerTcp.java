import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * qServerTcp.java
 *
 * Version:
 * $Id$
 *
 * Revisions:
 * $Log$
 *
 * Serves a quote to all those who connect
 * Now with concurrent TCP/IP!
 *
 * @author  Timothy Chisholm
 * @author  Jake Groszewski
 *
 *
 */
public class qServerTcp extends Thread{
    private ServerSocket serverSocket;
    private int port = 32010;
    private static int servCount = 0;
    private static ArrayList<String> quotes;


    private qServerTcp(int newPort, ArrayList<String> theQuotes){
        this.port = newPort;
        quotes = theQuotes;
        openServer();
    }

    private qServerTcp(int newPort){
        servCount++;
        port = newPort;
        openServer();
    }

    private void openServer(){
        try{
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port: " + serverSocket.getLocalPort());
        }catch(Exception e){
            System.out.println(e);
        }
    }

    private int getLocalPort(){
        return serverSocket.getLocalPort();
    }

    private String getQuote(){
        return quotes.get(getLocalPort() % quotes.size());
    }

    /**
     * Does the server stuff
     */
    public void run(){
        try{
            for(;;){
                Socket client = serverSocket.accept();
                System.out.println("Current # Connections : " + servCount);
                System.out.println("Waiting...");
                try{sleep(5000);}catch(Exception e){e.printStackTrace();}
                System.out.println(client);
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                writer.println(getQuote());
                client.close();
                servCount--;
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(e);
        }
    }

    /**
     * Waits for connections and serves them up in threads.
     */
    public void listen(){
        try{
            for(;;){
                Socket client = serverSocket.accept();
                System.out.println(client);
                //someone has connected, start a new server thread...
                qServerTcp aServer = new qServerTcp(0); //first available port
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                aServer.start();
                //let the client know of a new port
                writer.println(aServer.getLocalPort());
                //close the outfacing port to allow for another client
                client.close();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }

    public static String usageString(){
        return "Usage: java qServerTcp [LISTENING_PORT][QUOTES_FILE]";
    }

    public static void main(String[] args){
        int port = 0;
        String fileName;
        ArrayList<String> quotes = new ArrayList<>();
        if(args.length < 2){
            System.out.println(usageString());
            System.exit(-1);
        }else{
            try{
                port = Integer.parseInt(args[0]);
                fileName = args[1];

                BufferedReader reader = new BufferedReader(new FileReader(fileName));
                String line;
                while((line = reader.readLine()) != null){
                    quotes.add(line);
//                    System.out.println("Quote: " + line);
                }

                if(quotes.size() < 1)
                    throw new Exception("No quotes to read!");

            }catch(Exception e){
                e.printStackTrace();
            }
        }

        qServerTcp server = new qServerTcp(port, quotes);
        server.listen();
    }
}
