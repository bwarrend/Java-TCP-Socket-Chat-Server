import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
/**
 * ChatServer class will set up a chat server using TCP sockets and has a 
 * separate thread for each user.  An instance of ChatServer store the port 
 * that the server is listening on and a hashed set of usernames and the thread
 * associated with each user
 */
public class ChatServer extends Thread{
    private int port;
    private JTextArea jTextArea1;
    private Set<String> userNames = new HashSet<>();
    private Set<UserThread> userThreads = new HashSet<>();
    
    /**
     * Constructor: All we need is a port in order to start the server 
     */
    public ChatServer(int port, JTextArea jTextArea1) {
        this.port = port;
        this.jTextArea1 = jTextArea1;
    }
    
    /**
     * The execute method begins the server.  
     */
    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
 
            jTextArea1.setText("Chat Server is listening on port " + port);
 
            while (true) {
                Socket socket = serverSocket.accept();
                jTextArea1.setText(jTextArea1.getText() + "\nUser '" 
                        + socket.getInetAddress() + "' has connected.");                        
                //Create our userthread, start it and add it to our hash set
                UserThread newUser = new UserThread(socket, this, jTextArea1);
                userThreads.add(newUser);
                newUser.start(); 
            } 
        } catch (IOException ex) {
            jTextArea1.setText(jTextArea1.getText() + "Error: " 
                    + ex.getMessage());
            ex.printStackTrace();
        }
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
            jTextArea1.setText(jTextArea1.getText() + "\nUser " + userName 
                    + " has left the server.");
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
