package com.cydercode.gptbridge.assistant;

import com.cydercode.gptbridge.assistant.config.AssistantProperties;
import com.cydercode.gptbridge.assistant.model.Message;
import com.cydercode.gptbridge.assistant.model.MessagesRepository;
import com.cydercode.gptbridge.matrix.MatrixSendService;
import com.cydercode.gptbridge.openai.GptService;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import io.github.ma1uta.matrix.client.StandaloneClient;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistantService {
  private final MessagesRepository repository;
  private final AssistantProperties assistantProperties;
  private final StandaloneClient standaloneClient;

  private final MatrixSendService matrixSendService;
  private final GptService gptService;

  public void handleMessage(String eventId, String senderId, String message) {
    if (repository.countAllByEventId(eventId) > 0) {
      log.info("Ignoring already handled message, eventId: [{}]", eventId);
      return;
    }

    takeMessage(eventId, senderId, message);
    if (assistantProperties.getInitialSync()) {
      log.info("Skipping further processing (initial sync enabled)");
      return;
    }

    if (isUserMessage(senderId)) {
      log.info("Message from user: {}", message);
      var conversation = buildConversation();
      log.info("Conversion: {}", conversation);
      var response = gptService.complete(conversation);
      matrixSendService.sendMessage(response);
    }
  }

  private List<ChatMessage> buildConversation() {
    var messages = repository.findTop20ByOrderByCreatedAtDesc();
    Collections.reverse(messages);
    return messages.stream().map(this::buildChatMessage).toList();
  }

  private ChatMessage buildChatMessage(Message message) {
    var role =
        isUserMessage(message.getSenderId()) ? ChatMessageRole.USER : ChatMessageRole.ASSISTANT;
    return new ChatMessage(role.value(), message.getContent());
  }

  private boolean isUserMessage(String senderId) {
    return !senderId.equals(standaloneClient.getUserId());
  }

  private void takeMessage(String eventId, String senderId, String message) {
    takeMessage(
        Message.builder()
            .content(message)
            .senderId(senderId)
            .eventId(eventId)
            .createdAt(LocalDateTime.now())
            .build());
  }

  private Message takeMessage(Message assistedMessage) {
    log.info("Saving new message: [{}]", assistedMessage);
    return repository.save(assistedMessage);
  }
}
