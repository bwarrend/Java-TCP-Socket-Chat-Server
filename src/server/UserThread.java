package server;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * UserThread class is a thread that is created for each user who connects to 
 * the server.  Each UserThread will contain a Print Writer and references to 
 * a ChatServer and a socket.
 */
public class UserThread extends Thread {
    private Socket socket;
    private ChatServer server;
    private PrintWriter writer; 
    /**
     * Constructor: Take Socket and ChatServer and set the class variables 
     */
    public UserThread(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }    
    /**
     * Run:  When start() is called on the thread. Set up the components to 
     * receive messages from the user and then run until user says bye
     */
    @Override
    public void run() {
        try {
            //Set up a buffered reader and a print writer for the userthread
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input)); 
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            //Send a message to the connecting user about who is already here            
            printUsers();
            //Receive username from the user
            String userName = reader.readLine();
            server.addUserName(userName);
            //Tell all other users that a new user has connected
            String serverMessage = "New user connected: " + userName;
            server.broadcast(serverMessage, this);
            
            String clientMessage;
            /**
             * Receive messages from the client until we receive a 'bye'
             */
            do {
                /**
                 * Get message, append user to front, then broadcast to all 
                 * users 
                 */
                clientMessage = reader.readLine();
                serverMessage = userName + ": " + clientMessage;
                server.broadcast(serverMessage, this);
 
            } while (!clientMessage.equals("bye"));            
            /**
             * Once 'bye' is sent, remove userName and UserThread from the hash
             * set.  Tell all users except obviously the current one that this 
             * user has left the server. Close the socket.
             */
            server.removeUser(userName, this);
            socket.close(); 
            serverMessage = userName + " has left the server.";
            server.broadcast(serverMessage, this);
 
        } catch (IOException ex) {
            System.out.println("Error in UserThread: " + ex.getMessage());
            ex.printStackTrace();
        }
    } 
    /**
     * Sends a series of all connected User's user names to a newly connecting
     * user.
     */
    void printUsers() {
        if (server.hasUsers()) {
            writer.println("Currently connected: " + server.getUserNames());
        } else {
            writer.println("No users currently connected.");
        }
    } 
    /**
     * Sends string message to the user of this userthread
     */
    void sendMessage(String message) {
        writer.println(message);
    }
}
