package com.example.demo;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientApp2 extends Application {
    TextField inputField = new TextField();
    private PrintWriter out;
    private BufferedReader in;
    private static final String BACKGROUND_IMAGE = "src/main/resources/loog.jpg";
    private static final String OK_SOUND = "src/main/resources/ok.mp3";

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Client2");

        ImageView backgroundImageView = null;
        try {
            backgroundImageView = new ImageView(new Image(new FileInputStream(BACKGROUND_IMAGE)));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
        backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());
        StackPane rooot = new StackPane(backgroundImageView);

        // Create a VBox as the root node
        VBox root = new VBox();
        root.setSpacing(10);
        root.setPadding(new Insets(10));

        GridPane grid = new GridPane();
        grid.setVgap(15);
        grid.setHgap(15);
        grid.setAlignment(Pos.BASELINE_LEFT);
        GridPane grid2 = new GridPane();
        grid.setVgap(15);
        grid.setHgap(15);
        grid.setAlignment(Pos.BASELINE_LEFT);

        Label ChatLabel = new Label("Start Chat");
        ChatLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        ChatLabel.setTextFill(Color.WHITE);
        ChatLabel.setPadding(new Insets(0,0,0,150));
        GridPane.setColumnSpan(ChatLabel, 2);
        GridPane.setHalignment(ChatLabel, HPos.CENTER);

        // TextFlow to display messages
        TextFlow messageFlow = new TextFlow();
        messageFlow.setPrefHeight(200); // Set preferred height to match TextArea for overlay
        messageFlow.setStyle("-fx-background-color: white;"); // Match the background color

        ScrollPane scrollPane = new ScrollPane(messageFlow);
        scrollPane.setFitToWidth(true);


        // TextField to enter messages
        inputField.setPromptText("Enter a message...");
        inputField.setOnAction(e -> sendMessage(inputField.getText()));
        inputField.setPrefWidth(300);

        // Button to send messages
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        sendButton.setOnAction(e -> sendMessage(inputField.getText()));

        // Label to display connection status
        Label statusLabel = new Label("Not connected");


        grid.addRow(0, inputField, sendButton);
        grid.addRow(2, statusLabel);
        grid2.addRow(0,ChatLabel);
        root.getChildren().add(grid2);
        root.getChildren().add(scrollPane);
        root.getChildren().add(grid);
        rooot.getChildren().add(root);
        Scene scene = new Scene(rooot, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.setX(800); // X coordinate
        primaryStage.setY(250);
        primaryStage.show();

        // Connect to the server
        connectToServer(statusLabel, messageFlow);
    }

    private void connectToServer(Label statusLabel, TextFlow messageFlow) {
        try {
            Socket socket = new Socket("localhost", 12345); // Change localhost to server IP if needed
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            statusLabel.setText("Connected to server");
            statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            statusLabel.setTextFill(Color.WHITE);

            // Start a separate thread to receive messages from the server
            new Thread(() -> {
                try {
                    String message;
                    while ((message = in.readLine()) != null) {
                        String finalMessage = message; // Make message effectively final for use in lambda
                        javafx.application.Platform.runLater(() -> displayMessage(finalMessage, messageFlow));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void sendMessage(String message) {
        if (out != null) {
            out.println(message+"@");
        }
        inputField.clear(); // Clear the input field after sending the message
    }

    private void displayMessage(String message, TextFlow messageFlow) {
        // Format message with timestamp
        LocalDateTime now = LocalDateTime.now();
        String formattedMessage =  message;
        String time = "[" + now.format(DateTimeFormatter.ofPattern("hh:mm:ss a")) + "]  " ;

        // Remove client IP address from the message
        int index = formattedMessage.indexOf(": ");
        if (index != -1) {
            formattedMessage = formattedMessage.substring(index + 2); // Skip IP and ": "
            formattedMessage =  formattedMessage;
        }

        // Determine the color based on the last character
        String cleanMessage = formattedMessage;
        Color textColor = Color.BLACK;
        if (message.contains("@")) {
            textColor = Color.BLACK;
            cleanMessage = formattedMessage.replace("@", ""); // Remove "@"
        } else if (message.contains("#")) {
            textColor = Color.ORANGE;
            okSound();
            cleanMessage = formattedMessage.replace("#", ""); // Remove "#"
        }
        Text texttime = new Text(time);
        texttime.setFill(Color.BLUE);
        messageFlow.getChildren().add(texttime);
        // Create the text node with the cleaned message and appropriate color
        Text textNode = new Text(cleanMessage + "\n");
        textNode.setFill(textColor);

        // Add the formatted message container to the message flow
        messageFlow.getChildren().add(textNode);

        // Scroll to the bottom
        ScrollPane scrollPane = (ScrollPane) messageFlow.getParent();
        scrollPane.layout();
        scrollPane.setVvalue(1.0);
    }
    private void okSound() {
        Media sound = new Media(new File(OK_SOUND).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
