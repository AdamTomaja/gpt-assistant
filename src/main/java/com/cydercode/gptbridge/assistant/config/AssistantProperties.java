package com.cydercode.gptbridge.assistant.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AssistantProperties {

  @Value("${assistant.initial-sync}")
  private Boolean initialSync;

  @Value("${assistant.system-prompt}")
  private String systemPrompt;

  @Value("${assistant.conversation-length}")
  private int conversationLength;
}
