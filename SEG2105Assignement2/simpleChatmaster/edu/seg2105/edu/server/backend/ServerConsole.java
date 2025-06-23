package edu.seg2105.edu.server.backend;

import java.io.*;
import java.util.Scanner;

import edu.seg2105.edu.server.backend.EchoServer;
import edu.seg2105.client.common.ChatIF;

public class ServerConsole implements ChatIF {

    final public static int DEFAULT_PORT = 5555;
    private EchoServer server;
    private Scanner fromConsole;

    public ServerConsole(int port) {
        try {
            server = new EchoServer(port, this);
            server.listen();
        } catch (IOException e) {
            System.out.println("Error: Can't setup server! Terminating.");
            System.exit(1);
        }

        fromConsole = new Scanner(System.in);
    }

    public void accept() {
        while (true) {
            String message = fromConsole.nextLine();
            if (message.startsWith("#")) {
                handleCommand(message);
            } else {
                server.sendToAllClients("SERVER MSG> " + message);
                display("SERVER MSG> " + message);
            }
        }
    }

    public void display(String message) {
        System.out.println(message);
    }

    private void handleCommand(String command) {
        String[] tokens = command.split(" ");
        String cmd = tokens[0];

        switch (cmd) {
            case "#quit":
                try {
                    server.close();
                } catch (IOException e) {
                    System.out.println("Error closing server.");
                }
                System.exit(0);
                break;

            case "#stop":
                server.stopListening();
                display("Server has stopped listening for new clients.");
                break;

            case "#close":
                try {
                    server.close();
                    display("Server closed. All clients disconnected.");
                } catch (IOException e) {
                    System.out.println("Error closing server.");
                }
                break;

            case "#start":
                if (!server.isListening()) {
                    try {
                        server.listen();
                        display("Server started listening.");
                    } catch (IOException e) {
                        System.out.println("Error starting server.");
                    }
                } else {
                    display("Server is already listening.");
                }
                break;

            case "#setport":
                if (!server.isListening()) {
                    if (tokens.length > 1) {
                        try {
                            int newPort = Integer.parseInt(tokens[1]);
                            server.setPort(newPort);
                            display("Port set to: " + newPort);
                        } catch (NumberFormatException e) {
                            display("Invalid port number.");
                        }
                    } else {
                        display("Usage: #setport <port>");
                    }
                } else {
                    display("Cannot change port while server is listening.");
                }
                break;

            case "#getport":
                display("Current port: " + server.getPort());
                break;

            default:
                display("Unknown command: " + command);
        }
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid port number. Using default.");
            }
        }

        ServerConsole sc = new ServerConsole(port);
        sc.accept(); 
    }
}
