package com.cydercode.gptbridge.assistant;

import com.cydercode.gptbridge.assistant.config.AssistantProperties;
import com.cydercode.gptbridge.assistant.intention.Intention;
import com.cydercode.gptbridge.assistant.intention.IntentionService;
import com.cydercode.gptbridge.assistant.model.Message;
import com.cydercode.gptbridge.assistant.model.MessagesRepository;
import com.cydercode.gptbridge.matrix.MatrixSendService;
import com.cydercode.gptbridge.openai.GptService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistantService {
  private final MessagesRepository repository;
  private final AssistantProperties assistantProperties;
  private final MatrixSendService matrixSendService;
  private final GptService gptService;
  private final ConversationService conversationService;

  private final MemoryService memoryService;
  private final UsersService usersService;

  private final IntentionService intentionService;

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

    if (usersService.isUserMessage(senderId)) {
      Intention intention = intentionService.getIntention(message);
      if (intention == Intention.STATEMENT) {
        memoryService.saveMemory(message);
      }
      log.info("Message from user: {}", message);
      var conversation = conversationService.buildConversation(intention);
      log.info("Conversion: {}", conversation);
      var response = gptService.complete(conversation);
      matrixSendService.sendMessage(response);
    }
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
