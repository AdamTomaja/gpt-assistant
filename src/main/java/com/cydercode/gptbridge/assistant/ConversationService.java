package com.cydercode.gptbridge.assistant;

import com.cydercode.gptbridge.assistant.config.AssistantProperties;
import com.cydercode.gptbridge.assistant.intention.Intention;
import com.cydercode.gptbridge.assistant.model.Message;
import com.cydercode.gptbridge.assistant.model.MessagesRepository;
import com.cydercode.gptbridge.embeddings.EmbeddingService;
import com.cydercode.gptbridge.embeddings.QueryEmbedding;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConversationService {
  private final MessagesRepository repository;
  private final AssistantProperties assistantProperties;
  private final UsersService usersService;

  private final EmbeddingService embeddingService;

  public List<ChatMessage> buildConversation(Intention intention) {
    var conversation =
        new ArrayList<>(
            findLastMessages().getContent().stream().map(this::buildChatMessage).toList());
    if (intention == Intention.QUESTION) {
      conversation.addAll(getMemories(conversation.get(0)));
    }
    conversation.add(
        new ChatMessage(ChatMessageRole.ASSISTANT.value(), assistantProperties.getSystemPrompt()));
    Collections.reverse(conversation);
    return conversation;
  }

  private List<ChatMessage> getMemories(ChatMessage chatMessage) {
    String message = chatMessage.getContent();
    List<QueryEmbedding> embeddings = embeddingService.search(message);
    return embeddings.stream().map(this::createMemoryChatMessage).toList();
  }

  private ChatMessage createMemoryChatMessage(QueryEmbedding queryEmbedding) {
    return new ChatMessage(ChatMessageRole.USER.value(), queryEmbedding.embedding().getContent());
  }

  private Page<Message> findLastMessages() {
    return repository.findAll(
        PageRequest.of(
            0, assistantProperties.getConversationLength(), Sort.by(Sort.Order.desc("createdAt"))));
  }

  private ChatMessage buildChatMessage(Message message) {
    var role =
        usersService.isUserMessage(message.getSenderId())
            ? ChatMessageRole.USER
            : ChatMessageRole.ASSISTANT;
    return new ChatMessage(role.value(), message.getContent());
  }
}
