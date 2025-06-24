package controller.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import model.*;

import java.util.*;
import java.util.stream.Collectors;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Controller for handling the photo search functionality.
 * <p>
 * Allows users to search for photos based on tags and date ranges,
 * and to save search results as a new album.
 * </p>
 */
public class SearchViewController {

    @FXML private TextField startDateField;
    @FXML private TextField endDateField;
    @FXML private TextField tag1Field;
    @FXML private TextField tag2Field;
    @FXML private ChoiceBox<String> operatorChoice;
    @FXML private ListView<Photo> resultsList;

    private User currentUser;
    private PhotoManager photoManager;
    private Album currentAlbum;

    /**
     * Initializes the controller with user context and current album.
     *
     * @param user the current user
     * @param album the album from which the search was initiated
     * @param manager the photo manager instance
     */
    public void init(User user, Album album, PhotoManager manager) {
        this.currentUser = user;
        this.currentAlbum = album;
        this.photoManager = manager;

        operatorChoice.getItems().addAll("None", "AND", "OR");
        operatorChoice.setValue("None");
    }

    /**
     * Handles navigation back to the photo view.
     */
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/photo_view.fxml"));
            Parent root = loader.load();

            PhotoViewController controller = loader.getController();
            controller.init(currentUser, currentAlbum, photoManager);

            Stage stage = (Stage) resultsList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Photo View");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves the current search results as a new album.
     */
    public void handleSaveAsAlbum() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Save Search Results");
        dialog.setHeaderText("Create a new album from search results");
        dialog.setContentText("Enter album name:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String albumName = result.get().trim();
            if (albumName.isEmpty()) {
                showAlert("Invalid Name", "Album name cannot be empty.");
                return;
            }

            if (currentUser.getAlbum(albumName) != null) {
                showAlert("Duplicate Album", "An album with that name already exists.");
                return;
            }

            Album newAlbum = new Album(albumName);
            for (Photo p : resultsList.getItems()) {
                newAlbum.addPhoto(p);
            }

            currentUser.addAlbum(newAlbum);
            photoManager.saveUsers();
            showAlert("Success", "Album \"" + albumName + "\" created.");
        }
    }

    /**
     * Executes a search based on user-specified tag and/or date criteria.
     * Displays matching photos in the result list.
     */
    @FXML
    public void handleSearch() {
        List<Photo> allPhotos = currentUser.getAlbums().stream()
            .flatMap(album -> album.getPhotos().stream())
            .distinct()
            .collect(Collectors.toList());

        List<Photo> results = new ArrayList<>(allPhotos);

        String tag1 = tag1Field.getText().trim();
        String tag2 = tag2Field.getText().trim();
        String op = operatorChoice.getValue();

        if (!tag1.isEmpty()) {
            String[] parts1 = tag1.split("=");
            if (parts1.length == 2) {
                results = results.stream()
                    .filter(p -> p.getTags().contains(new Tag(parts1[0].trim(), parts1[1].trim())))
                    .collect(Collectors.toList());
            }
        }

        if (!tag2.isEmpty() && !op.equals("None")) {
            String[] parts2 = tag2.split("=");
            if (parts2.length == 2) {
                List<Photo> tag2Matches = allPhotos.stream()
                    .filter(p -> p.getTags().contains(new Tag(parts2[0].trim(), parts2[1].trim())))
                    .collect(Collectors.toList());

                if (op.equals("AND")) {
                    results.retainAll(tag2Matches);
                } else if (op.equals("OR")) {
                    Set<Photo> union = new HashSet<>(results);
                    union.addAll(tag2Matches);
                    results = new ArrayList<>(union);
                }
            }
        }

        String startDateText = startDateField.getText().trim();
        String endDateText = endDateField.getText().trim();

        if (!startDateText.isEmpty() && !endDateText.isEmpty()) {
            try {
                LocalDate startDate = LocalDate.parse(startDateText);
                LocalDate endDate = LocalDate.parse(endDateText);

                results = results.stream()
                    .filter(photo -> {
                        LocalDate photoDate = photo.getDateAsLocalDate();
                        return (photoDate.isEqual(startDate) || photoDate.isAfter(startDate)) &&
                               (photoDate.isEqual(endDate) || photoDate.isBefore(endDate));
                    })
                    .collect(Collectors.toList());

            } catch (DateTimeParseException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Date");
                alert.setHeaderText("Please enter valid dates in yyyy-MM-dd format.");
                alert.showAndWait();
            }
        }

        resultsList.getItems().setAll(results);
    }

    /**
     * Displays an informational alert dialog.
     *
     * @param title the title of the alert
     * @param msg the message to display
     */
    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
