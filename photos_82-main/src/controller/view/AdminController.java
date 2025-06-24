package controller.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.PhotoManager;
import model.User;

/**
 * Controller class for the Admin view.
 * <p>
 * This class allows the admin to manage users within the application, including
 * listing users, creating new users, deleting existing users, and logging out.
 * </p>
 */
public class AdminController {

    /**
     * The ListView UI element displaying the list of usernames.
     */
    @FXML private ListView<String> userList;

    /**
     * Reference to the shared {@link PhotoManager} instance.
     */
    private PhotoManager photoManager;

    /**
     * Initializes the AdminController with the provided {@link PhotoManager}.
     * This method should be called after loading the FXML.
     *
     * @param photoManager the PhotoManager managing application data.
     */
    public void init(PhotoManager photoManager) {
        this.photoManager = photoManager;
        refreshUserList();
    }

    /**
     * Refreshes the displayed list of users, excluding the admin user.
     */
    private void refreshUserList() {
        userList.getItems().setAll(
            photoManager.getAllUsers().stream()
                .map(User::getUsername)
                .filter(name -> !name.equalsIgnoreCase("admin"))
                .toList()
        );
    }

    /**
     * Prompts the admin to enter a new username and creates a new user
     * if the username is valid and not already taken.
     */
    public void createUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter new username:");
        dialog.showAndWait().ifPresent(userName -> {
            if (photoManager.getUser(userName) == null && !userName.isBlank()) {
                photoManager.addUser(new User(userName, null));
                refreshUserList();
            }
        });
    }

    /**
     * Deletes the selected user from the list, excluding the admin.
     */
    public void deleteUser() {
        String user = userList.getSelectionModel().getSelectedItem();
        if (user != null && !user.equalsIgnoreCase("admin")) {
            photoManager.removeUser(user);
            refreshUserList();
        }
    }

    /**
     * Logs the admin out by saving user data and returning to the login view.
     */
    @FXML
    public void logout() {
        try {
            photoManager.saveUsers();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_view.fxml"));
            Stage stage = (Stage) userList.getScene().getWindow();
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Photo Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
