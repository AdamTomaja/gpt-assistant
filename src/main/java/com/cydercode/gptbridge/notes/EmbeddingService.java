package com.cydercode.gptbridge.notes;

import com.cydercode.gptbridge.openai.OpenAiEmbeddingService;
import com.cydercode.gptbridge.pinecone.PineconeService;
import io.pinecone.proto.ScoredVector;
import io.pinecone.proto.SingleQueryResults;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmbeddingService {

  private final OpenAiEmbeddingService openAiEmbeddingService;
  private final PineconeService pineconeService;
  private final EmbeddingsRepository embeddingsRepository;

  public Embedding createEmbedding(Embedding requestedEmbedding) {
    log.info("Creating new embedding: {}", requestedEmbedding);
    List<Double> doubleValues =
        openAiEmbeddingService.createEmbedding(requestedEmbedding.getContent());
    List<Float> floatList = convertToFloat(doubleValues);
    String vectorId = pineconeService.upsert(floatList);
    Embedding newEmbedding =
        Embedding.builder()
            .content(requestedEmbedding.getContent())
            .vectorId(vectorId)
            .type(requestedEmbedding.getType())
            .build();

    Embedding createdEmbedding = embeddingsRepository.save(newEmbedding);
    log.info("New embedding created: {}", createdEmbedding);
    return createdEmbedding;
  }

  public List<String> search(Embedding searchedEmbedding) {
    log.info("Searching for embeddings by: {}", searchedEmbedding);

    List<Double> embedding = openAiEmbeddingService.createEmbedding(searchedEmbedding.getContent());
    List<Float> floatList = convertToFloat(embedding);
    SingleQueryResults vectors = pineconeService.search(floatList);
    List<String> vectorsIds =
        vectors.getMatchesList().stream().map(ScoredVector::getId).collect(Collectors.toList());
    log.info("Found vectors: {}", vectorsIds);

    List<String> embeddings =
        vectorsIds.stream()
            .map(embeddingsRepository::findOneByVectorId)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(Embedding::getContent)
            .collect(Collectors.toList());
    log.info("Found embeddings: {}", embeddings);
    return embeddings;
  }

  @NotNull
  private static List<Float> convertToFloat(List<Double> doubleValues) {
    return doubleValues.stream().map(Double::floatValue).collect(Collectors.toList());
  }
}
