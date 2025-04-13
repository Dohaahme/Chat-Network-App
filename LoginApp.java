package com.example.demo;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.StackPane;

public class LoginApp extends Application {

    private static final String DATABASE_URL = "jdbc:ucanaccess://C://Users//Gaming 3//IdeaProjects//demo//target//user1.accdb";
    private static final String BACKGROUND_IMAGE = "src/main/resources/loog.jpg";
    private static final String NOTIFICATION_SOUND = "src/main/resources/notification_sound.mp3";
    private static final String OK_SOUND = "src/main/resources/ok.mp3";


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Login Page");

            // Load background image
            ImageView backgroundImageView = new ImageView(new Image(new FileInputStream(BACKGROUND_IMAGE)));
            backgroundImageView.fitWidthProperty().bind(primaryStage.widthProperty());
            backgroundImageView.fitHeightProperty().bind(primaryStage.heightProperty());
            StackPane root = new StackPane(backgroundImageView);

            VBox vbox = new VBox(20);
            vbox.setPadding(new Insets(100));
            vbox.setAlignment(Pos.CENTER);

            GridPane grid = new GridPane();
            grid.setVgap(15);
            grid.setHgap(15);
            grid.setAlignment(Pos.CENTER);

            Label loginLabel = new Label("Login");
            loginLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
            loginLabel.setTextFill(Color.WHITE);
            loginLabel.setPadding(new Insets(0,0,50,0));
            GridPane.setColumnSpan(loginLabel, 2);
            GridPane.setHalignment(loginLabel, HPos.CENTER);

            Label usernameLabel = new Label("Username:");
            usernameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            usernameLabel.setTextFill(Color.WHITE);

            TextField usernameField = new TextField();
            usernameField.setFont(Font.font("Arial", 15));
            usernameField.setPromptText("Enter your username");

            Label passwordLabel = new Label("Password:");
            passwordLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            passwordLabel.setTextFill(Color.WHITE);

            PasswordField passwordField = new PasswordField();
            passwordField.setFont(Font.font("Arial", 15));
            passwordField.setPromptText("Enter your password");
            passwordField.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(),primaryStage));

            grid.addRow(0, loginLabel);
            grid.addRow(1, usernameLabel, usernameField);
            grid.addRow(2, passwordLabel, passwordField);

            Button loginButton = new Button("Login");
            loginButton.setPadding(new Insets(10, 80, 10, 80));
            loginButton.setFont(Font.font("Arial", 15));
            loginButton.setTextFill(Color.WHITE);
            loginButton.setBackground(Background.fill(Paint.valueOf("orange")));
            loginButton.setDefaultButton(false);

            Button signUpButton = new Button("Sign Up");
            signUpButton.setPadding(new Insets(8, 74, 8, 74));
            signUpButton.setFont(Font.font(15));
            signUpButton.setDefaultButton(false);

            VBox buttonBox = new VBox(10, loginButton, signUpButton);
            buttonBox.setAlignment(Pos.CENTER);

            vbox.getChildren().addAll(grid, buttonBox);
            root.getChildren().add(vbox);

            Scene scene = new Scene(root, 800, 600);
            primaryStage.setScene(scene);
            primaryStage.show();

            loginButton.setOnAction(e -> handleLogin(usernameField.getText(), passwordField.getText(),primaryStage));
            signUpButton.setOnAction(e -> handleSignUp(primaryStage));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleLogin(String username, String password, Stage primaryStage) {
        if (authenticateUser(username, password)) {
            okSound();
            showAlert(AlertType.INFORMATION, "Login Successful", "Welcome, " + username + "!");

            showServerPage();
            showClientPage();
            showClient2Page();
            primaryStage.close();
        } else {
            playNotificationSound();
            showAlert(AlertType.ERROR, "Login Failed", "Invalid username or password. Please try again.");
        }
    }

    private void handleSignUp(Stage primaryStage) {
        try {
            new SignUpApp().start(new Stage());
            primaryStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            playNotificationSound();
            showAlert(AlertType.ERROR, "Error", "An error occurred while opening the sign-up page.");
        }
    }

    private boolean authenticateUser(String username, String password) {
        try (Connection con = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = createPreparedStatement(con, username, password);
             ResultSet rs = pstmt.executeQuery()) {

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            playNotificationSound();
            showAlert(AlertType.ERROR, "Database Error", "An error occurred while authenticating user. Please try again later.");
            return false;
        }
    }

    private PreparedStatement createPreparedStatement(Connection con, String username, String password) throws SQLException {
        String sql = "SELECT * FROM Users WHERE username = ? AND password = ?";
        PreparedStatement pstmt = con.prepareStatement(sql);
        pstmt.setString(1, username);
        pstmt.setString(2, password);
        return pstmt;
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void playNotificationSound() {
        Media sound = new Media(new File(NOTIFICATION_SOUND).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private void showServerPage() {
        try {
            new ServerPage().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            playNotificationSound();
            showAlert(AlertType.ERROR, "Error", "An error occurred while opening the server page.");
        }
    }
    private void okSound() {
        Media sound = new Media(new File(OK_SOUND).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
    }

    private void showClientPage() {
        try {
            new ClientApp().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            playNotificationSound();
            showAlert(AlertType.ERROR, "Error", "An error occurred while opening the client page.");
        }
    }
    private void showClient2Page() {
        try {
            new ClientApp2().start(new Stage());
        } catch (Exception e) {
            e.printStackTrace();
            playNotificationSound();
            showAlert(AlertType.ERROR, "Error", "An error occurred while opening the client page.");
        }
    }
}
