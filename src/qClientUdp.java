import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * qClientUdp.java
 *
 * Version:
 * $Id$
 *
 * Revisions:
 * $Log$
 *
 * Will connect to a server and grab a quote of the day
 *
 * @author  Timothy Chisholm
 * @author  Jake Groszewski
 *
 *
 */
public class qClientUdp {

    //Usage string
    private static final String usage = "Usage: java qClientUdp [SERVER_URI][PORT]";

    public static void main(String[] args){
        int socketNum = 32010;
        String hostName = "localhost";

        try {
            if(args.length < 2){
                System.err.println(usage);
                System.exit(-1);
            }else{
                hostName = args[0];
                socketNum = Integer.parseInt(args[1]);
            }

            /*This gets a port to do work with*/
            Socket sock = new Socket(hostName, socketNum);
            BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            int newPort = Integer.parseInt(in.readLine());
            sock.close();

            /* with new port */
            sock = new Socket(hostName, newPort);
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            System.out.println("Today's quote: " + in.readLine());
            in.close();
            sock.close();

        }catch(UnknownHostException e){
            System.out.println("Unknown host.");
        }catch(Exception e){
            System.out.println(e);
        }
    }
}
