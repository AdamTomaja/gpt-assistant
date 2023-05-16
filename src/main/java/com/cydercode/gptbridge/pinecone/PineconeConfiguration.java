package com.cydercode.gptbridge.pinecone;

import io.pinecone.PineconeClient;
import io.pinecone.PineconeClientConfig;
import io.pinecone.PineconeConnectionConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PineconeConfiguration {

  private final PineconeProperties pineconeProperties;

  @Bean
  public PineconeClientConfig setPineconeProperties() {
    return new PineconeClientConfig()
        .withApiKey(pineconeProperties.getToken())
        .withEnvironment(pineconeProperties.getEnvironment())
        .withProjectName(pineconeProperties.getProjectId());
  }

  @Bean
  public PineconeClient pineconeClient(PineconeClientConfig pineconeClientConfig) {
    return new PineconeClient(pineconeClientConfig);
  }

  @Bean
  public PineconeConnectionConfig pineconeConnectionConfig() {
    return new PineconeConnectionConfig().withIndexName(pineconeProperties.getIndexName());
  }
}
