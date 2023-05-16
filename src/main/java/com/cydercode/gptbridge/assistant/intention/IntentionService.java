package com.cydercode.gptbridge.assistant.intention;

import com.cydercode.gptbridge.openai.GptService;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntentionService {
  private final String PROMPT =
      """
                Act as intention specialist. Every message from user assign to one of the following categories:
                GREETING,
                QUESTION,
                STATEMENT,
                UNKNOWN

                Answer with only category name uppercase and nothing else. No additional comments.
                """;

  private final GptService gptService;

  public Intention getIntention(String message) {
    List<ChatMessage> messages = new ArrayList<>();
    messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), PROMPT));
    messages.add(new ChatMessage(ChatMessageRole.USER.value(), message));
    String response = gptService.complete(messages);
    log.info("Intention response: {}", response);
    return Intention.valueOf(response.toUpperCase().replace(".", ""));
  }
}
