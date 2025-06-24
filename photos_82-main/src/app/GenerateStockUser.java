package app;

import model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Utility class to generate a default stock user and save it to a data file.
 * <p>
 * Loads all image files from the "data" directory and adds them to a default
 * stock user's album, then serializes the data to "users.dat".
 * </p>
 */
public class GenerateStockUser {

    /**
     * Entry point for generating the stock user.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        generate();
    }

    /**
     * Generates a stock user with one album populated with photos from the "data" directory.
     * Serializes the user data to a file named "users.dat".
     */
    public static void generate() {
        Map<String, User> users = new HashMap<>();
        User stockUser = new User("stock", "stock");
        Album stockAlbum = new Album("stock");

        File stockDir = new File("data");
        File[] files = stockDir.listFiles((dir, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                   lower.endsWith(".png") || lower.endsWith(".bmp") || lower.endsWith(".gif");
        });

        if (files != null) {
            for (File file : files) {
                try {
                    Photo p = new Photo("data/" + file.getName());
                    stockAlbum.addPhoto(p);
                } catch (Exception ignored) {}
            }
        }

        stockUser.addAlbum(stockAlbum);
        users.put("stock", stockUser);

        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("users.dat"))) {
            out.writeObject(users);
        } catch (Exception ignored) {}
    }
}
