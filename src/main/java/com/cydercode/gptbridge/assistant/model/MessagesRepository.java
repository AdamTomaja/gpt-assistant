package com.cydercode.gptbridge.assistant.model;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessagesRepository extends JpaRepository<Message, Integer> {
  int countAllByEventId(String eventId);

  @NotNull
  Page<Message> findAll(@NotNull Pageable pageable);
}
