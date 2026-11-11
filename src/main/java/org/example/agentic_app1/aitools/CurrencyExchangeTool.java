package org.example.agentic_app1.aitools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class CurrencyExchangeTool {

    private final RestClient restClient;

    public CurrencyExchangeTool(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.frankfurter.dev")
                .build();
    }

    @Tool(description = "Gets the latest exchange rate " +
            "between two currencies.")
    public Map<String, Object> getExchangeRate(
            @ToolParam(description = "Source currency code, " +
                    "for example USD")
            String from,

            @ToolParam(description = "Target currency code," +
                    " for example INR")
            String to) {

        System.out.println("Currency Exchange tool called: " + from + " -> " + to);

        try {
            String source = from == null ? "" : from.trim().toUpperCase();
            String target = to == null ? "" : to.trim().toUpperCase();

            Map<String, Object> body = restClient.get()
                    .uri("/v2/rate/{from}/{to}", source, target)
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (body == null) {
                return Map.of("error", "Exchange service returned no data");
            }
            return body;   // already a JSON object

        } catch (Exception e) {
            return Map.of("error", "Could not get exchange rate for "
                    + from + " to " + to + ": " + e.getMessage());
        }
    }
}