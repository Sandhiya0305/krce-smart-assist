# KRCE Mobile Android App

Open this folder directly in Android Studio.

## Create/run

- Android Studio Koala or newer recommended
- JDK 17 for Gradle/Android build
- Android SDK 35 installed
- Android emulator or Android phone

## Backend connection

The app uses `BuildConfig.API_BASE_URL`.

Default:

`http://10.0.2.2:8080/`

That works on the Android Studio emulator when Spring Boot is running on the same PC.

For a real phone, replace it with your PC's LAN IP in `app/build.gradle`.

## Backend must be running

Start `../backend` first:

```bash
mvn spring-boot:run
```

Then run the Android app.
