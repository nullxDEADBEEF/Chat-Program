import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// NOTE: Client must send a "hearbeat alive" message every minute
// NOTE: Client must send a Quit message when it is closing

public class Client implements Runnable {
    private static InetAddress host;
    private static final int PORT =  4200;
    private static String username;

    public Client() {}

    public static void main( String[] args ) {
        System.out.println( "To join the chat do: JOIN <<username>>" );
        Scanner inputScanner = new Scanner( System.in );

        String joinCommand = inputScanner.nextLine();
        while ( !joinCommand.startsWith( "JOIN" ) ) {
            System.out.println( "Unrecognized command, to join the chat do: JOIN <<username>>" );
            joinCommand = inputScanner.nextLine();
        }
        username = joinCommand.substring( 5 );

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

    public void run() {
        Socket connection = null;

        try {
            connection = new Socket( host, PORT );

            Scanner userInput = new Scanner( System.in );
            Scanner serverResponse = new Scanner( connection.getInputStream() );
            // we set the printwriter to autoflush the output buffer so that no data remains after a message has been send
            PrintWriter clientOutput = new PrintWriter( connection.getOutputStream(), true );

            String userText = "";
            String response = serverResponse.nextLine();
            if ( response.equals( "J_OK" ) ) {
                System.out.println( response );
                while (!userText.equals("QUIT")) {
                    System.out.print("Enter message: ");
                    userText = userInput.nextLine();
                    clientOutput.println(userText);
                    response = serverResponse.nextLine();
                    System.out.println("\n" + getUsername() + " - " + response);
                }
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

    public String getUsername() {
        return username;
    }
}
