package com.cydercode.gptbridge.openai;

import com.cydercode.gptbridge.openai.config.OpenAiProperties;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
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

  public String complete(List<ChatMessage> messages) {
    ChatCompletionRequest completionRequest = buildRequest(messages);
    log.info("Sending chat-completion request: [{}]", completionRequest);
    String response =
        prepareResponse(openAiService.createChatCompletion(completionRequest).getChoices());
    log.info("Chat-completion response: [{}]", response);
    return response;
  }

  private ChatCompletionRequest buildRequest(List<ChatMessage> messages) {
    return ChatCompletionRequest.builder()
        .messages(messages)
        .temperature(openAiProperties.getTemperature())
        .model(openAiProperties.getModel())
        .build();
  }

  @NotNull
  private static String prepareResponse(List<ChatCompletionChoice> choices) {
    return choices.stream()
        .map(ChatCompletionChoice::getMessage)
        .map(ChatMessage::getContent)
        .collect(Collectors.joining(","));
  }
}
