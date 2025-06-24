package model;

import java.io.Serializable;

/**
 * Represents a tag associated with a photo.
 * <p>
 * Tags consist of a name-value pair such as "person: john" or "location: paris".
 * Tags are case-insensitive and trimmed of whitespace.
 * </p>
 */
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The name of the tag (e.g., person, location). */
    private String name;

    /** The value of the tag (e.g., John, Paris). */
    private String value;

    /**
     * Constructs a Tag with the specified name and value.
     *
     * @param name the tag name (must not be null)
     * @param value the tag value (must not be null)
     */
    public Tag(String name, String value) {
        if (name == null || value == null) {
            throw new IllegalArgumentException("Tag name and value cannot be null");
        }

        this.name = name.toLowerCase().trim();
        this.value = value.toLowerCase().trim();
    }

    /**
     * Returns the name of the tag.
     *
     * @return the tag name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the value of the tag.
     *
     * @return the tag value
     */
    public String getValue() {
        return value;
    }

    /**
     * Checks if this tag is equal to another object.
     * Tags are equal if both name and value match (case-insensitive).
     *
     * @param obj the object to compare
     * @return true if the tags are equal, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Tag)) {
            return false;
        }
        Tag other = (Tag) obj;
        return name.equals(other.name) && value.equals(other.value);
    }

    /**
     * Returns a hash code for the tag based on its name and value.
     *
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return name.hashCode() + value.hashCode();
    }

    /**
     * Returns a string representation of the tag in the format "name: value".
     *
     * @return the string representation of the tag
     */
    @Override
    public String toString() {
        return name + ": " + value;
    }
}
