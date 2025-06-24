# JavaFXAlbumManager

A feature-rich desktop application for managing photo albums, built using JavaFX. Users can create, view, organize, and search photo collections with tagging, captioning, and slideshow support — all through an intuitive GUI.

---

## 🚀 Features

- 📂 Create, delete, rename albums
- 🖼️ Add, remove, and caption photos
- 🏷️ Add custom tags (e.g., `person:John`, `location:Paris`)
- 🔍 Search photos by single or multiple tags
- 🖥️ Slideshow view of album contents
- 📅 Sort photos by date range
- 🔄 Copy/move photos across albums
- 💾 Persistent data storage via Java Serialization

---

## 🛠️ Technologies Used

- **Java 21**
- **JavaFX SDK 21**
- **FXML** for UI design
- **MVC Architecture**
- **Object Serialization** for persistent storage

---

## 🛠️ Setup Instructions for Graders

This project uses **JavaFX SDK 21** and **Java 21**.

To run it in **Visual Studio Code**:

1. **Install JavaFX SDK 21** if not already installed.

2. **Update `.vscode/launch.json`**:
   - Replace the hardcoded path:
     ```
     "/Users/kritibajaad/Downloads/javafx-sdk-21.0.6/lib"
     ```
     with the path to **your** JavaFX SDK 21 `lib` directory.

3. **Add JavaFX SDK via the VS Code interface**:
   - Open Command Palette: `Cmd+Shift+P` (Mac) or `Ctrl+Shift+P` (Windows)
   - Select **"Java: Configure Java Runtime"**
   - Under **"Referenced Libraries"**, click **"Add"**
   - Select the `lib` folder from your JavaFX SDK 21 installation

> ⚠️ `settings.json` will be updated automatically. It assumes `src` is your starting folder — this is important for successful compilation and execution.

--- 

## 📁 Project Structure

| Folder/File       | Description                                |
|-------------------|--------------------------------------------|
| `controller/`      | JavaFX Controllers for each screen         |
| `model/`           | Photo, Album, Tag, and User classes        |
| `view/`            | FXML files and styling                     |
| `PhotosFX.java`    | Main launcher class                        |
