# KRCE Mobile System

This is the Android Studio runnable mobile version of the KRCE college portal, paired with its Spring Boot backend.

## Stack

### Android
- Java 17
- Android Studio
- XML Views
- AndroidX
- Material Components
- Retrofit + Gson
- Glide

### Backend
- Spring Boot 3.4.3
- Java 17
- SQLite
- Spring Data JPA
- Jsoup

## Modules

1. **College Explorer**
   - Home dashboard
   - Live website sync
   - Search official KRCE pages
   - Image cards
   - Open official source pages

2. **KRCE Bot**
   - Chat-style interface inspired by the supplied reference
   - Quick questions
   - Free-form question input
   - Live KRCE website retrieval
   - Answer + official source deep link

3. **Contact**
   - Name / phone / message form
   - Saved by Spring Boot into SQLite

## Run backend first

Requirements: JDK 17 and Maven.

```bash
cd backend
mvn spring-boot:run
```

Keep the backend running on port 8080.

## Run Android app

Open the `android-app` folder in Android Studio.

The default API URL is:

`http://10.0.2.2:8080/`

This is correct for the Android Studio emulator because `10.0.2.2` maps to the host PC's localhost.

### Physical Android phone

If you run on a real phone, edit:

`android-app/app/build.gradle`

and change:

```gradle
buildConfigField 'String', 'API_BASE_URL', '"http://10.0.2.2:8080/"'
```

to your PC's LAN address, for example:

```gradle
buildConfigField 'String', 'API_BASE_URL', '"http://192.168.1.10:8080/"'
```

Your phone and PC must be on the same network, and Windows Firewall must allow port 8080.

## First run

1. Start Spring Boot.
2. Open `android-app` in Android Studio.
3. Let Gradle sync.
4. Start an emulator.
5. Run the Android app.
6. Open Home -> **Sync Official Site**.
7. Open Explore or KRCE Bot.

## Important architecture note

The Android app does NOT scrape KRCE directly. The Spring Boot server owns the scraper. The mobile app talks to the server through REST APIs. This prevents scraper logic from being duplicated in the APK.

## Reality check about live scraping

`live=true` deliberately forces a fresh crawl for the chatbot. That is suitable for a college demo/prototype but not ideal for high traffic. A production deployment should use scheduled/incremental crawling, caching, rate limiting and a stronger retrieval layer.
