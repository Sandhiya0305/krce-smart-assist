package com.krce.mobilebackend.service;

import com.krce.mobilebackend.entity.SitePage;
import com.krce.mobilebackend.repository.SitePageRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class LiveScraperService {
    private final SitePageRepository repository;

    @Value("${krce.site.url}") private String siteUrl;
    @Value("${krce.scraper.max-pages:30}") private int maxPages;
    @Value("${krce.scraper.timeout-ms:15000}") private int timeoutMs;
    @Value("${krce.scraper.user-agent:KRCE-Mobile-College-Assistant/1.0}") private String userAgent;
    @Value("${krce.scraper.refresh-minutes:5}") private int refreshMinutes;

    public LiveScraperService(SitePageRepository repository) {
        this.repository = repository;
    }

    /** Force a website crawl. Used by the sync endpoint and by live chat refreshes. */
    public synchronized int scrapeSite() {
        String root = normalize(siteUrl);
        String siteHost = host(root);
        Deque<String> queue = new ArrayDeque<>();
        Set<String> seen = new LinkedHashSet<>();
        queue.add(root);
        int saved = 0;

        while (!queue.isEmpty() && seen.size() < maxPages) {
            String url = queue.poll();
            if (url == null || !seen.add(url)) continue;

            try {
                Document doc = Jsoup.connect(url)
                        .userAgent(userAgent)
                        .timeout(timeoutMs)
                        .followRedirects(true)
                        .get();

                if (!"text/html".equalsIgnoreCase(doc.connection().response().contentType())
                        && doc.body() == null) continue;

                SitePage page = repository.findByUrl(url).orElseGet(SitePage::new);
                page.setUrl(url);
                page.setTitle(clean(doc.title()));
                page.setDescription(clean(doc.select("meta[name=description]").attr("content")));
                page.setContent(extractText(doc));
                page.setImageUrl(firstImage(doc));
                String canonical = doc.select("link[rel=canonical]").attr("abs:href");
                page.setCanonicalUrl(canonical.isBlank() ? url : stripFragment(canonical));
                page.setScrapedAt(LocalDateTime.now());
                repository.save(page);
                saved++;

                for (Element anchor : doc.select("a[href]")) {
                    String next = anchor.absUrl("href");
                    if (next == null || next.isBlank()) continue;
                    next = stripFragment(next);
                    if (next.startsWith("http")
                            && host(next).equalsIgnoreCase(siteHost)
                            && isHtmlLike(next)
                            && !seen.contains(next)) {
                        queue.add(next);
                    }
                }
            } catch (Exception ignored) {
                // One broken/slow page must not abort the entire crawl.
            }
        }
        return saved;
    }

    /**
     * Keeps answers reasonably live without crawling 30 pages for every single keystroke.
     * The mobile chatbot always asks for a fresh search; this method refreshes the cache when stale.
     */
    public List<SitePage> liveSearch(String query) {
        if (isCacheStale()) scrapeSite();
        List<SitePage> pages = repository.findAll();
        return rank(query, pages);
    }

    public List<SitePage> forceLiveSearch(String query) {
        scrapeSite();
        return rank(query, repository.findAll());
    }

    private boolean isCacheStale() {
        Optional<SitePage> newest = repository.findAll().stream()
                .filter(p -> p.getScrapedAt() != null)
                .max(Comparator.comparing(SitePage::getScrapedAt));
        return newest.isEmpty() || newest.get().getScrapedAt()
                .isBefore(LocalDateTime.now().minusMinutes(refreshMinutes));
    }

    private List<SitePage> rank(String query, List<SitePage> pages) {
        String normalized = Objects.toString(query, "").toLowerCase(Locale.ROOT);
        Set<String> tokens = new LinkedHashSet<>(Arrays.asList(normalized.split("[^a-z0-9]+")));
        List<ScoredPage> scored = new ArrayList<>();

        for (SitePage page : pages) {
            String title = Objects.toString(page.getTitle(), "").toLowerCase(Locale.ROOT);
            String description = Objects.toString(page.getDescription(), "").toLowerCase(Locale.ROOT);
            String content = Objects.toString(page.getContent(), "").toLowerCase(Locale.ROOT);
            int score = 0;
            for (String token : tokens) {
                if (token.length() < 3) continue;
                if (title.contains(token)) score += 5;
                if (description.contains(token)) score += 3;
                if (content.contains(token)) score += 1;
            }
            if (score > 0) scored.add(new ScoredPage(page, score));
        }

        scored.sort(Comparator.comparingInt(ScoredPage::score).reversed());
        return scored.stream().limit(6).map(ScoredPage::page).toList();
    }

    private String extractText(Document doc) {
        doc.select("script,style,noscript,svg,nav,footer,header").remove();
        return doc.body() == null ? "" : clean(doc.body().text());
    }

    private String firstImage(Document doc) {
        String og = doc.select("meta[property=og:image]").attr("abs:content");
        if (!og.isBlank()) return og;
        Element img = doc.select("img[src]").first();
        return img == null ? "" : img.absUrl("src");
    }

    private String clean(String text) {
        return Objects.toString(text, "").replaceAll("\\s+", " ").trim();
    }

    private String normalize(String url) { return stripFragment(url.trim()); }
    private String stripFragment(String url) { int i = url.indexOf('#'); return i >= 0 ? url.substring(0, i) : url; }
    private boolean isHtmlLike(String url) {
        String x = url.toLowerCase(Locale.ROOT);
        return !x.matches(".*\\.(pdf|jpg|jpeg|png|gif|webp|svg|zip|doc|docx|xls|xlsx|mp4|mp3)(\\?.*)?$");
    }
    private String host(String url) {
        try { return URI.create(url).getHost(); } catch (Exception e) { return ""; }
    }

    private record ScoredPage(SitePage page, int score) {}
}
