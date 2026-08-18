package com.krce.mobilebackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
public class ContactMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 120) private String name;
    @Column(nullable = false, length = 30) private String phone;
    @Column(length = 3000) private String message;
    @Column(nullable = false) private LocalDateTime createdAt;

    protected ContactMessage() {}
    public ContactMessage(String name, String phone, String message) {
        this.name = name; this.phone = phone; this.message = message; this.createdAt = LocalDateTime.now();
    }
}
