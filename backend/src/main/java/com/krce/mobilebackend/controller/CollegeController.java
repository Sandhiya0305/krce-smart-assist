package com.krce.mobilebackend.controller;

import com.krce.mobilebackend.entity.SitePage;
import com.krce.mobilebackend.repository.SitePageRepository;
import com.krce.mobilebackend.service.LiveScraperService;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/college")
public class CollegeController {
    private final SitePageRepository repository;
    private final LiveScraperService scraper;

    public CollegeController(SitePageRepository repository, LiveScraperService scraper) {
        this.repository = repository;
        this.scraper = scraper;
    }

    @GetMapping("/pages")
    public List<SitePage> pages() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(SitePage::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .limit(100)
                .toList();
    }

    @GetMapping("/search")
    public List<SitePage> search(@RequestParam String q) {
        return scraper.liveSearch(q);
    }

    @PostMapping("/sync")
    public Map<String, Object> sync() {
        int saved = scraper.scrapeSite();
        return Map.of("saved", saved, "message", "KRCE website synchronized");
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "site", "https://www.krce.ac.in/");
    }
}
