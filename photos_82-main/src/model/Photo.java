package model;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * Represents a photo with metadata including file path, caption, date taken,
 * and associated tags. Provides functionality to manage and retrieve these details.
 */
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Path to the photo file on disk. */
    private String filePath;

    /** Date the photo was taken (based on file modification time). */
    private Calendar dateTaken;

    /** Optional user-defined caption for the photo. */
    private String caption;

    /** Set of tags associated with the photo. */
    private Set<Tag> tags = new HashSet<>();

    /** Predefined set of standard tag types. */
    public static final Set<String> presetTagTypes = new HashSet<>(Set.of("person", "location"));

    /** User-defined custom tag types. */
    public static final Set<String> customTagTypes = new HashSet<>();

    /**
     * Constructs a photo from the specified file path.
     *
     * @param filePath the path to the image file
     * @throws IllegalArgumentException if the file does not exist
     */
    public Photo(String filePath) {
        this.filePath = filePath;
        File file = new File(filePath);

        if (!file.exists()) {
            throw new IllegalArgumentException("File does not exist: " + filePath);
        }

        this.dateTaken = extractDate(filePath);
    }

    /**
     * Extracts the date from the file's last modified timestamp.
     *
     * @param path the file path
     * @return a Calendar representing the date taken
     */
    private Calendar extractDate(String path) {
        File file = new File(path);
        long lastModified = file.lastModified();

        Calendar info = Calendar.getInstance();
        info.setTime(new Date(lastModified));
        info.set(Calendar.MILLISECOND, 0);

        return info;
    }

    /**
     * Returns the file path of the photo.
     *
     * @return the file path
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Returns the date the photo was taken.
     *
     * @return the date as a Calendar object
     */
    public Calendar getDateTaken() {
        return dateTaken;
    }

    /**
     * Returns the date taken as a formatted string.
     *
     * @return the formatted date string
     */
    public String getFormattedDate() {
        return dateTaken.getTime().toString();
    }

    /**
     * Returns the date taken as a LocalDate object.
     *
     * @return the date as a LocalDate
     */
    public java.time.LocalDate getDateAsLocalDate() {
        return dateTaken.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Adds a tag to the photo, replacing existing location tags if necessary.
     *
     * @param name the tag type
     * @param value the tag value
     * @return true if the tag was added, false otherwise
     */
    public boolean addTag(String name, String value) {
        if (name == null || value == null) return false;

        Tag newTag = new Tag(name.trim(), value.trim());

        if (name.equalsIgnoreCase("location")) {
            tags.removeIf(tag -> tag.getName().equalsIgnoreCase("location"));
        }

        return tags.add(newTag);
    }

    /**
     * Removes a tag from the photo.
     *
     * @param name the tag type
     * @param value the tag value
     * @return true if the tag was removed, false otherwise
     */
    public boolean removeTag(String name, String value) {
        return tags.remove(new Tag(name.trim(), value.trim()));
    }

    /**
     * Adds a new custom tag type to the global set.
     *
     * @param newType the new tag type to add
     */
    public static void addCustomTagType(String newType) {
        if (newType != null && !newType.isBlank()) {
            customTagTypes.add(newType.toLowerCase().trim());
        }
    }

    /**
     * Returns all tag types including preset and custom types.
     *
     * @return a set of all tag types
     */
    public static Set<String> getAllTagTypes() {
        Set<String> all = new HashSet<>();
        all.addAll(presetTagTypes);
        all.addAll(customTagTypes);
        return all;
    }

    /**
     * Returns the tags associated with this photo.
     *
     * @return a set of tags
     */
    public Set<Tag> getTags() {
        return tags;
    }

    /**
     * Returns the caption of the photo.
     *
     * @return the caption
     */
    public String getCaption() {
        return caption;
    }

    /**
     * Sets the caption for the photo.
     *
     * @param caption the caption to set
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }

    /**
     * Returns a string representation of the photo, including file name,
     * caption, and date.
     *
     * @return string representation of the photo
     */
    @Override
    public String toString() {
        String fileName = new File(filePath).getName();
        String caption = getCaption() != null ? getCaption() : "(no caption)";
        String date = getFormattedDate();
        return fileName + " | " + caption + " | " + date;
    }
}
