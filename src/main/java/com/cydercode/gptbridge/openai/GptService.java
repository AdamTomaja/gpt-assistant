package com.cydercode.gptbridge.openai;

import com.cydercode.gptbridge.embeddings.Embedding;
import com.cydercode.gptbridge.embeddings.EmbeddingService;
import com.cydercode.gptbridge.openai.config.OpenAiProperties;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GptService {

  private final OpenAiService openAiService;
  private final OpenAiProperties openAiProperties;

  private final EmbeddingService embeddingService;

  public String complete(String message) {
    ChatCompletionRequest completionRequest = buildRequest(message);
    log.info("Request to gpt: {}", completionRequest);
    String response =
        prepareResponse(openAiService.createChatCompletion(completionRequest).getChoices());
    createMemoryEmbedding(message, response);
    return response;
  }

  private void createMemoryEmbedding(String message, String response) {
    embeddingService.createEmbedding(
        Embedding.builder()
            .content(String.format("Adam powiedział: %s| Ava odpowiedziała: %s", message, response))
            .type(Embedding.EmbeddingType.MEMORY)
            .build());
  }

  private ChatCompletionRequest buildRequest(String message) {
    List<Embedding> notes = getEmbeddings(message);
    String notesDb = buildTypeDb(notes, Embedding.EmbeddingType.NOTE);
    String systemPrompt = openAiProperties.getPrompt().replace("<notes-database>", notesDb);

    String memoriesDb = buildTypeDb(notes, Embedding.EmbeddingType.MEMORY);
    systemPrompt = systemPrompt.replace("<memories-database>", memoriesDb);

    return ChatCompletionRequest.builder()
        .messages(
            Arrays.asList(
                new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt),
                new ChatMessage(ChatMessageRole.USER.value(), message)))
        .temperature(openAiProperties.getTemperature())
        .model(openAiProperties.getModel())
        .build();
  }

  private String buildTypeDb(List<Embedding> notes, Embedding.EmbeddingType type) {
    StringBuilder stringBuilder = new StringBuilder();
    notes.stream()
        .filter(n -> n.getType().equals(type))
        .forEach(n -> stringBuilder.append(n.getContent()).append("\n"));
    return stringBuilder.toString();
  }

  @NotNull
  private List<Embedding> getEmbeddings(String message) {
    Embedding request = Embedding.builder().content(message).build();
    return embeddingService.search(request);
  }

  @NotNull
  private static String prepareResponse(List<ChatCompletionChoice> choices) {
    return choices.stream()
        .map(ChatCompletionChoice::getMessage)
        .map(ChatMessage::getContent)
        .collect(Collectors.joining(","));
  }
}
