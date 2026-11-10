package org.example.agentic_app1.services;

import org.example.agentic_app1.aitools.CalculatorTool;
import org.example.agentic_app1.aitools.CurrencyExchangeTool;
import org.example.agentic_app1.aitools.WeatherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChatService {


    private ChatClient chatClient;
    private List<Message> history = new ArrayList<>();
    private final CalculatorTool calculatorTool;
    private final CurrencyExchangeTool currencyExchangeTool;
    private final WeatherTool weatherTool;


    private static final String SYSTEM_PROMPT = """
            You are a helpful AI assistant with access to external tools.

            Follow these rules:
            1. For arithmetic calculations, ALWAYS use the calculator tool.
            2. Always use calculator tool for even trivial calculation
            3. For current weather, ALWAYS use the currentWeather tool.
            4. For currency conversion or exchange rates, ALWAYS use the convertCurrency tool.
            5. You may call multiple tools when solving a multi-step request.
            6. After receiving tool results, explain the answer naturally.
            7. Never invent current weather or exchange-rate information.
            """;
            


    public ChatService(ChatClient.Builder builder, CalculatorTool calculatorTool,
                       CurrencyExchangeTool exchangeTool,
                       WeatherTool weatherTool) {
        this.chatClient = builder.build();
        this.calculatorTool = calculatorTool;
        this.currencyExchangeTool = exchangeTool;
        this.weatherTool = weatherTool;
    }

    public String generateResponse(String prompt){
        history.add(new UserMessage(prompt));

        String output = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .tools(calculatorTool,currencyExchangeTool,weatherTool)
                .messages(history)
                .call()
                .content();

        history.add(new AssistantMessage(output));

        return output;
    }

}
