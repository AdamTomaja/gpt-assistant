package com.cydercode.gptbridge.notes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "embeddings")
@NoArgsConstructor
@Builder
@Getter
@AllArgsConstructor
@ToString
public class Embedding {
  public enum EmbeddingType {
    NOTE,
    QUESTION,
    ANSWER
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "note_id")
  private Integer id;

  @Column(name = "content", length = 2000)
  private String content;

  @Column(name = "vector_id", length = 100)
  private String vectorId;

  @Column(name = "type", length = 100)
  private EmbeddingType type;
}
