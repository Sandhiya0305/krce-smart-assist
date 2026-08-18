package com.krce.mobilebackend.repository;

import com.krce.mobilebackend.entity.ChatLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatLogRepository extends JpaRepository<ChatLog, Long> {}
