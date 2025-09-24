# Wise

Wise is a multi-module Android project primarily written in Kotlin. It serves as a collection of demo applications and feature experiments, including interactive learning modules, note-taking functionality, Unsplash integration, and user interface experiments. The repository contains several modules such as `trinity`, `TestPresso`, `Noteapp`, `Unsplash`, and `WiseTest`, each with its own main activity and focus.

---

## Features

- **Offline Caching**: Ensures core content and user data are available even without an internet connection.
- **Firebase Notifications**: Integrates with Firebase Cloud Messaging to deliver push notifications and updates.
- **Dark Mode**: Full support for system-wide dark mode, providing a modern and comfortable user experience.
- **Trinity Module**: Interactive learning activities, including lessons, quizzes (like true/false), comic fragments, drag-and-drop exercises, and vibration feedback. Integrates with Firebase Analytics and supports dynamic links.
- **Noteapp Module**: Simple note-taking application with basic CRUD operations.
- **Unsplash Module**: Integrates with the Unsplash API to display images.
- **TestPresso Module**: Demonstrates navigation and UI interactions using buttons and activities.
- **WiseTest**: Testing harness with dependency injection using Hilt and navigation components.

---

## Setup Instructions

1. **Clone the Repository**
   ```bash
   git clone https://github.com/rimshadpcs/wise.git
   ```

2. **Open in Android Studio**
   - Open the project in Android Studio (preferably version Flamingo or above).
   - Let Gradle sync and download dependencies.

3. **Configure API Keys (if required)**
   - Some modules (like Unsplash) may require API keys. Set these in your `local.properties` or as environment variables as needed.

4. **Build and Run**
   - Select the desired module/app in the run configuration dropdown.
   - Connect an Android device or start an emulator.
   - Run the application.

---

## Usage

- **Trinity**: Navigate through lessons and activities, which may include interactive HTML content, quizzes, and feedback via vibration and pop-ups.
- **Noteapp**: Launch the note-taking module to create, edit, and delete notes.
- **TestPresso**: Explore UI navigation and button interaction demos.
- **Unsplash**: Browse images (API key setup may be required).
- **WiseTest**: Used for instrumented testing and demoing DI/navigation.

---

## Technologies Used

- **Kotlin**
- **Android Jetpack (Navigation, ViewModel, LiveData, etc.)**
- **Dagger Hilt** (for dependency injection)
- **Firebase Analytics & Cloud Messaging**
- **Coroutines**
- **Offline caching strategies**
- **HTML/JavaScript (for embedded activities)**
- **Dark Mode Support**

---

## Contributing

Contributions are welcome! Please fork the repository, create a branch for your feature or bugfix, and open a pull request.

---

## License

This project currently does not specify a license. Please contact the repository owner for more information.

---

## Author

- [rimshadpcs](https://github.com/rimshadpcs)

---

**Note:** As this repository contains several independent demo projects, refer to individual module directories for more specific documentation and configuration.