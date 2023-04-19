```mermaid
sequenceDiagram
    actor User
    User->>Facebook: Request
    Facebook-->>Mau-Synapse: Request
    Mau-Synapse-->>GPT-Assistant: Request
    GPT-Assistant->>OpenAI: ChatCompletionRequest
        Note right of GPT-Assistant: Store response in DB

    OpenAI->>GPT-Assistant: ChatCompletionResponse
    GPT-Assistant-->>Mau-Synapse: Response
    Mau-Synapse-->>Facebook: Response
    Facebook->>User: Response
```