package com.cydercode.gptbridge.pinecone;

import io.pinecone.PineconeClient;
import io.pinecone.PineconeConnection;
import io.pinecone.PineconeConnectionConfig;
import io.pinecone.proto.QueryRequest;
import io.pinecone.proto.QueryResponse;
import io.pinecone.proto.QueryVector;
import io.pinecone.proto.SingleQueryResults;
import io.pinecone.proto.UpsertRequest;
import io.pinecone.proto.UpsertResponse;
import io.pinecone.proto.Vector;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PineconeService {

  private final PineconeProperties pineconeProperties;
  private final PineconeClient pineconeClient;
  private final PineconeConnectionConfig connectionConfig;

  public String upsert(List<Float> values) {
    String id = UUID.randomUUID().toString();
    log.info("Inserting new vector with id: {}", id);
    try (PineconeConnection connection = pineconeClient.connect(connectionConfig)) {
      Vector v1 = Vector.newBuilder().setId(id).addAllValues(values).build();

      UpsertRequest upsertRequest =
          UpsertRequest.newBuilder()
              .addVectors(v1)
              .setNamespace(pineconeProperties.getNamespace())
              .build();

      UpsertResponse upsertResponse = connection.getBlockingStub().upsert(upsertRequest);
      log.info("Inserted new vector with id: {}", id);
      log.info("Upsert response: {}", upsertResponse);
    }

    return id;
  }

  public SingleQueryResults search(List<Float> values) {
    log.info("Querying for vector");
    try (PineconeConnection connection = pineconeClient.connect(connectionConfig)) {
      QueryVector queryVector =
          QueryVector.newBuilder()
              .setNamespace(pineconeProperties.getNamespace())
              .addAllValues(values)
              .build();

      QueryRequest queryRequest =
          QueryRequest.newBuilder().addQueries(queryVector).setTopK(5).build();

      QueryResponse response = connection.getBlockingStub().query(queryRequest);
      log.info("Query response: {}", response);
      return response.getResultsList().get(0);
    }
  }
}
