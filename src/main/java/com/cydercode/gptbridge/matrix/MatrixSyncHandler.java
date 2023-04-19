package com.cydercode.gptbridge.matrix;

import com.cydercode.gptbridge.assistant.AssistantService;
import com.cydercode.gptbridge.matrix.config.MatrixProperties;
import io.github.ma1uta.matrix.client.StandaloneClient;
import io.github.ma1uta.matrix.client.model.sync.JoinedRoom;
import io.github.ma1uta.matrix.client.model.sync.Rooms;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import io.github.ma1uta.matrix.event.RoomMessage;
import io.github.ma1uta.matrix.event.content.EventContent;
import io.github.ma1uta.matrix.event.message.Text;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MatrixSyncHandler {

  private final MatrixProperties matrixProperties;
  private final AssistantService assistantService;
  private final StandaloneClient standaloneClient;

  public void handleSync(SyncResponse syncResponse) {
    log.info("Sync received: {}", syncResponse);
    handleNullable(syncResponse.getRooms(), this::handleRooms);
  }

  private void handleRooms(Rooms rooms) {
    handleNullable(rooms.getJoin(), this::handleJoin);
  }

  private void handleJoin(Map<String, JoinedRoom> joined) {
    handleNullable(joined.get(matrixProperties.getRoomId()), this::handleRoom);
  }

  private void handleRoom(JoinedRoom room) {
    room.getTimeline().getEvents().stream()
        .filter(RoomMessage.class::isInstance)
        .map(RoomMessage.class::cast)
        .forEach(this::handleRoomMessage);
  }

  private void handleRoomMessage(RoomMessage<?> roomMessage) {
    if (roomMessage.getSender().equals(standaloneClient.getUserId())) {
      log.info("Ignoring my own message: eventId: [{}]", roomMessage.getEventId());
      return;
    }

    EventContent content = roomMessage.getContent();
    if (content instanceof Text textContent) {
      log.info(
          "Text content received: [{}] from: [{}], evId: [{}]",
          textContent.getBody(),
          roomMessage.getSender(),
          roomMessage.getEventId());
      assistantService.handleMessage(
          roomMessage.getEventId(), roomMessage.getSender(), textContent.getBody());
    }
  }

  private <T> void handleNullable(T obj, Consumer<T> consumer) {
    Optional.ofNullable(obj).ifPresent(consumer);
  }
}
