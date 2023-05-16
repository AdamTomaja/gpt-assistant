package com.cydercode.gptbridge.pinecone;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class PineconeProperties {

  @Value("${pinecone.environment}")
  private String environment;

  @Value("${pinecone.namespace}")
  private String namespace;

  @Value("${pinecone.token}")
  private String token;

  @Value("${pinecone.projectId}")
  private String projectId;

  @Value("${pinecone.indexName}")
  private String indexName;
}
