package com.cydercode.gptbridge.openai;

import com.theokanning.openai.embedding.EmbeddingRequest;
import com.theokanning.openai.embedding.EmbeddingResult;
import com.theokanning.openai.service.OpenAiService;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OpenAiEmbeddingService {
  private final OpenAiService openAiService;

  public List<Double> createEmbedding(String text) {
    EmbeddingResult embeddingResult =
        openAiService.createEmbeddings(
            EmbeddingRequest.builder()
                .model("text-embedding-ada-002")
                .input(Collections.singletonList(text))
                .build());
    return embeddingResult.getData().get(0).getEmbedding();
  }
}
