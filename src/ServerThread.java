import java.io.*;
import java.net.Socket;

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
            DataInputStream clientInput = new DataInputStream( socket.getInputStream() );

            String clientMessage;
            do {
                clientMessage = clientInput.readUTF();
                System.out.println( "Received a message" );
                Server.broadcastMessage( clientMessage );
            } while ( !clientMessage.equals( "QUIT" ) );

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
