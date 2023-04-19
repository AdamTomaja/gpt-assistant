package com.cydercode.gptbridge.assistant;

import com.cydercode.gptbridge.assistant.config.AssistantProperties;
import com.cydercode.gptbridge.assistant.model.AssistedMessage;
import com.cydercode.gptbridge.assistant.model.AssistedMessagesRepository;
import com.cydercode.gptbridge.matrix.MatrixSendService;
import com.cydercode.gptbridge.matrix.MatrixSyncService;
import com.cydercode.gptbridge.openai.GptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AssistantService {
    private final AssistedMessagesRepository repository;
    private final AssistantProperties assistantProperties;
    private final MatrixSendService matrixSendService;
    private final GptService gptService;

    public void handleMessage(String eventId, String senderId, String message) {
        if(repository.countAllByEventId(eventId) > 0) {
            log.info("Ignoring already handled message, eventId: [{}]", eventId);
            return;
        }

        if(assistantProperties.getInitialSync()) {
            saveAsInitialSync(eventId, senderId, message);
            return;
        }

        assist(eventId, senderId, message);
    }

    private void assist(String eventId, String senderId, String message) {
        String completion = gptService.complete(message);
        matrixSendService.sendMessage(completion);

        saveMessage(AssistedMessage.builder()
                .response(completion)
                .request(message)
                .initialSync(false)
                .senderId(senderId)
                .eventId(eventId)
                .build());
    }

    private void saveAsInitialSync(String eventId, String senderId, String message) {
        AssistedMessage assistedMessage = AssistedMessage.builder()
                .eventId(eventId)
                .senderId(senderId)
                .request(message)
                .initialSync(true)
                .build();
        saveMessage(assistedMessage);
    }

    private AssistedMessage saveMessage(AssistedMessage assistedMessage) {
        log.info("Saving new message: [{}]", assistedMessage);
        return repository.save(assistedMessage);
    }
}
