package model;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a user in the Photo App.
 * <p>
 * A user has a username, password, and a list of photo albums.
 * Users can add, remove, retrieve, and rename albums.
 * </p>
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The username for login. */
    private String username;

    /** The user's password (can be null for some accounts). */
    private String password;

    /** The list of albums owned by the user. */
    private List<Album> albums = new ArrayList<>();

    /**
     * Constructs a new User with the given username and password.
     *
     * @param username the username
     * @param password the password (can be null)
     */
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.albums = new ArrayList<>();
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the user's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the list of albums.
     *
     * @return the user's albums
     */
    public List<Album> getAlbums() {
        return albums;
    }

    /**
     * Adds a new album if one with the same name does not already exist.
     *
     * @param album the album to add
     * @return true if added, false if already exists or null
     */
    public boolean addAlbum(Album album) {
        if (album == null || albums.stream().anyMatch(a -> a.getName().equalsIgnoreCase(album.getName()))) return false;
        return albums.add(album);
    }

    /**
     * Removes an album by name.
     *
     * @param name the name of the album to remove
     * @return true if the album was removed, false otherwise
     */
    public boolean removeAlbum(String name) {
        return albums.removeIf(a -> a.getName().equalsIgnoreCase(name));
    }

    /**
     * Retrieves an album by name (case-insensitive).
     *
     * @param name the name of the album
     * @return the Album if found, null otherwise
     */
    public Album getAlbum(String name) {
        for (Album a : albums) {
            if (a.getName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        return null;
    }

    /**
     * Renames an existing album to a new name.
     *
     * @param oldName the current name of the album
     * @param newName the new name for the album
     * @return true if renamed successfully, false otherwise
     */
    public boolean renameAlbum(String oldName, String newName) {
        Album oldAlbum = getAlbum(oldName);
        if (oldAlbum == null || getAlbum(newName) != null) return false;

        Album renamedAlbum = new Album(newName);
        for (Photo p : oldAlbum.getPhotos()) {
            renamedAlbum.addPhoto(p);
        }

        removeAlbum(oldName);
        return addAlbum(renamedAlbum);
    }

    /**
     * Returns the username as the string representation of the user.
     *
     * @return the username
     */
    @Override
    public String toString() {
        return username;
    }
}
