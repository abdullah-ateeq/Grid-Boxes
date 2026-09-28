# 🎲 Grid Boxes

**Grid Boxes** is a modern, beautifully designed Android game based on the classic **Dots and Boxes** strategy game. Built 100% with **Jetpack Compose** and **Kotlin**, it features a sleek geometric UI, immersive sound and haptic feedback, customizable board themes, persistent stats tracking, and responsive gameplay.

---

## 🌟 Features

- **🎮 Classic Dots & Boxes Gameplay**: Play turn-by-turn on a 5x5 grid. Connect dots, complete boxes, earn extra turns, and claim victory!
- **🎨 Custom Themes & Dark Mode**:
  - **App Theme**: System Default, Light, or Dark mode.
  - **Board Themes**: Classic and Midnight styling options.
- **🔊 Rich Audio & Haptic Feedback**: Tactile vibrations and synthesized audio cues for border selection, box completions, and win/loss celebrations.
- **📊 Persistent Game Statistics**: Powered by **DataStore Preferences**, tracking:
  - Total games played
  - Player 1 & Player 2 win counts & draws
  - Highest single-game score
  - Largest winning margin
- **🛠 Gameplay Customization**: Toggle sound, haptic feedback, "last border drawn" highlight, and border guide lines.
- **📖 Interactive Guide & Settings**: Comprehensive "How to Play" visual tutorial screen and an "About" page.
- **⚡ Modern Architecture & Performance**: Canvas-based custom rendering for maximum frame-rate and precision touch handling.

---

## 🏗 Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material 3)
- **Navigation**: [Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- **Architecture**: MVVM (Model-View-ViewModel) with StateFlow / Kotlin Coroutines
- **Local Storage**: [Jetpack DataStore Preferences](https://developer.android.com/topic/libraries/architecture/datastore)
- **Audio & Haptics**: Native `AudioTrack` synthesizer & `Vibrator` / `VibratorManager`
- **Build System**: Gradle with Kotlin DSL & Version Catalog (`libs.versions.toml`)

---

## 📁 Project Structure

```text
com.example.gridboxes/
├── data/           # GamePreferences & DataStore models
├── logic/          # Core GameLogic, Move calculation, Grid geometry
├── model/          # Data classes (GridBoxesState, Player, BorderKey, CellPosition)
├── navigation/     # Jetpack Compose Navigation Graph & Routes
├── ui/
│   ├── components/ # GameBoard Canvas, PlayerPanel, Buttons, Dialogs
│   ├── screens/    # HomeScreen, GameScreen, SettingsScreen, HowToPlayScreen, ResultScreen, AboutScreen
│   ├── theme/      # Custom GridBoxes Palette, Typography, Theme switching
│   └── viewmodel/  # GameViewModel managing game state and settings
└── util/           # SoundManager & HapticHelper utilities
```

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio**: Ladybug (2024.2.1) or newer recommended
- **JDK**: JDK 17 or higher
- **Minimum SDK**: API Level 24 (Android 7.0)
- **Target SDK**: API Level 35 (Android 15)

### Building the Project

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/Grid-Boxes.git
   cd Grid-Boxes
   ```

2. **Open in Android Studio** and sync Gradle.

3. **Build & Run**:
   Select your connected device or emulator and hit **Run (Shift + F10)**, or run via command line:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Run Unit Tests**:
   ```bash
   ./gradlew testDebugUnitTest
   ```

---

## 📄 License

This project is open-source and available under the [MIT License](LICENSE).
