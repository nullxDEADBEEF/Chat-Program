import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

// NOTE: Client must send a "hearbeat alive" message every minute
// NOTE: Client must send a Quit message when it is closing

public class Client implements Runnable {
    private static InetAddress host;
    private static final int PORT =  1337;
    private static String username;

    public void run() {

    }

    public static void main( String[] args ) {
        System.out.print( "Enter your desired chat-name: " );
        Scanner inputScanner = new Scanner( System.in );
        username = inputScanner.nextLine();

        try {
            host = InetAddress.getLocalHost();
        } catch ( UnknownHostException e ) {
            System.out.println( "Unable to find ID of host!" );
            e.printStackTrace();
            System.exit( 1 );
        }

        connectToServer();
    }

    private static void connectToServer() {
        Socket connection = null;

        try {
            connection = new Socket( host, PORT );
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
