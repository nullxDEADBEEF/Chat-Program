import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

// TODO: broadcast written messages to all connected clients
// TODO: check for username max 12 characters etc
// TODO: check for existing username
// TODO: check for heartbeat for each client
// TODO: send correct messages to/from the server

/*
Takes care of all incoming messages/connections from clients
and prints appropriate responses
 */
public class Server {
    private static ServerSocket serverSocket;
    private static final int PORT = 4200;
    private static final int MAX_THREADS = 5;
    private static ThreadPoolExecutor threadPoolExecutor;
    // we use a vector since they are thread-safe
    private static Vector<Socket> activeUsers;

    private static final String ACCEPT_CLIENT = "J_OK";

    public static void main( String[] args ) {
        System.out.println( "Opening port: " + PORT );

        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool( MAX_THREADS );
        activeUsers = new Vector<>();

        try {
            serverSocket = new ServerSocket( PORT );
        } catch ( IOException e ) {
            System.out.println( "Unable to attach port!" );
            e.printStackTrace();
            System.exit( 1 );
        }

        while ( !serverSocket.isClosed() ) {
            handleClientMessages();
        }

        threadPoolExecutor.shutdown();
    }

    /*
    handle messages from the client and print them out
    while also printing the appropriate protocol responses to the client
     */
    private static void handleClientMessages() {
        // listen for a connection to be made to the server socket
        while ( true ) {
            try {
                // create a socket to establish connection
                Socket clientConnection = serverSocket.accept();
                DataInputStream clientInput = new DataInputStream( clientConnection.getInputStream() );
                DataOutputStream serverOutput = new DataOutputStream( clientConnection.getOutputStream() );

                String clientJoinText = clientInput.readUTF();
                while ( true ) {
                    if ( clientJoinText.startsWith( "JOIN" ) ) {
                        serverOutput.writeUTF( ACCEPT_CLIENT );
                        serverOutput.flush();
                        break;
                    }

                    serverOutput.writeUTF( "J_ER 1: Unknown command" );
                    serverOutput.flush();
                    clientJoinText = clientInput.readUTF();
                }
                // we keep a collection of socket output streams to be able to
                // write to all clients
                activeUsers.add( clientConnection );
                Runnable serverThread = new ServerThread( clientConnection );
                threadPoolExecutor.execute( serverThread );
            } catch ( IOException e ) {
                System.out.println( "Could not accept" );
                e.printStackTrace();
                System.exit( 1 );
            }
        }
    }

    public static void broadcastMessage( String message ) {
        for ( Socket s : activeUsers ) {
            try {
                DataOutputStream outputStream = new DataOutputStream( s.getOutputStream() );
                outputStream.writeUTF( "> " + message );
            } catch ( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
