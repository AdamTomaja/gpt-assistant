package com.cydercode.gptbridge.openai.config;

import static com.theokanning.openai.service.OpenAiService.defaultClient;
import static com.theokanning.openai.service.OpenAiService.defaultObjectMapper;
import static com.theokanning.openai.service.OpenAiService.defaultRetrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.OpenAiApi;
import com.theokanning.openai.service.OpenAiService;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;

@Configuration
@RequiredArgsConstructor
public class OpenAiConfiguration {

  private final OpenAiProperties openAiProperties;

  @Bean
  public OpenAiService openAiService() {
    ObjectMapper objectMapper = defaultObjectMapper();
    OkHttpClient client = defaultClient(openAiProperties.getToken(), Duration.ofSeconds(30L));
    Retrofit retrofit = defaultRetrofit(client, objectMapper);
    OpenAiApi api = retrofit.create(OpenAiApi.class);
    return new OpenAiService(api);
  }
}
