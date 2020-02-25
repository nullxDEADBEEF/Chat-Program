import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

// NOTE: Must use threads
// NOTE: Must accept more than 1 client to join the chat system

public class Server {
    private static ServerSocket serverSocket;
    private static final int PORT = 1337;
    private static final int MAX_THREADS = 5;
    private static final int MAX_CLIENTS = 5;
    private static ThreadPoolExecutor threadPoolExecutor;
    private static ArrayList<String> activeUsers;

    public static void main( String[] args ) {
        System.out.println( "Opening port: " + PORT );

        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool( MAX_THREADS );
        activeUsers = new ArrayList<>();

        try {
            serverSocket = new ServerSocket( PORT );
        } catch ( IOException e ) {
            System.out.println( "Unable to attach port!" );
            e.printStackTrace();
            System.exit( 1 );
        }

        while ( !serverSocket.isClosed() ) {
            serveClient();
        }

        threadPoolExecutor.shutdown();
    }

    /*
    handle messages from the client
     */
    private static void serveClient() {
        // create a socket to establish connection
        Socket clientConnection = null;

        // listen for a connection to be made to the server socket
        try {
            clientConnection = serverSocket.accept();


        } catch ( IOException e ) {
            e.printStackTrace();
        } finally {
            try {
                System.out.println( "Closing..." );
                clientConnection.close();
            } catch ( IOException e ) {
                System.out.println( "Could not disconnect!" );
                e.printStackTrace();
                System.exit( 1 );
            }
        }
    }
}
