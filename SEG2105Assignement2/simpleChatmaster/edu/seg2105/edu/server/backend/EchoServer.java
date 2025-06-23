package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.IOException;

import edu.seg2105.client.common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  ChatIF serverUI;

  public EchoServer(int port, ChatIF serverUI) {
    super(port);
    this.serverUI = serverUI;
}





  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
public void handleMessageFromClient(Object msg, ConnectionToClient client) {
    String message = msg.toString();

    // Check if client is already logged in
    boolean isLoggedIn = client.getInfo("loginId") != null;

    if (message.startsWith("#login")) {
        if (isLoggedIn) {
            // Client already logged in — reject
            try {
                client.sendToClient("ERROR: You are already logged in.");
                client.close(); // terminate connection
            } catch (IOException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        } else {
            // First login message — process it
            String[] parts = message.split(" ", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                try {
                    client.sendToClient("ERROR: Invalid login command. Usage: #login <loginId>");
                    client.close();
                } catch (IOException e) {
                    System.out.println("Error closing connection: " + e.getMessage());
                }
            } else {
                String loginId = parts[1].trim();
                client.setInfo("loginId", loginId); // Save login ID
                System.out.println("Client logged in as: " + loginId);
            }
        }
        return; // Don't echo the #login command
    }

    // If the client is NOT logged in, reject any message
    if (!isLoggedIn) {
        try {
            client.sendToClient("ERROR: You must login first.");
            client.close();
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
        return;
    }
    String loginId = client.getInfo("loginId").toString();
    String prefixedMessage = loginId + ": " + message;
    System.out.println("Message received: " + prefixedMessage);
    this.sendToAllClients(prefixedMessage);
}
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  // Client connected and disconected methods will be overwritten here
  	protected void clientConnected(ConnectionToClient client) {
      System.out.println("Client connected: " + client.getInetAddress().getHostAddress());
    }

	synchronized protected void clientDisconnected(ConnectionToClient client) {
    System.out.println("Client disconnected: " + client.getInetAddress().getHostAddress());
  }

  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
