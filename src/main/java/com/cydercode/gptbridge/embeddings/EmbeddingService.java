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
    Embedding newEmbedding = requestedEmbedding.toBuilder().vectorId(vectorId).build();
    Embedding createdEmbedding = embeddingsRepository.save(newEmbedding);
    log.info("New embedding created: {}", createdEmbedding);
    return createdEmbedding;
  }

  public List<Embedding> search(Embedding searchedEmbedding) {
    log.info("Searching for embeddings by: {}", searchedEmbedding);

    List<Double> embedding = openAiEmbeddingService.createEmbedding(searchedEmbedding.getContent());
    List<Float> floatList = convertToFloat(embedding);
    SingleQueryResults vectors = pineconeService.search(floatList);
    List<String> vectorsIds = getVectorIds(vectors);
    log.info("Found vectors: {}", vectorsIds);

    List<Embedding> embeddings = getSortedEmbeddings(vectors, vectorsIds);
    log.info("Found embeddings: {}", embeddings);

    return embeddings;
  }

  private List<String> getVectorIds(SingleQueryResults vectors) {
    return vectors.getMatchesList().stream().map(ScoredVector::getId).collect(Collectors.toList());
  }

  private List<Embedding> getSortedEmbeddings(SingleQueryResults vectors, List<String> vectorsIds) {
    Comparator<Embedding> comparator = createEmbeddingComparator(vectors);

    return embeddingsRepository.findByVectorIds(vectorsIds).stream()
        .sorted(comparator.reversed())
        .collect(Collectors.toList());
  }

  private Comparator<Embedding> createEmbeddingComparator(SingleQueryResults vectors) {
    return Comparator.comparingDouble(
        emb -> {
          ScoredVector scoredVector =
              vectors.getMatchesList().stream()
                  .filter(vector -> vector.getId().equals(emb.getVectorId()))
                  .findFirst()
                  .orElse(null);
          return scoredVector != null ? scoredVector.getScore() : 0.0;
        });
  }

  @NotNull
  private static List<Float> convertToFloat(List<Double> doubleValues) {
    return doubleValues.stream().map(Double::floatValue).collect(Collectors.toList());
  }
}
