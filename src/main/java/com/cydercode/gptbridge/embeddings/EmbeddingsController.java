package com.cydercode.gptbridge.embeddings;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmbeddingsController {

  private final EmbeddingService embeddingService;

  @PostMapping("/embeddings")
  public Embedding createNote(@RequestBody Embedding requestedEmbedding) {
    return embeddingService.createEmbedding(requestedEmbedding);
  }

  @PostMapping("/embeddings/search")
  public List<Embedding> search(@RequestBody Embedding note) {
    return embeddingService.search(note);
  }
}
