package com.cydercode.gptbridge.assistant.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Table(name = "messages")
@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
@ToString
public class Message {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "message_id")
  private Integer id;

  @Column(name = "event_id", length = 100)
  private String eventId;

  @Column(name = "sender_id", length = 100)
  private String senderId;

  @Column(name = "content", length = 2000)
  private String content;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;
}
