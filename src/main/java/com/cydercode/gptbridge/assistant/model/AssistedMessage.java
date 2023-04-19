package com.cydercode.gptbridge.assistant.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assisted_messages")
@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
@ToString
public class AssistedMessage {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "assisted_message_id")
  private Integer id;

  @Column(name = "event_id", length = 100)
  private String eventId;

  @Column(name = "sender_id", length = 100)
  private String senderId;

  @Column(name = "request_message", length = 2000)
  private String request;

  @Column(name = "response_message", length = 2000)
  private String response;

  @Column(name = "initial_sync")
  private Boolean initialSync;
}
