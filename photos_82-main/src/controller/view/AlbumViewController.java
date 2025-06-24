package controller.view;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import model.*;

/**
 * Controller class for managing the album view in the Photo App.
 * <p>
 * This class allows a user to view, create, rename, delete, and open photo albums.
 * It also handles navigation back to the login screen.
 * </p>
 */
public class AlbumViewController {

    /**
     * ListView that displays all albums associated with the current user.
     */
    @FXML private ListView<Album> albumList;

    /**
     * The currently logged-in user.
     */
    private User currUser;

    /**
     * The shared {@link PhotoManager} instance for accessing and modifying user data.
     */
    private PhotoManager photoManager;

    /**
     * Initializes the controller with the current user and photo manager.
     *
     * @param currUser the currently logged-in user
     * @param photoManager the PhotoManager instance managing user and album data
     */
    public void init(User currUser, PhotoManager photoManager) {
        this.currUser = currUser;
        this.photoManager = photoManager;
        albumList.getItems().setAll(currUser.getAlbums());
    }

    /**
     * Prompts the user to enter a new album name and adds it to their collection.
     * Saves changes and refreshes the album list upon success.
     */
    public void createAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Enter album name:");
        dialog.showAndWait().ifPresent(albumName -> {
            if (currUser.addAlbum(new Album(albumName))) {
                photoManager.saveUsers();
                albumList.getItems().setAll(currUser.getAlbums());
            }
        });
    }

    /**
     * Deletes the selected album after showing a confirmation prompt.
     * Does not allow deletion if no album is selected.
     */
    @FXML
    public void deleteAlbum() {
        Album selAlbum = albumList.getSelectionModel().getSelectedItem();
        if (selAlbum == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle("No Album Selected");
            warning.setHeaderText("Please select an album to delete.");
            warning.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Delete Album");
        confirmation.setHeaderText("Are you sure you want to delete the album \"" + selAlbum.getName() + "\"?");
        confirmation.setContentText("This cannot be undone.");
        confirmation.showAndWait().ifPresent(resp -> {
            if (resp == ButtonType.OK) {
                currUser.removeAlbum(selAlbum.getName());
                photoManager.saveUsers();
                refreshAlbumList();
            }
        });
    }

    /**
     * Refreshes the ListView to reflect the current user's album list.
     */
    private void refreshAlbumList() {
        albumList.getItems().setAll(currUser.getAlbums());
    }

    /**
     * Prompts the user to rename the selected album.
     * Updates the album name and refreshes the album list.
     */
    public void renameAlbum() {
        Album selAlbum = albumList.getSelectionModel().getSelectedItem();
        if (selAlbum != null) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Enter new name:");
            dialog.showAndWait().ifPresent(newAlbum -> {
                if (currUser.renameAlbum(selAlbum.getName(), newAlbum)) {
                    photoManager.saveUsers();
                    albumList.getItems().setAll(currUser.getAlbums());
                }
            });
        }
    }

    /**
     * Opens the selected album and transitions to the photo view screen.
     * Shows a warning if no album is selected.
     */
    @FXML
    public void openAlbum() {
        Album selAlbum = albumList.getSelectionModel().getSelectedItem();
        if (selAlbum == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setHeaderText("Please select an album to open.");
            warning.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photo_view.fxml"));
            Parent parent = loader.load();

            PhotoViewController controller = loader.getController();
            controller.init(currUser, selAlbum, photoManager);

            Stage stage = (Stage) albumList.getScene().getWindow();
            stage.setScene(new Scene(parent));
            stage.setTitle("Album: " + selAlbum.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs out the current user, saves user data, and navigates back to the login view.
     */
    @FXML
    public void logout() {
        try {
            photoManager.saveUsers();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_view.fxml"));
            Parent parent = loader.load();

            Stage stage = (Stage) albumList.getScene().getWindow();
            stage.setScene(new Scene(parent));
            stage.setTitle("Photo Login");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
