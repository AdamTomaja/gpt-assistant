package com.cydercode.gptbridge.embeddings;

import io.pinecone.proto.ScoredVector;

public record QueryEmbedding(Embedding embedding, ScoredVector vector) {

  public float getScore() {
    return vector.getScore();
  }
}
