//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.example.demo;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;

public class SignUpApp extends Application {
    private static final String BACKGROUND_IMAGE = "src/main/resources/loog.jpg";
    private static final String NOTIFICATION_SOUND = "src/main/resources/notification_sound.mp3";
    private static final String OK_SOUND = "src/main/resources/ok.mp3";
    public SignUpApp() {
    }

    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Sign Up Page");
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

        Label signUpLabel = new Label("Sign Up");
        signUpLabel.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        signUpLabel.setTextFill(Color.WHITE);
        signUpLabel.setPadding(new Insets(0,0,50,0));
        GridPane.setColumnSpan(signUpLabel, 2);
        GridPane.setHalignment(signUpLabel, HPos.CENTER);

        Label nameLabel = new Label("Username:");
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        nameLabel.setTextFill(Color.WHITE);

        TextField nameInput = new TextField();
        nameInput.setFont(Font.font("Arial", 15));
        nameInput.setPromptText("Enter your username");


        Label passLabel = new Label("Password:");
        passLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        passLabel.setTextFill(Color.WHITE);

        PasswordField passInput = new PasswordField();
        passInput.setFont(Font.font("Arial", 15));
        passInput.setPromptText("Enter your password");


        Label confirmPassLabel = new Label("Confirm Password:");
        confirmPassLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        confirmPassLabel.setTextFill(Color.WHITE);

        PasswordField confirmPassInput = new PasswordField();
        confirmPassInput.setPromptText("Re-enter your password");
        confirmPassInput.setFont(Font.font("Arial", 15));


        Button signUpButton = new Button("Sign Up");
        signUpButton.setPadding(new Insets(10, 80, 10, 80));
        signUpButton.setFont(Font.font("Arial", 15));
        signUpButton.setTextFill(Color.WHITE);
        signUpButton.setBackground(Background.fill(Paint.valueOf("orange")));
        signUpButton.setDefaultButton(false);

        Button loginButton = new Button("Login");
        loginButton.setPadding(new Insets(8, 74, 8, 74));
        loginButton.setFont(Font.font(15));
        loginButton.setDefaultButton(false);


        grid.addRow(0, new Node[]{signUpLabel});
        grid.addRow(1, new Node[]{nameLabel, nameInput});
        grid.addRow(2, new Node[]{passLabel, passInput});
        grid.addRow(3, new Node[]{confirmPassLabel, confirmPassInput});
        grid.add(signUpButton, 1, 4);
        grid.add(loginButton, 0, 4);
        root.getChildren().add(grid);
        signUpButton.setOnAction((e) -> {
            String username = nameInput.getText();
            String password = passInput.getText();
            String confirmPassword = confirmPassInput.getText();
            if (!password.equals(confirmPassword)) {
                playNotificationSound();
                this.showAlert(AlertType.ERROR, "Password Mismatch", "Passwords do not match. Please try again.");
            } else if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$")) {
                playNotificationSound();
                this.showAlert(AlertType.ERROR, "Invalid Password", "Password must be at least 8 characters long and contain at least one lowercase, one uppercase, one digit, and one special character.");
            } else {
                this.insertUserData(username, password, primaryStage);
                okSound();
                this.showAlert(AlertType.INFORMATION, "Sign Up Successful", "You have successfully signed up!");
            }
        });
        loginButton.setOnAction((e) -> {
            LoginApp loginPage = new LoginApp();

            try {
                loginPage.start(new Stage());
                primaryStage.hide();
            } catch (Exception var4) {
                Exception ex = var4;
                ex.printStackTrace();
            }

        });
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText((String)null);
        alert.setContentText(content);
        alert.showAndWait();
    }



    private void insertUserData(String username, String password, Stage primaryStage) {
        String url = "jdbc:ucanaccess://C://Users//Gaming 3//IdeaProjects//demo//target//user1.accdb";

        try {
            Connection con = DriverManager.getConnection(url);
            String sql = "INSERT INTO Users (username, password) VALUES (?, ?)";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            pstmt.close();
            con.close();
            okSound();
            this.showAlert(AlertType.INFORMATION, "User Added", "New user added successfully.");
            LoginApp loginPage = new LoginApp();

            try {
                loginPage.start(new Stage());
                primaryStage.hide();
            } catch (Exception var10) {
                Exception ex = var10;
                ex.printStackTrace();
            }
        } catch (SQLException var11) {
            SQLException e = var11;
            e.printStackTrace();
            playNotificationSound();
            this.showAlert(AlertType.ERROR, "Database Error", "An error occurred while adding the user. Please try again later.");
        }

    }
    private void playNotificationSound() {
        Media sound = new Media(new File(NOTIFICATION_SOUND).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.play();
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
