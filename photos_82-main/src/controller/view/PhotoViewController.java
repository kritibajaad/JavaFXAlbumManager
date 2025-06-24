package controller.view;

import model.Tag;
import model.User;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import model.Album;
import model.Photo;
import model.PhotoManager;
import javafx.scene.image.ImageView;

/**
 * Controller class for managing photos within an album in the Photo App.
 * <p>
 * Provides functionality to view, add, remove, move, and tag photos,
 * as well as edit captions, view details, and launch slideshows.
 * </p>
 */
public class PhotoViewController {

    /** ListView displaying all photos in the current album. */
    @FXML private ListView<Photo> photoList;

    /** The PhotoManager managing users and persistent data. */
    private PhotoManager photoManager;

    /** The currently logged-in user. */
    private User currentUser;

    /** The album currently being viewed. */
    private Album currentAlbum;

    /** Default message used when no photo is selected. */
    private String noPhoto = "No Photo Selected";

    /**
     * Initializes the controller with the current user, album, and photo manager.
     * 
     * @param currentUser the user logged in
     * @param currentAlbum the album being viewed
     * @param photoManager the photo manager handling persistence
     */
    public void init(User currentUser, Album currentAlbum, PhotoManager photoManager) {
        this.currentUser = currentUser;
        this.currentAlbum = currentAlbum;
        this.photoManager = photoManager;

        refreshPhotoList();

        photoList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Photo photo = photoList.getSelectionModel().getSelectedItem();
                if (photo != null) {
                    showPhotoDetails(photo);
                }
            }
        });
    }

    /**
     * Displays a dialog with detailed information about the selected photo,
     * including image preview, caption, date, and tags.
     *
     * @param photo the photo to display details for
     */
    private void showPhotoDetails(Photo photo) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Photo Details");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));

        ImageView imageView = new ImageView(new javafx.scene.image.Image("file:" + photo.getFilePath()));
        imageView.setFitWidth(300);
        imageView.setPreserveRatio(true);

        Label caption = new Label("Caption: " + (photo.getCaption() != null ? photo.getCaption() : "(none)"));
        Label date = new Label("Date: " + photo.getFormattedDate());

        String tagText = photo.getTags().isEmpty()
                ? "(no tags)"
                : photo.getTags().stream().map(Tag::toString).reduce("", (a, b) -> a + "\n• " + b);
        Label tags = new Label("Tags:\n" + tagText);

        vbox.getChildren().addAll(imageView, caption, date, tags);
        dialog.getDialogPane().setContent(vbox);
        dialog.showAndWait();
    }

    /**
     * Refreshes the photo list to reflect the current album.
     */
    private void refreshPhotoList() {
        photoList.getItems().setAll(currentAlbum.getPhotos());
    }

    /**
     * Opens a file chooser to let the user add a new photo to the album.
     * Prevents adding duplicate photo paths.
     */
    public void addPhoto() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Photo to Add");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.bmp")
        );

        File file = fileChooser.showOpenDialog(photoList.getScene().getWindow());

        if (file != null) {
            String path = file.getAbsolutePath();
            for (Photo photo : currentAlbum.getPhotos()) {
                if (photo.getFilePath().equals(path)) {
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setHeaderText("Duplicate Photo");
                    warning.setContentText("This photo already exists in the album.");
                    warning.showAndWait();
                    return;
                }
            }

            try {
                Photo newPhoto = new Photo(path);
                currentAlbum.addPhoto(newPhoto);
                photoManager.saveUsers();
                refreshPhotoList();
            } catch (IllegalArgumentException e) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setHeaderText("Invalid Photo");
                error.setContentText("The selected file could not be loaded.");
                error.showAndWait();
            }
        }
    }

    /**
     * Removes the selected photo from the album after confirmation.
     */
    @FXML
    public void removePhoto() {
        Photo photo = photoList.getSelectionModel().getSelectedItem();

        if (photo == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle(noPhoto);
            warning.setHeaderText("Please select a photo to remove.");
            warning.showAndWait();
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Remove Photo");
        confirmation.setHeaderText("Are you sure you want to remove this photo?");
        confirmation.setContentText(photo.toString());

        confirmation.showAndWait().ifPresent(res -> {
            if (res == ButtonType.OK) {
                currentAlbum.removePhoto(photo);
                photoManager.saveUsers();
                refreshPhotoList();
            }
        });
    }

    /**
     * Opens a dialog to allow the user to edit the caption of the selected photo.
     */
    @FXML
    public void editCaption() {
        Photo photo = photoList.getSelectionModel().getSelectedItem();

        if (photo == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setTitle(noPhoto);
            warning.setHeaderText("Please select a photo to edit.");
            warning.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog(photo.getCaption());
        dialog.setTitle("Edit Caption");
        dialog.setHeaderText("Update the caption for this photo:");
        dialog.setContentText("New Caption:");

        dialog.showAndWait().ifPresent(caption -> {
            photo.setCaption(caption.trim());
            photoManager.saveUsers();
            refreshPhotoList();
        });
    }

    /**
     * Prompts the user to add a new tag to the selected photo.
     * Prevents duplicate tags.
     */
    @FXML
    public void addTag() {
        Photo photo = photoList.getSelectionModel().getSelectedItem();

        if (photo == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setHeaderText(noPhoto);
            warning.setContentText("Please select a photo to add a tag.");
            warning.showAndWait();
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Tag");
        dialog.setHeaderText("Enter tag type (e.g., person, location):");
        dialog.setContentText("Tag Type:");

        dialog.showAndWait().ifPresent(type -> {
            if (type.isBlank()) return;

            TextInputDialog dialog2 = new TextInputDialog();
            dialog2.setTitle("Add Tag");
            dialog2.setHeaderText("Enter value for tag type '" + type + "':");
            dialog2.setContentText("Tag Value:");

            dialog2.showAndWait().ifPresent(value -> {
                boolean added = photo.addTag(type.trim(), value.trim());
                if (!added) {
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setHeaderText("Duplicate Tag");
                    warning.setContentText("This tag already exists.");
                    warning.showAndWait();
                } else {
                    photoManager.saveUsers();
                    refreshPhotoList();
                }
            });
        });
    }

    /**
     * Prompts the user to select a tag to remove from the selected photo.
     */
    @FXML
    public void removeTag() {
        Photo photo = photoList.getSelectionModel().getSelectedItem();

        if (photo == null) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setHeaderText(noPhoto);
            warning.setContentText("Please select a photo to remove a tag.");
            warning.showAndWait();
            return;
        }

        Set<Tag> tags = photo.getTags();
        if (tags.isEmpty()) {
            Alert information = new Alert(Alert.AlertType.INFORMATION);
            information.setHeaderText("No Tags");
            information.setContentText("This photo has no tags to remove.");
            information.showAndWait();
            return;
        }

        ChoiceDialog<Tag> dialog = new ChoiceDialog<>(tags.iterator().next(), tags);
        dialog.setTitle("Remove Tag");
        dialog.setHeaderText("Select a tag to remove:");
        dialog.setContentText("Tags:");

        dialog.showAndWait().ifPresent(tag -> {
            photo.removeTag(tag.getName(), tag.getValue());
            photoManager.saveUsers();
            refreshPhotoList();
        });
    }

    /**
     * Allows the user to copy the selected photo into another album.
     * Prevents copying if the photo already exists in the target album.
     */
    @FXML
    public void copyPhoto() {
        Photo photo = photoList.getSelectionModel().getSelectedItem();

        if (photo == null) {
            showAlert(noPhoto, "Select a photo to copy.");
            return;
        }

        List<String> albumNames = photoManager.getUser(currentUser.getUsername())
                                              .getAlbums()
                                              .stream()
                                              .map(Album::getName)
                                              .filter(name -> !name.equals(currentAlbum.getName()))
                                              .toList();

        if (albumNames.isEmpty()) {
            showAlert("No Other Albums", "There are no other albums to copy to.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(albumNames.get(0), albumNames);
        dialog.setTitle("Copy Photo");
        dialog.setHeaderText("Choose album to copy photo into:");

        dialog.showAndWait().ifPresent(albumName -> {
            Album targetAlbum = photoManager.getUser(currentUser.getUsername()).getAlbum(albumName);
            if (targetAlbum.containsPhoto(photo)) {
                showAlert("Already Exists", "Photo already exists in selected album.");
            } else {
                targetAlbum.addPhoto(photo);
                showAlert("Photo Copied", "Photo successfully copied to \"" + albumName + "\".");
            }
        });
    }

    /**
     * Navigates to the photo search screen.
     */
    @FXML
    public void search() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/search_view.fxml"));
            Parent parent = loader.load();

            SearchViewController controller = loader.getController();
            controller.init(currentUser, currentAlbum, photoManager);

            Stage stage = (Stage) photoList.getScene().getWindow();
            stage.setScene(new Scene(parent));
            stage.setTitle("Search Photos");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Moves the selected photo to a different album.
     */
    @FXML
    public void movePhoto() {
        Photo photo = photoList.getSelectionModel().getSelectedItem();

        if (photo == null) {
            showAlert("No Photo Selected", "Select a photo to move.");
            return;
        }

        List<String> albumNames = photoManager.getUser(currentUser.getUsername())
                                              .getAlbums()
                                              .stream()
                                              .map(Album::getName)
                                              .filter(name -> !name.equals(currentAlbum.getName()))
                                              .toList();

        if (albumNames.isEmpty()) {
            showAlert("No Other Albums", "There are no other albums to move to.");
            return;
        }

        ChoiceDialog<String> dialog = new ChoiceDialog<>(albumNames.get(0), albumNames);
        dialog.setTitle("Move Photo");
        dialog.setHeaderText("Choose album to move photo into:");

        dialog.showAndWait().ifPresent(albumName -> {
            Album targetAlbum = photoManager.getUser(currentUser.getUsername()).getAlbum(albumName);

            if (targetAlbum.containsPhoto(photo)) {
                showAlert("Already Exists", "Photo already exists in selected album.");
            } else {
                targetAlbum.addPhoto(photo);
                currentAlbum.removePhoto(photo);
                refreshPhotoList();
                showAlert("Photo Moved", "Photo successfully moved to \"" + albumName + "\".");
            }
        });
    }

    /**
     * Displays an informational alert with the given header and content.
     *
     * @param header the alert header
     * @param content the alert message
     */
    private void showAlert(String header, String content) {
        Alert information = new Alert(Alert.AlertType.INFORMATION);
        information.setHeaderText(header);
        information.setContentText(content);
        information.showAndWait();
    }

    /**
     * Returns to the album view screen for the current user.
     */
    @FXML
    public void back() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/album_view.fxml"));
            Parent parent = loader.load();

            AlbumViewController controller = loader.getController();
            controller.init(currentUser, photoManager);

            Stage stage = (Stage) photoList.getScene().getWindow();
            stage.setScene(new Scene(parent));
            stage.setTitle("Albums - " + currentUser.getUsername());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Launches a slideshow view for the current album.
     */
    @FXML
    public void slideshow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/slideshow_view.fxml"));
            Parent parent = loader.load();

            SlideShowController controller = loader.getController();
            controller.init(currentAlbum.getPhotos());

            Stage stage = new Stage();
            stage.setTitle("Slideshow - " + currentAlbum.getName());
            stage.setScene(new Scene(parent));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logs the user out, saves all data, and returns to the login screen.
     */
    @FXML
    public void logout() {
        try {
            photoManager.saveUsers();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login_view.fxml"));
            Parent parent = loader.load();

            Stage stage = (Stage) photoList.getScene().getWindow();
            stage.setScene(new Scene(parent));
            stage.setTitle("Photo Login");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
