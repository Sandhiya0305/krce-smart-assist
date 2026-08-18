package com.krce.mobilebackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_logs")
public class ChatLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 2000)
    private String question;
    @Lob
    private String answer;
    @Column(length = 1000)
    private String sourceUrl;
    @Column(nullable = false)
    private LocalDateTime createdAt;

    protected ChatLog() {}

    public ChatLog(String question, String answer, String sourceUrl) {
        this.question = question;
        this.answer = answer;
        this.sourceUrl = sourceUrl;
        this.createdAt = LocalDateTime.now();
    }
}
