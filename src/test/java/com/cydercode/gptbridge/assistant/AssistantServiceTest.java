package com.cydercode.gptbridge.assistant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.cydercode.gptbridge.assistant.config.AssistantProperties;
import com.cydercode.gptbridge.assistant.model.AssistedMessage;
import com.cydercode.gptbridge.assistant.model.AssistedMessagesRepository;
import com.cydercode.gptbridge.matrix.MatrixSendService;
import com.cydercode.gptbridge.openai.GptService;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AssistantServiceTest {

  @Mock AssistedMessagesRepository repository;

  @Mock AssistantProperties properties;

  @Mock MatrixSendService matrixSendService;

  @Mock GptService gptService;

  @Test
  void shouldSkipMessageAlreadyAssisted() {
    // given
    AssistantService assistantService = createService();

    when(repository.countAllByEventId(Mockito.anyString())).thenReturn(1);

    // when
    assistantService.handleMessage("", "", "");

    // then
    verify(repository).countAllByEventId("");
    verifyNoMoreInteractions(repository);
    verifyNoInteractions(matrixSendService);
  }

  @Test
  void shouldSaveMessageOnInitialSync() {
    // given
    AssistantService assistantService = createService();

    when(repository.countAllByEventId(anyString())).thenReturn(0);
    when(properties.getInitialSync()).thenReturn(true);

    ArgumentCaptor<AssistedMessage> captor = ArgumentCaptor.forClass(AssistedMessage.class);

    // when
    assistantService.handleMessage("ev", "se", "me");

    // then
    verify(repository).save(captor.capture());
    AssistedMessage message = captor.getValue();
    assertThat(message.getInitialSync()).isTrue();
    assertThat(message.getRequest()).isEqualTo("me");
    assertThat(message.getResponse()).isNull();
    assertThat(message.getSenderId()).isEqualTo("se");
    assertThat(message.getEventId()).isEqualTo("ev");
  }

  @Test
  void shouldNotProcessMessageWhenInitialSyncOn() {
    // when
    AssistantService assistantService = createService();

    when(repository.countAllByEventId(anyString())).thenReturn(0);
    when(properties.getInitialSync()).thenReturn(true);

    ArgumentCaptor<AssistedMessage> captor = ArgumentCaptor.forClass(AssistedMessage.class);

    // when
    assistantService.handleMessage("ev", "se", "me");

    // then
    verifyNoInteractions(matrixSendService);
  }

  @NotNull
  private AssistantService createService() {
    return new AssistantService(repository, properties, matrixSendService, gptService);
  }
}
