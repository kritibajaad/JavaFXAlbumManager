package app;

import javafx.application.Application;
import javafx.stage.Stage;
import model.PhotoManager;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.net.URL;

/**
 * The entry point for the Photo App JavaFX application.
 * <p>
 * This class sets up the application lifecycle, including loading user data,
 * displaying the login screen using FXML, and saving user data on exit.
 * </p>
 */
public class Photos extends Application {

    /**
     * A static reference to the {@link PhotoManager}, used to manage user data and session state.
     */
    private static PhotoManager photoManager;

    /**
     * Launches the JavaFX application.
     *
     * @param args Command-line arguments passed to the application (unused).
     */
    public static void main(String[] args) {
        launch(args); // Entry point
    }

    /**
     * Returns the shared instance of {@link PhotoManager}.
     *
     * @return the application's PhotoManager instance.
     */
    public static PhotoManager getPhotoManager() {
        return photoManager;
    }

    /**
     * Called automatically when the application starts.
     * Loads user data, initializes the login view, and sets up the main stage.
     *
     * @param stage The primary stage provided by the JavaFX runtime.
     */
    @Override
    public void start(Stage stage) {
        photoManager = new PhotoManager();
        photoManager.loadUsers(); // 🔁 Load from disk

        try {
            // Load the FXML resource for the login screen
            URL url = getClass().getResource("/view/login_view.fxml");
            System.out.println("Loaded FXML from: " + url);  // Debug print

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Create the scene and attach it to the stage
            Scene scene = new Scene(root);
            stage.setTitle("Photo App - Login");
            stage.setScene(scene);
            stage.setResizable(false); // Prevent resizing
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Save user data when the window is closed
        stage.setOnCloseRequest(event -> {
            if (photoManager != null) {
                photoManager.saveUsers();  // Ensures safe quit
            }
        });
    }

    /**
     * Called when the application is closed.
     * Ensures that user data is saved before exiting.
     */
    @Override
    public void stop() {
        if (photoManager != null) {
            photoManager.saveUsers(); // Persist data
        }
    }
}
