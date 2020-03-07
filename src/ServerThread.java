import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/*
Takes care of broadcasting a client message to all other clients
 */
public class ServerThread implements Runnable {
    protected Socket socket;

    public ServerThread( Socket socket ) {
        this.socket = socket;
    }

    /*
    When a server thread is started, this will run and get client input and print them out to other clients
     */
    public void run() {
        try {
            BufferedReader clientInput = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
            PrintWriter serverOutput = new PrintWriter( socket.getOutputStream(), true );

            String clientMessage = clientInput.readLine();
            while ( !clientMessage.equals( "QUIT" ) ) {
                serverOutput.println( clientMessage );
                clientMessage = clientInput.readLine();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println( "Closing..." );
                socket.close();
            } catch ( IOException e ) {
                System.out.println( "Could not disconnect!" );
                e.printStackTrace();
                System.exit( 1 );
            }
        }
    }
}
