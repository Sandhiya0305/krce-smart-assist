package com.krce.mobilebackend.repository;

import com.krce.mobilebackend.entity.SitePage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SitePageRepository extends JpaRepository<SitePage, Long> {
    Optional<SitePage> findByUrl(String url);
    void deleteAllByUrlNotIn(java.util.Collection<String> urls);
}
