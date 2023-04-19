package com.cydercode.gptbridge.assistant.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AssistantProperties {

    @Value("${assistant.initial-sync}")
    private Boolean initialSync;
}
