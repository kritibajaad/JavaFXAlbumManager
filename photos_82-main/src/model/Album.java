package model;

import java.io.Serializable;
import java.util.*;

/**
 * Represents a photo album containing a list of photos.
 * Provides methods to manage photos within the album.
 */
public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The name of the album. */
    private String name;

    /** The list of photos contained in the album. */
    private List<Photo> photos;

    /**
     * Constructs a new Album with the specified name.
     *
     * @param name the name of the album
     */
    public Album(String name) {
        this.name = name;
        this.photos = new ArrayList<>();
    }

    /**
     * Returns the name of the album.
     *
     * @return the album name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the list of photos in the album.
     *
     * @return the list of photos
     */
    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * Adds a photo to the album if it is not already present.
     *
     * @param photo the photo to add
     * @return true if the photo was added, false otherwise
     */
    public boolean addPhoto(Photo photo) {
        if (photo == null || photos.contains(photo)) return false;
        return photos.add(photo);
    }

    /**
     * Removes a photo from the album.
     *
     * @param photo the photo to remove
     * @return true if the photo was removed, false otherwise
     */
    public boolean removePhoto(Photo photo) {
        return photos.remove(photo);
    }

    /**
     * Checks if a specific photo exists in the album.
     *
     * @param photo the photo to check
     * @return true if the photo exists, false otherwise
     */
    public boolean containsPhoto(Photo photo) {
        return photos.contains(photo);
    }

    /**
     * Returns the number of photos in the album.
     *
     * @return the photo count
     */
    public int getPhotoCount() {
        return photos.size();
    }

    /**
     * Returns a string representation of the album.
     *
     * @return the album name and photo count
     */
    @Override
    public String toString() {
        return name + " (" + getPhotoCount() + " photos)";
    }
}
