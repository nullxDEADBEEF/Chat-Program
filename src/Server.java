import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

// TODO: assign a thread when a client connects
// TODO: broadcast written messages to all connected clients
// TODO: figure out how to do it with the given threadpool
// TODO: check for username max 12 characters etc
// TODO: check for existing username
// TODO: check for heartbeat for each client
// TODO: send correct messages to/from the server

public class Server {
    private static ServerSocket serverSocket;
    private static final int PORT = 4200;
    private static final int MAX_THREADS = 5;
    private static final int MAX_CLIENTS = 5;
    private static ThreadPoolExecutor threadPoolExecutor;
    private static Vector<Socket> activeUsers;

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
     */
    private static void handleClientMessages() {
        // listen for a connection to be made to the server socket
        while ( true ) {
            try {
                // create a socket to establish connection
                Socket clientConnection = serverSocket.accept();
                Runnable r = new ServerThread( clientConnection );
                threadPoolExecutor.execute( r );
            } catch ( IOException e ) {
                System.out.println( "Could not accept");
                e.printStackTrace();
                System.exit( 1 );
            }
        }
    }
}
