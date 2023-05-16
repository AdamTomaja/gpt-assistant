package com.cydercode.gptbridge.embeddings;

import com.cydercode.gptbridge.openai.OpenAiEmbeddingService;
import com.cydercode.gptbridge.pinecone.PineconeService;
import io.pinecone.proto.ScoredVector;
import io.pinecone.proto.SingleQueryResults;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    Embedding newEmbedding = requestedEmbedding.toBuilder().vectorId(vectorId).build();
    Embedding createdEmbedding = embeddingsRepository.save(newEmbedding);
    log.info("New embedding created: {}", createdEmbedding);
    return createdEmbedding;
  }

  public List<QueryEmbedding> search(String message) {
    return search(Embedding.builder().content(message).build());
  }

  public List<QueryEmbedding> search(Embedding searchedEmbedding) {
    log.info("Searching for embeddings by: {}", searchedEmbedding);

    List<Double> embedding = openAiEmbeddingService.createEmbedding(searchedEmbedding.getContent());
    List<Float> floatList = convertToFloat(embedding);

    SingleQueryResults vectors = pineconeService.search(floatList);
    List<String> vectorsIds = getVectorIds(vectors);
    log.info("Found vectors: {}", vectorsIds);
    return mergeData(vectors);
  }

  private List<QueryEmbedding> mergeData(SingleQueryResults vectors) {
    List<String> vectorsIds = getVectorIds(vectors);
    List<Embedding> embeddings = embeddingsRepository.findByVectorIds(vectorsIds);
    log.info("Found embeddings: {}", embeddings);

    return embeddings.stream()
        .map(em -> new QueryEmbedding(em, matchVector(em.getVectorId(), vectors)))
        .sorted(Comparator.comparingDouble(QueryEmbedding::getScore).reversed())
        .collect(Collectors.toList());
  }

  private ScoredVector matchVector(String vectorId, SingleQueryResults vectors) {
    return vectors.getMatchesList().stream()
        .filter(vector -> vector.getId().equals(vectorId))
        .findFirst()
        .orElseThrow();
  }

  private List<String> getVectorIds(SingleQueryResults vectors) {
    return vectors.getMatchesList().stream().map(ScoredVector::getId).collect(Collectors.toList());
  }

  private static List<Float> convertToFloat(List<Double> doubleValues) {
    return doubleValues.stream().map(Double::floatValue).collect(Collectors.toList());
  }
}
