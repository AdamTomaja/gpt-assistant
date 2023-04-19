package com.cydercode.gptbridge.matrix;

import static org.mockito.Mockito.*;

import com.cydercode.gptbridge.assistant.AssistantService;
import com.cydercode.gptbridge.matrix.config.MatrixProperties;
import io.github.ma1uta.matrix.client.StandaloneClient;
import io.github.ma1uta.matrix.client.model.sync.JoinedRoom;
import io.github.ma1uta.matrix.client.model.sync.Rooms;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import io.github.ma1uta.matrix.client.model.sync.Timeline;
import io.github.ma1uta.matrix.event.RoomMessage;
import io.github.ma1uta.matrix.event.message.Text;
import java.util.Arrays;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatrixSyncHandlerTest {

  @Mock MatrixProperties matrixProperties;

  @Mock AssistantService assistantService;

  @Mock StandaloneClient standaloneClient;

  @Test
  void shouldNotProcessMessagesSentByTheAssistant() {
    // given
    String userId = "assistant";
    String roomId = "roomId";
    MatrixSyncHandler handler = createHandler();
    when(standaloneClient.getUserId()).thenReturn(userId);
    when(matrixProperties.getRoomId()).thenReturn(roomId);
    SyncResponse syncResponse = buildSyncResponse(userId, roomId);

    // when
    handler.handleSync(syncResponse);

    // then
    verifyNoInteractions(assistantService);
  }

  @Test
  void shouldProcessMessagesSentByNotAssistant() {
    // given
    String roomId = "roomId";
    MatrixSyncHandler handler = createHandler();
    when(standaloneClient.getUserId()).thenReturn("assistant");
    when(matrixProperties.getRoomId()).thenReturn(roomId);
    SyncResponse syncResponse = buildSyncResponse("some-user", roomId);

    // when
    handler.handleSync(syncResponse);

    // then
    verify(assistantService).handleMessage("event-id", "some-user", "Message");
  }

  private static SyncResponse buildSyncResponse(String userId, String roomId) {
    SyncResponse syncResponse = new SyncResponse();
    JoinedRoom room = new JoinedRoom();
    Timeline timeline = new Timeline();
    RoomMessage<Text> roomMessage = new RoomMessage<>();
    roomMessage.setEventId("event-id");

    roomMessage.setSender(userId);
    Text text = new Text();
    text.setBody("Message");
    roomMessage.setContent(text);

    timeline.setEvents(Arrays.asList(roomMessage));

    room.setTimeline(timeline);
    Rooms rooms = new Rooms();
    rooms.setJoin(new HashMap<>());
    rooms.getJoin().put(roomId, room);
    syncResponse.setRooms(rooms);
    return syncResponse;
  }

  private MatrixSyncHandler createHandler() {
    return new MatrixSyncHandler(matrixProperties, assistantService, standaloneClient);
  }
}
