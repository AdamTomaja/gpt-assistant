package com.cydercode.gptbridge.assistant.model;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Message, Integer> {
  int countAllByEventId(String eventId);

  List<Message> findTop20ByOrderByCreatedAtDesc();
}
