package com.cydercode.gptbridge.openai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

import com.cydercode.gptbridge.openai.config.OpenAiProperties;
import com.theokanning.openai.completion.chat.*;
import com.theokanning.openai.service.OpenAiService;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GptServiceTest {

  @Mock OpenAiService openAiService;

  @Mock OpenAiProperties properties;

  @Test
  void shouldReturnOpenAiCompletion() {
    // given
    GptService gptService = createService();
    String message = "Hello Alex!";
    String completion = "Hi, im virtual assistant";

    when(openAiService.createChatCompletion(any())).thenReturn(buildResult(completion));

    // when
    String result = gptService.complete(message);

    // then
    assertThat(result).isEqualTo(completion);
  }

  @Test
  void shouldRequestCompletionWithConfiguredPromptAndMessage() {
    // given
    GptService gptService = createService();
    String message = "Hello Alex!";
    String prompt = "You are Alex, virtual assistent";
    ArgumentCaptor<ChatCompletionRequest> requestArgumentCaptor =
        ArgumentCaptor.forClass(ChatCompletionRequest.class);

    when(properties.getPrompt()).thenReturn(prompt);
    when(openAiService.createChatCompletion(any())).thenReturn(buildResult("response"));

    // when
    gptService.complete(message);

    // then
    verify(openAiService).createChatCompletion(requestArgumentCaptor.capture());
    ChatCompletionRequest request = requestArgumentCaptor.getValue();

    // prompt
    assertThat(request.getMessages().get(0).getContent()).isEqualTo(prompt);
    assertThat(request.getMessages().get(0).getRole()).isEqualTo(ChatMessageRole.SYSTEM.value());

    // message
    assertThat(request.getMessages().get(1).getContent()).isEqualTo(message);
    assertThat(request.getMessages().get(1).getRole()).isEqualTo(ChatMessageRole.USER.value());
  }

  @Test
  void shouldConcatChoices() {
    // given
    GptService gptService = createService();
    String message = "Hello Alex!";
    String completionA = "Hi, im virtual assistant";
    String completionB = "Im virtual assistant Alex!";

    when(openAiService.createChatCompletion(any()))
        .thenReturn(buildResult(completionA, completionB));

    // when
    String result = gptService.complete(message);

    // then
    assertThat(result).isEqualTo(String.format("%s,%s", completionA, completionB));
  }

  private GptService createService() {
    return new GptService(openAiService, properties);
  }

  private static ChatCompletionResult buildResult(String... completions) {
    ChatCompletionResult completionResult = new ChatCompletionResult();

    List<ChatCompletionChoice> choices =
        Arrays.asList(completions).stream()
            .map(c -> new ChatMessage(ChatMessageRole.ASSISTANT.value(), c))
            .map(
                m ->
                    new ChatCompletionChoice() {
                      {
                        setMessage(m);
                      }
                    })
            .collect(Collectors.toList());

    completionResult.setChoices(choices);
    return completionResult;
  }
}
