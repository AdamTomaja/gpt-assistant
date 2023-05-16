package com.cydercode.gptbridge.assistant;

import com.cydercode.gptbridge.assistant.intention.IntentionService;
import com.cydercode.gptbridge.embeddings.Embedding;
import com.cydercode.gptbridge.embeddings.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemoryService {

  private final IntentionService intentionService;
  private final EmbeddingService embeddingsService;

  @Async
  public void saveMemory(String message) {
    embeddingsService.createEmbedding(
        Embedding.builder().content(message).type(Embedding.EmbeddingType.MEMORY).build());
  }
}
