package com.cydercode.gptbridge.matrix;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cydercode.gptbridge.matrix.config.MatrixProperties;
import io.github.ma1uta.matrix.client.StandaloneClient;
import io.github.ma1uta.matrix.client.methods.blocked.EventMethods;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MatrixSendServiceTest {

  @Mock StandaloneClient standaloneClient;

  @Mock MatrixProperties matrixProperties;

  @Test
  void shouldSendMessageToConfiguredRoom() {
    // given
    String message = "message";
    String roomId = "room-id";
    MatrixSendService matrixSendService = buildService();

    when(matrixProperties.getRoomId()).thenReturn(roomId);
    EventMethods eventMethod = Mockito.mock(EventMethods.class);
    when(standaloneClient.event()).thenReturn(eventMethod);

    // when
    matrixSendService.sendMessage(message);

    // then
    verify(eventMethod).sendMessage(roomId, message);
  }

  @NotNull
  private MatrixSendService buildService() {
    return new MatrixSendService(standaloneClient, matrixProperties);
  }
}
