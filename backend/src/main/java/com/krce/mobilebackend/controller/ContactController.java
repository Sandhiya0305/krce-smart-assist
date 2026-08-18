package com.krce.mobilebackend.controller;

import com.krce.mobilebackend.entity.ContactMessage;
import com.krce.mobilebackend.repository.ContactMessageRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
public class ContactController {
    private final ContactMessageRepository repository;
    public ContactController(ContactMessageRepository repository) { this.repository = repository; }

    @PostMapping
    public Map<String, String> submit(@Valid @RequestBody ContactRequest request) {
        repository.save(new ContactMessage(request.name(), request.phone(), request.message()));
        return Map.of("message", "Your message has been submitted to KRCE.");
    }

    public record ContactRequest(
            @NotBlank String name,
            @NotBlank String phone,
            String message) {}
}
