import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServerThread implements Runnable {
    protected Socket socket;

    public ServerThread( Socket socket ) {
        this.socket = socket;
    }

    public void run() {
        try {
            Scanner clientInput = new Scanner( socket.getInputStream() );
            PrintWriter serverOutput = new PrintWriter( socket.getOutputStream(), true );

            String clientMessage = clientInput.nextLine();

            while ( !clientMessage.equals( "QUIT" ) ) {
                serverOutput.println( clientMessage );
                clientMessage = clientInput.nextLine();
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
