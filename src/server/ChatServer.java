package server;
import java.io.*;
import java.net.*;
import java.util.*;
/**
 * ChatServer class will set up a chat server using TCP sockets and has a 
 * separate thread for each user.  An instance of ChatServer store the port 
 * that the server is listening on and a hashed set of usernames and the thread
 * associated with each user
 */
public class ChatServer {
    private int port;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    
    /**
     * Constructor: All we need is a port in order to start the server 
     */
    public ChatServer(int port) {
        this.port = port;
    }
    
    /**
     * The execute method begins the server.  
     */
    public void execute() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            System.out.println("Chat Server is listening on port " + port);
 
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("User '" + socket.getInetAddress() 
                        + "' has connected.");
                        
                //Create our userthread, start it and add it to our hash set
                UserThread newUser = new UserThread(socket, this);
                userThreads.add(newUser);
                newUser.start(); 
            } 
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
 
    /**
     * Main: Take one command line argument for the port.  If not args are 
     * provided, then exit.  If a port is provided, create instance of 
     * ChatServer and start it
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Missing port to listen on." 
                + "\nTry java -r server.jar <PORT>");
            System.exit(0);
        }
        int port = Integer.parseInt(args[0]);
        ChatServer server = new ChatServer(port);
        server.execute();
    } 
    /**
     * broadcast method: Takes a string message and the User who wishes to send
     * the message as arguments.  Iterates through each connected user and 
     * sends the string message to each, excluding the sending user.
     */
    void broadcast(String message, UserThread excludeUser) {
        for (UserThread user : userThreads) {
            if (user != excludeUser) {
                user.sendMessage(message);
            }
        }
    } 
    /**
     * Adds username to the hash set of usernames
     */
    void addUserName(String userName) {
        userNames.add(userName);
    }
 
    /**
     * This is called when the userthread detects that the user has typed 'bye'.
     * Removes the user from the hashset of usernames and user threads
     */
    void removeUser(String userName, UserThread user) {
        boolean removed = userNames.remove(userName);
        if (removed) {
            userThreads.remove(user);
            System.out.println("The user " + userName + " quitted");
        }
    }
 
    /**
     * This returns a string that is a series of all user's usernames that are 
     * currently connected.  This is used to tell a joining user who else is 
     * already in the server.
     */
    Set<String> getUserNames() {
        return this.userNames;
    }
 
    /**
     * Returns true if there are already users on the server when connecting
     */
    boolean hasUsers() {
        return !this.userNames.isEmpty();
    }
}
