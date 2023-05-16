package com.cydercode.gptbridge.notes;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmbeddingsRepository extends JpaRepository<Embedding, Integer> {
  Optional<Embedding> findOneByVectorId(String vectorId);
}
