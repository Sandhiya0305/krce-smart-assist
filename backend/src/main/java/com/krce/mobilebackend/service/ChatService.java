package com.krce.mobilebackend.service;

import com.krce.mobilebackend.entity.ChatLog;
import com.krce.mobilebackend.entity.SitePage;
import com.krce.mobilebackend.repository.ChatLogRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatService {
    private final LiveScraperService scraper;
    private final ChatLogRepository logs;

    public ChatService(LiveScraperService scraper, ChatLogRepository logs) {
        this.scraper = scraper;
        this.logs = logs;
    }

    public ChatResponse ask(String question, boolean forceLive) {
        List<SitePage> pages = forceLive
                ? scraper.forceLiveSearch(question)
                : scraper.liveSearch(question);

        if (pages.isEmpty()) {
            String answer = "I couldn't find a reliable answer on the KRCE website. Try asking about admissions, departments, courses, facilities, placements, library, management or campus information.";
            logs.save(new ChatLog(question, answer, "https://www.krce.ac.in/"));
            return new ChatResponse(answer, "https://www.krce.ac.in/", "KRCE official website", List.of());
        }

        SitePage best = pages.get(0);
        String answer = composeAnswer(question, best);
        String source = best.getCanonicalUrl() == null || best.getCanonicalUrl().isBlank()
                ? best.getUrl() : best.getCanonicalUrl();

        List<Source> sources = pages.stream()
                .map(p -> new Source(
                        p.getTitle() == null || p.getTitle().isBlank() ? "KRCE page" : p.getTitle(),
                        p.getCanonicalUrl() == null || p.getCanonicalUrl().isBlank() ? p.getUrl() : p.getCanonicalUrl(),
                        p.getImageUrl()))
                .toList();

        logs.save(new ChatLog(question, answer, source));
        return new ChatResponse(answer, source, best.getTitle(), sources);
    }

    private String composeAnswer(String question, SitePage page) {
        String content = Objects.toString(page.getContent(), "");
        if (content.isBlank()) return "I found the official KRCE page, but it did not expose readable text. Please open the source below.";

        String[] sentences = content.split("(?<=[.!?])\\s+");
        Set<String> tokens = new LinkedHashSet<>(Arrays.asList(
                question.toLowerCase(Locale.ROOT).split("[^a-z0-9]+")));
        List<String> matches = new ArrayList<>();

        for (String sentence : sentences) {
            String lower = sentence.toLowerCase(Locale.ROOT);
            int hits = 0;
            for (String token : tokens) {
                if (token.length() >= 4 && lower.contains(token)) hits++;
            }
            if (hits > 0) matches.add(sentence.trim());
        }

        if (matches.isEmpty()) return firstWords(content, 100);
        return String.join(" ", matches.stream().limit(4).toList());
    }

    private String firstWords(String text, int maxWords) {
        String[] words = text.split(" ");
        int count = Math.min(words.length, maxWords);
        return String.join(" ", Arrays.copyOf(words, count)) + (words.length > count ? "…" : "");
    }

    public record Source(String title, String url, String imageUrl) {}
    public record ChatResponse(String answer, String sourceUrl, String sourceTitle, List<Source> sources) {}
}
