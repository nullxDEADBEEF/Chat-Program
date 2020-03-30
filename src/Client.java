import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

// NOTE: Client must send a "hearbeat alive" message every minute

/*
Makes a connection to a server so that the client can chat with other clients
 */
public class Client implements Runnable {
    private static InetAddress host;
    private static final int PORT =  4200;

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
        BufferedReader userInput = null;
        DataInputStream serverReponse = null;
        DataOutputStream clientOutput = null;

        BufferedReader inputScanner = new BufferedReader( new InputStreamReader( System.in ) );
        // "JOIN"
        String joinString = "";
        try {
            joinString = inputScanner.readLine();
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        try {
            connection = new Socket( host, PORT );

            userInput = new BufferedReader( new InputStreamReader( System.in ) );
            serverReponse = new DataInputStream( connection.getInputStream() );
            clientOutput = new DataOutputStream( connection.getOutputStream() );
            clientOutput.writeUTF( joinString );

            /*
            Keep asking for correct JOIN command
             */
            String response = serverReponse.readUTF();
            while ( !response.startsWith( CLIENT_ACCEPTED ) ) {
                System.out.println( response );
                System.out.println( "To join the chat do: JOIN <<username>>" );
                joinString = userInput.readLine();
                clientOutput.writeUTF( joinString );
                clientOutput.flush();
                response = serverReponse.readUTF();
            }

            String username = joinString.substring( 5 );

            System.out.println( response );
            /*
            Correspondence between client and server
             */

            String userText = "";
            Thread messageInput = new Thread( new MessageInput(  connection, username ) );
            messageInput.start();
            // TODO: find a way to make the thread run in the while loop
            do {
                /*
                System.out.print( "Enter message: " );
                userText = userInput.readLine();
                clientOutput.writeUTF( username + " - " + userText );
                clientOutput.flush();
                 */
                response = serverReponse.readUTF();
                System.out.println( response );
            } while ( !userText.equals( CLIENT_QUIT ) );

        } catch ( IOException e ) {
            System.out.println( "Could not connect!" );
            e.printStackTrace();
            System.exit( 1 );
        } finally {
            try {
                System.out.println( "*** CLOSING CONNECTION... ***" );
                userInput.close();
                clientOutput.close();
                serverReponse.close();
                connection.close();
            } catch ( IOException e ) {
                System.out.println( "Unable to disconnect!" );
                System.exit( 1 );
            }
        }
    }

    public static class MessageInput implements Runnable {
        private DataOutputStream clientOutput;
        private BufferedReader userInput;
        private Socket socket;
        private String username;

        public MessageInput( Socket socket, String username ) {
            this.socket = socket;
            this.username = username;
            userInput = new BufferedReader( new InputStreamReader( System.in ) );
            try {
                clientOutput = new DataOutputStream( socket.getOutputStream() );
            } catch ( IOException ex ) {
                ex.printStackTrace();
            }
        }

        @Override
        public void run() {
            while ( true ) {
                try {
                    String userText = userInput.readLine();
                    clientOutput.writeUTF( username + " - " + userText );
                    clientOutput.flush();
                } catch ( IOException ex ) {
                    ex.printStackTrace();
                }

                if ( !socket.isConnected() ) {
                    break;
                }
            }
        }
    }
}
