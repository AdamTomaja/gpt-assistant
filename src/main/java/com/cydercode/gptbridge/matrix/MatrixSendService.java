package com.cydercode.gptbridge.matrix;

import com.cydercode.gptbridge.matrix.config.MatrixProperties;
import io.github.ma1uta.matrix.client.StandaloneClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatrixSendService {
  private final StandaloneClient matrixClient;
  private final MatrixProperties matrixProperties;

  public void sendMessage(String message) {
    matrixClient.event().sendMessage(matrixProperties.getRoomId(), message);
  }
}
