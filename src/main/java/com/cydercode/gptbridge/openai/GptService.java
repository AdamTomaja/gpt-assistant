package com.cydercode.gptbridge.openai;

import com.cydercode.gptbridge.openai.config.OpenAiProperties;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
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

  public String complete(String message) {
    ChatCompletionRequest completionRequest = buildRequest(message);
    return prepareResponse(openAiService.createChatCompletion(completionRequest).getChoices());
  }

  private ChatCompletionRequest buildRequest(String message) {
    return ChatCompletionRequest.builder()
            .messages(
                Arrays.asList(
                    new ChatMessage(ChatMessageRole.SYSTEM.value(), openAiProperties.getPrompt()),
                    new ChatMessage(ChatMessageRole.USER.value(), message)))
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
