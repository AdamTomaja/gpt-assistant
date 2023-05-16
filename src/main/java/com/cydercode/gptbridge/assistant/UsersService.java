package com.cydercode.gptbridge.assistant;

import io.github.ma1uta.matrix.client.StandaloneClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsersService {
  private final StandaloneClient standaloneClient;

  public boolean isUserMessage(String senderId) {
    return !senderId.equals(standaloneClient.getUserId());
  }
}
