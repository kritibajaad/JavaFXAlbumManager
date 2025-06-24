package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Manages all user accounts and their albums.
 * <p>
 * Handles loading, saving, and initializing user data,
 * including a default stock user with sample photos.
 * </p>
 */
public class PhotoManager {

    /** A map of usernames to user objects. */
    private Map<String, User> users = new HashMap<>();

    /**
     * Constructs a new PhotoManager and loads user data.
     * If no data file is found, initializes a stock user.
     */
    public PhotoManager() {
        File file = new File("users.dat");
        if (file.exists()) {
            loadUsers();  
        } else {
            loadStockUser(); 
            saveUsers();     
        }
    }

    /**
     * Loads a default stock user and adds any photos found in the "data" directory.
     */
    public void loadStockUser() {
        User stockUser = new User("stock", "stock");
        Album stockAlbum = new Album("stock");

        File stockDir = new File("data");
        File[] files = stockDir.listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".gif") || name.endsWith(".jpeg") || name.endsWith(".bmp"));

        if (files != null) {
            for (File file : files) {
                try {
                    Photo p = new Photo(file.getAbsolutePath());
                    stockAlbum.addPhoto(p);
                } catch (Exception e) {
                    e.printStackTrace(); // skip invalid photo
                }
            }
        }

        stockUser.addAlbum(stockAlbum);
        users.put("stock", stockUser);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username the username to look up
     * @return the User object if found, null otherwise
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Removes a user by their username.
     *
     * @param username the username of the user to remove
     */
    public void removeUser(String username) {
        users.remove(username);
    }

    /**
     * Returns a collection of all users in the system.
     *
     * @return a collection of User objects
     */
    public Collection<User> getAllUsers() {
        return users.values();
    }

    /**
     * Adds a user to the system and immediately saves the state.
     *
     * @param user the user to add
     */
    public void addUser(User user) {
        users.put(user.getUsername(), user);
        saveUsers();
    }

    /**
     * Saves all users and their data to a file.
     */
    public void saveUsers() {
        try {
            FileOutputStream fileOut = new FileOutputStream("users.dat");
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(users);
            out.close();
            fileOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads all users and their data from a file.
     */
    @SuppressWarnings("unchecked")
    public void loadUsers() {
        try {
            FileInputStream fileIn = new FileInputStream("users.dat");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            users = (Map<String, User>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            users = new HashMap<>(); // fallback if file doesn't exist yet
        }
    }
}
