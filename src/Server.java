/**
 * Chat application.
 * Java.net.ServerSocket class is used by server applications to obtain a port and listen for client requests.
 * Java.net.Socket class represents the socket that both the client and the server use to communicate with each other.
 *
 */

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{

    private JTextField userText;
    private JTextArea chatWindow;
    // Users communicate through streams. Output and input.
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    // Server that waits for everyone to connect.
    private ServerSocket serverSocket;
    // In Java connections are sockets.
    private Socket socket;

    public Server() {
        super("Instant Messenger Application");
        userText = new JTextField();
        // By default you can't type anything in message box until you're connected to anyone.
        userText.setEditable(false);
        // When user types something in you want to send it.
        userText.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Pass in text typed into field.
                sendMessage(e.getActionCommand());
                // After message is sent make text box blank.
                userText.setText("");
            }
        });
        add(userText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        chatWindow.setEditable(false);
        add(new JScrollPane(chatWindow));
        setSize(300, 300);
        setVisible(true);
    }

    // Set up and start the server.
    public void startRunning() {
        try {
            // Make a new socket. Backlog is how many people can wait to access the server.
            serverSocket = new ServerSocket(6789, 100);
            while (true) {
                try {
                    // Wait for someone to connect.
                    waitForConnection();
                    // Set up input and output streams.
                    setUpStreams();
                    // Run program while chatting.
                    whileChatting();
                } catch (EOFException e) {
                    showMessage("\n Server ended the connection!");
                } finally {
                    closeEverything();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Wait for a connection and then display connection information.
    private void waitForConnection() throws IOException {
        showMessage("Waiting for someone to connect... \n");
        // Wait for connection and accept it when it occurs.
        socket = serverSocket.accept();
        showMessage("Now connected to " + socket.getInetAddress().getHostName());
    }

    // Get the stream to send and receive data.
    private void setUpStreams() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        // Flush out leftover data.
        outputStream.flush();
        // You can't flush out the input stream because that is not yours.
        inputStream = new ObjectInputStream(socket.getInputStream());
        showMessage("/n Streams are now set up! \n");
    }

    // Method that runs during the conversation.
    private void whileChatting() throws IOException {
        String message = "You are now connected!";
        showMessage(message);
        ableToType(true);
        // Have a conversation while both people want to.
        do {
            try {
                // Input stream is their message. Cast the object to a string.
                message = (String) inputStream.readObject();
                sendMessage("\n" + message);
            } catch (ClassNotFoundException e) {
                showMessage("\n The user sent some strange object we can't understand as a string.");
            }
            // If user sends END then the conversation ends.
        } while (!message.equals("CLIENT - END"));
    }

    // Display the messages on the main chat area.
    private void showMessage(final String message) {
        // Update parts of the GUI, only the chat window. Create a new thread to update the GUI.
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // Add message to the end of the document.
                chatWindow.append(message);
            }
        });
    }

    // The user can type into the chat box or not.
    private void ableToType(final Boolean canType) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // User text is where the user types in.
                userText.setEditable(canType);
            }
        });
    }

    // Close down the stream and the sockets.
    private void closeEverything() {
        showMessage("\n Closing connections...\n");
        ableToType(false);
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send a message to the client.
    private void sendMessage(String message) {
        try {
            outputStream.writeObject("SERVER - " + message);
            // If something happened and bytes are left over push out the extra stuff.
            outputStream.flush();
            showMessage("\nSERVER - " + message);
        } catch (IOException e) {
            chatWindow.append("\n ERROR: Message not sent.");
        }
    }

}
