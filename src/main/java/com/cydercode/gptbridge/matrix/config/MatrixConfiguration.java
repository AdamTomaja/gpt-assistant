package com.cydercode.gptbridge.matrix.config;

import io.github.ma1uta.matrix.client.StandaloneClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MatrixConfiguration {

  private final MatrixProperties matrixProperties;

  @Bean
  public StandaloneClient matrixStandaloneClient() {
    return new StandaloneClient.Builder().domain(matrixProperties.getDomain()).build();
  }
}
