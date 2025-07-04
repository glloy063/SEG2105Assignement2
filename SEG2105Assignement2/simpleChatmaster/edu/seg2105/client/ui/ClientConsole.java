package edu.seg2105.client.ui;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;
import java.util.Scanner;

import edu.seg2105.client.backend.ChatClient;
import edu.seg2105.client.common.*;

/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole 
 *
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Dr Timothy C. Lethbridge  
 * @author Dr Robert Lagani&egrave;re
 */
public class ClientConsole implements ChatIF 
{
  //Class variables *************************************************
  
  /**
   * The default port to connect on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  ChatClient client;
  
  
  
  /**
   * Scanner to read from the console
   */
  Scanner fromConsole; 

  
  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientConsole(String loginID,String host, int port) 
  {
    try 
    {
      client= new ChatClient(loginID, host, port, this);
      
      
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"
                + " Terminating client.");
      System.exit(1);
    }
    
    // Create scanner object to read from console
    fromConsole = new Scanner(System.in); 
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept() 
  {
    try
    {

      String message;

      while (true) 
      {
        message = fromConsole.nextLine();
        client.handleMessageFromClientUI(message);
      }
    } 
    catch (Exception ex) 
    {
      System.out.println
        ("Unexpected error while reading from console!");
    }
  }
// Creating method to read commands starting with a #

  public void handleMessageFromConsole(String message) {
      if (message.startsWith("#")) {
          handleCommand(message);
      } else {
         try {
     client.handleMessageFromClientUI(message);
      } catch (Exception e) {
      System.out.println("Unexpected error. Terminating client.");
      client.quit();
}

      }
  }

  private void handleCommand(String command) {
    String[] tokens = command.split(" ");
    String cmd = tokens[0];

    switch (cmd) {
        case "#quit":
            client.quit();
            break;

        case "#logoff":
          try {
            client.closeConnection();
          } catch (IOException e) {
            System.out.println("Error disconnecting from server: " + e.getMessage());
          }
    break;

        case "#sethost":
            if (!client.isConnected()) {
                if (tokens.length > 1) {
                    client.setHost(tokens[1]);
                    System.out.println("Host set to: " + tokens[1]);
                } else {
                    System.out.println("Usage: #sethost <host>");
                }
            } else {
                System.out.println("Cannot set host while connected.");
            }
            break;

        case "#setport":
            if (!client.isConnected()) {
                if (tokens.length > 1) {
                    try {
                        int port = Integer.parseInt(tokens[1]);
                        client.setPort(port);
                        System.out.println("Port set to: " + port);
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid port number.");
                    }
                } else {
                    System.out.println("Usage: #setport <port>");
                }
            } else {
                System.out.println("Cannot set port while connected.");
            }
            break;

        case "#login":
            if (!client.isConnected()) {
                try {
                    client.openConnection();
                } catch (IOException e) {
                    System.out.println("Failed to connect to server.");
                }
            } else {
                System.out.println("Already connected.");
            }
            break;

        case "#gethost":
            System.out.println("Current host: " + client.getHost());
            break;

        case "#getport":
            System.out.println("Current port: " + client.getPort());
            break;

        default:
            System.out.println("Unknown command: " + command);
    }
}



  /**
   * This method overrides the method in the ChatIF interface.  It
   * displays a message onto the screen.
   *
   * @param message The string to be displayed.
   */
  public void display(String message) 
  {
    System.out.println("> " + message);
  }

  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of the Client UI.
   *
   * @param args[0] The host to connect to.
   */
  public static void main(String[] args) 
  {
    String loginId = null;
    String host = "localhost";
    int port = DEFAULT_PORT;


    try {
        loginId = args[0]; 
    } catch (Exception e) {
        System.out.println("Usage: java ClientConsole <loginId> [host] [port]");
        System.exit(1);
    }

    if (loginId == null || loginId.trim().isEmpty()) {
        System.out.println("ERROR: No login ID specified. Connection ended.");
        System.exit(1);
    }
    ClientConsole chat= new ClientConsole(loginId, host, port);
    chat.accept();  //Wait for console data
  }
}
//End of ConsoleChat class
