package com.cydercode.gptbridge.openai.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenAiConfiguration {

  private final OpenAiProperties openAiProperties;

  @Bean
  public OpenAiService openAiService() {
    return new OpenAiService(openAiProperties.getToken());
  }
}
