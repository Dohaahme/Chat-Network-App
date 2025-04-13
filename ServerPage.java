package com.example.demo;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerPage extends Application {

    private TextArea messageArea;
    private List<PrintWriter> clientWriters = new ArrayList<>(); // List to store PrintWriter for each client

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Server Page");

        // Create a VBox as the root node
        VBox root = new VBox();
        root.setSpacing(20);
        root.setPadding(new Insets(50));

        // Label to display server status
        Label statusLabel = new Label("Server is running...");
        root.getChildren().add(statusLabel);

        // TextArea to display messages from clients
        messageArea = new TextArea();
        messageArea.setPrefHeight(200);
        messageArea.setEditable(false);
        root.getChildren().add(messageArea);

        // Create the scene and set it to the stage
        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setX(475); // X coordinate
        primaryStage.setY(10);
        primaryStage.show();

        // Start the server socket in a separate thread
        new Thread(() -> startServer(statusLabel)).start();
    }

    private void startServer(Label statusLabel) {
        try {
            // Create a server socket
            ServerSocket serverSocket = new ServerSocket(12345);

            while (true) {
                // Wait for client connections
                Socket clientSocket = serverSocket.accept();
                statusLabel.setText("Client connected: " + clientSocket.getInetAddress());

                // Setup input and output streams
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                // Store the PrintWriter associated with this client
                clientWriters.add(out);

                // Handle client connection in a separate thread
                new Thread(() -> handleClient(clientSocket, in)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void handleClient(Socket clientSocket, BufferedReader in) {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                // Handle client messages here
                System.out.println("Received from client: " + message);

                // Update GUI with received message
                updateMessageArea("Received from client: " + message);

                // Broadcast the received message to all clients
                broadcastMessage("Client " + clientSocket.getInetAddress() + " says: " + message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Remove the PrintWriter associated with this client when it disconnects
            clientWriters.removeIf(out -> out.equals(clientSocket));
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to update the TextArea with received messages
    private void updateMessageArea(String message) {
        messageArea.appendText(message + "\n");
        messageArea.setStyle("-fx-text-fill: orange; -fx-font-weight: bold; -fx-font-size: 10pt;");

    }

    // Method to broadcast a message to all clients
    private void broadcastMessage(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}