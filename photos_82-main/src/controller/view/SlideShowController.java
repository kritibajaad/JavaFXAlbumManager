package controller.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.stage.Stage;
import model.*;

import java.io.File;
import java.util.List;

/**
 * Controller for managing a slideshow view of photos.
 * <p>
 * Allows users to cycle through photos using next and previous controls,
 * view captions, and close the slideshow window.
 * </p>
 */
public class SlideShowController {

    /** The ImageView component displaying the photo. */
    @FXML private ImageView slideshowImage;

    /** The label displaying the photo caption or error messages. */
    @FXML private Label label;

    /** List of photos included in the slideshow. */
    private List<Photo> photos;

    /** Index of the currently displayed photo. */
    private int currIndex = 0;

    /**
     * Initializes the slideshow with a list of photos and displays the first photo.
     *
     * @param photos the list of photos to include in the slideshow
     */
    public void init(List<Photo> photos) {
        this.photos = photos;
        if (photos != null && !photos.isEmpty()) {
            showPhoto(currIndex);
        }
    }

    /**
     * Displays the photo at the specified index.
     *
     * @param index the index of the photo to show
     */
    private void showPhoto(int index) {
        try {
            Photo photo = photos.get(index);
            File file = new File(photo.getFilePath());

            if (file.exists()) {
                slideshowImage.setImage(new Image(file.toURI().toString()));
                label.setText(photo.getCaption() != null ? photo.getCaption() : "");
            } else {
                showErrorState("Image file not found");
            }
        } catch (Exception e) {
            showErrorState("Error loading photo");
            e.printStackTrace();
        }
    }

    /**
     * Displays an error state in the slideshow view.
     *
     * @param caption the message to display
     */
    private void showErrorState(String caption) {
        label.setText(caption);
        // Optionally set a default/error image
        // slideshowImage.setImage(new Image("/path/to/error.png"));
    }

    /**
     * Displays the next photo in the slideshow if available.
     */
    @FXML
    public void next() {
        if (photos != null && currIndex < photos.size() - 1) {
            showPhoto(++currIndex);
        }
    }

    /**
     * Displays the previous photo in the slideshow if available.
     */
    @FXML
    public void previous() {
        if (photos != null && currIndex > 0) {
            showPhoto(--currIndex);
        }
    }

    /**
     * Closes the slideshow window.
     */
    @FXML
    public void close() {
        Stage stage = (Stage) slideshowImage.getScene().getWindow();
        stage.close();
    }
}
