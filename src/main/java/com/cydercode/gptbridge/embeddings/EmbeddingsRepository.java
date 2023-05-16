package com.cydercode.gptbridge.embeddings;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface EmbeddingsRepository extends JpaRepository<Embedding, Integer> {
  @Query("SELECT e FROM Embedding e WHERE e.vectorId IN :vectorIds")
  List<Embedding> findByVectorIds(List<String> vectorIds);
}
