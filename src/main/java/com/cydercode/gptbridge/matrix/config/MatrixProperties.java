package com.cydercode.gptbridge.matrix.config;

import java.time.Duration;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class MatrixProperties {

  @Value("${matrix.domain}")
  private String domain;

  @Value("${matrix.username}")
  private String username;

  @Value("${matrix.password}")
  private String password;

  @Value("${matrix.roomId}")
  private String roomId;

  @Value("${matrix.timeout}")
  private Duration timeout;
}
