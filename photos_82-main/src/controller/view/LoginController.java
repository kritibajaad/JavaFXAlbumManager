package controller.view;

import java.io.IOException;

import app.Photos;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import model.*;

/**
 * Controller class for handling user login in the Photo App.
 * <p>
 * This class determines whether the user is an admin or a regular user,
 * then redirects to the appropriate subsystem view. If the user is new,
 * they are automatically created and added to the system.
 * </p>
 */
public class LoginController {

    /**
     * Text field where the user enters their username.
     */
    @FXML private TextField usernames;

    /**
     * Reference to the application's {@link PhotoManager} instance.
     */
    PhotoManager photoManager = Photos.getPhotoManager();

    /**
     * Handles login when the user clicks the login button or presses enter.
     * <ul>
     *     <li>If the username is "admin", loads the admin subsystem.</li>
     *     <li>If the user doesn't exist, creates a new user.</li>
     *     <li>Otherwise, loads the album view for the existing user.</li>
     * </ul>
     */
    public void login() {
        String username = usernames.getText().trim();

        // Ignore blank input
        if (username.isEmpty()) return;

        // Admin login
        if (username.equalsIgnoreCase("admin")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/admin_view.fxml"));
                Parent parent = loader.load();

                AdminController controller = loader.getController();
                controller.init(photoManager);

                Stage stage = (Stage) usernames.getScene().getWindow();
                stage.setScene(new Scene(parent));
                stage.setTitle("Admin Subsystem");
                stage.show();
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Regular user login
        User user = photoManager.getUser(username);
        if (user == null) {
            user = new User(username, null); // Create new user with no password
            photoManager.addUser(user);
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/album_view.fxml"));
            Scene scene = new Scene(loader.load());

            AlbumViewController controller = loader.getController();
            controller.init(user, photoManager);

            Stage stage = (Stage) usernames.getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
