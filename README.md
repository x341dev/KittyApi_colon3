# KittyApi :3

An Android Jetpack Compose app that fetches random cats from TheCatAPI, lets you toggle light/dark mode, switch between list/grid layouts, view details, and save favorites locally.

## Features
- Fetch 10 random cats per refresh
- List or grid layout (applies to home and favorites)
- Cat detail screen with breed info and favorite toggle
- Favorites persisted with Room
- Theme preference (dark/light) persisted with DataStore

## Setup
1. Install Android Studio (Giraffe+), Android SDK 36, JDK 17 or 11.
2. Clone the repo.
3. Add your Cat API key:
   - Copy `secrets.defaults.properties` to `secrets.properties` (gitignored).
   - Replace the value of `CAT_API_KEY` with your key.
   - The Gradle Secrets plugin injects it into `BuildConfig.CAT_API_KEY`.
4. Sync Gradle.
5. Run the app on API 33+ (minSdk 32, targetSdk 36).

## Build/Run
```bash
./gradlew clean assembleDebug
./gradlew installDebug
```
Or use Android Studio “Run”.

## Usage
- Home: tap Reload to get 10 new cats; tap a card to open details; tap heart to add/remove favorite.
- Favorites: shows saved cats in list/grid matching your setting; remove via button.
- Settings: toggle dark mode and list/grid; delete all favorites.

## Tech Stack
- Kotlin, Jetpack Compose, Navigation Compose
- Retrofit + Gson, Coil
- Room (favorites), DataStore (settings)
