import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// NOTE: Client must send a "hearbeat alive" message every minute

/*
Makes a connection to a server so that the client can chat with other clients
 */
public class Client implements Runnable {
    private static InetAddress host;
    private static final int PORT =  4200;
    private static String username;

    private final String CLIENT_ACCEPTED = "J_OK";
    private final String CLIENT_QUIT = "QUIT";

    public Client() {}

    public static void main( String[] args ) {
        System.out.println( "To join the chat do: JOIN <<username>>" );
        try {
            host = InetAddress.getLocalHost();
        } catch ( UnknownHostException e ) {
            System.out.println( "Unable to find ID of host!" );
            e.printStackTrace();
            System.exit( 1 );
        }

        Client client = new Client();
        client.run();
    }

    /*
    This method is run when we are starting a new client thread,
    the thread takes care of the communication from the client to the server
     */
    public void run() {
        Socket connection = null;

        Scanner inputScanner = new Scanner( System.in );
        // "JOIN"
        String joinString = inputScanner.next();

        try {
            connection = new Socket( host, PORT );

            Scanner userInput = new Scanner( System.in );
            BufferedReader serverResponse = new BufferedReader( new InputStreamReader( connection.getInputStream() ) );
            // we set the printwriter to autoflush the output buffer so that no data remains after a message has been send
            PrintWriter clientOutput = new PrintWriter( connection.getOutputStream(), true );
            clientOutput.println( joinString );

            /*
            Keep asking for correct JOIN command
             */
            String response = serverResponse.readLine();
            username = inputScanner.next();
            while ( !response.equals( CLIENT_ACCEPTED ) ) {
                System.out.println( "To join the chat do: JOIN <<username>>" );
                joinString = userInput.next();
                username = userInput.next();
                clientOutput.println( joinString );
                response = serverResponse.readLine();
                userInput.nextLine();
            }


            /*
            Correspondence between client and server
             */
            String userText = "";
            while ( !userText.equals( CLIENT_QUIT ) ) {
                System.out.print( "Enter message: " );
                userText = userInput.nextLine();
                clientOutput.println( userText );
                response = serverResponse.readLine();
                System.out.println( "\n" + username + " - " + response );
            }
        } catch ( IOException e ) {
            System.out.println( "Could not connect!" );
            e.printStackTrace();
            System.exit( 1 );
        } finally {
            try {
                System.out.println( "*** CLOSING CONNECTION... ***" );
                connection.close();
            } catch ( IOException e ) {
                System.out.println( "Unable to disconnect!" );
                System.exit( 1 );
            }
        }
    }
}
