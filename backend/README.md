# KRCE Mobile Backend

Spring Boot 3.4.3 + Java 17 + SQLite + Jsoup.

## Run

```bash
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

## Main endpoints

- `GET /api/college/health`
- `POST /api/college/sync`
- `GET /api/college/pages`
- `GET /api/college/search?q=hostel`
- `POST /api/chat/ask`
- `POST /api/contact`

Chat request example:

```json
{
  "question": "What facilities are available in the hostel?",
  "live": true
}
```

The scraper stays on `www.krce.ac.in`, skips common binary files, extracts page text/title/description/image/canonical URL, and stores the searchable content in `krce.db`.

The `live=true` chatbot mode forces a fresh crawl before ranking relevant pages. This is intentionally a demo/research architecture; for production traffic, replace full-site crawling per question with scheduled crawling + incremental updates to avoid hammering the official site.
