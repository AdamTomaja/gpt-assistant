package com.cydercode.gptbridge.matrix;

import com.cydercode.gptbridge.matrix.config.MatrixProperties;
import io.github.ma1uta.matrix.client.StandaloneClient;
import io.github.ma1uta.matrix.client.model.auth.LoginResponse;
import io.github.ma1uta.matrix.client.model.sync.SyncResponse;
import io.github.ma1uta.matrix.client.sync.SyncLoop;
import io.github.ma1uta.matrix.client.sync.SyncParams;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MatrixSyncService {

  private final ExecutorService executorService = Executors.newFixedThreadPool(1);
  private final StandaloneClient matrixClient;
  private final MatrixProperties matrixProperties;
  private final MatrixSyncHandler matrixEventHandler;

  @PostConstruct
  private void login() {
    LoginResponse loginResponse =
        matrixClient
            .auth()
            .login(matrixProperties.getUsername(), matrixProperties.getPassword().toCharArray());
    log.info("Matrix auth successful: {}", loginResponse.toString());
    runSyncLoop();
  }

  private void runSyncLoop() {
    SyncLoop syncLoop = new SyncLoop(matrixClient.sync(), this::syncCallback);
    SyncParams syncParams =
        SyncParams.builder()
            .timeout(matrixProperties.getTimeout().toMillis())
            .fullState(true)
            .build();
    syncLoop.setInit(syncParams);
    executorService.submit(syncLoop);
  }

  private void syncCallback(SyncResponse syncResponse, SyncParams syncParams) {
    log.info("Sync finished");
    matrixEventHandler.handleSync(syncResponse);
    syncParams.setFullState(false);
  }
}
