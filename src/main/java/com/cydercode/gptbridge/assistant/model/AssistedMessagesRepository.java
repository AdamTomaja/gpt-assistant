package com.cydercode.gptbridge.assistant.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AssistedMessagesRepository extends JpaRepository<AssistedMessage, Integer> {
    int countAllByEventId(String eventId);
}
