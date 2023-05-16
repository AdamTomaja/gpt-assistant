package com.cydercode.gptbridge.openai.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class OpenAiProperties {

  @Value("${openai.token}")
  private String token;

  @Value("${openai.model}")
  private String model;

  @Value("${openai.temperature}")
  private Double temperature;

  @Value("${openai.embeddings-model}")
  private String embeddingsModel;
}
