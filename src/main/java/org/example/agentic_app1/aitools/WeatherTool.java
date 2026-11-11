package org.example.agentic_app1.aitools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class WeatherTool {

    private final RestClient restClient;
    private final String apiKey;

    public WeatherTool(RestClient.Builder builder,
                       @Value("${WEATHER_API_KEY}") String apikey) {

        this.restClient = builder.baseUrl("https://api.weatherapi.com/v1").build();
        this.apiKey = apikey;
    }

    @Tool(description = "Get the current weather of a city.")
    public Map<String, Object> currentWeather(
            @ToolParam(description = "Name of the city") String city) {

        System.out.println("Weather tool called for: " + city);

        try {
            Map<String, Object> body = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/current.json")
                            .queryParam("key", apiKey)
                            .queryParam("q", city)
                            .build())
                    .retrieve()
                    .body(new ParameterizedTypeReference<Map<String, Object>>() {});

            if (body == null) {
                return Map.of("error", "Weather service returned no data");
            }

            Map<String, Object> location = asMap(body.get("location"));
            Map<String, Object> current = asMap(body.get("current"));
            Map<String, Object> condition = asMap(current.get("condition"));

            // Short, clean summary instead of the whole raw API response
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("city", location.get("name"));
            result.put("region", location.get("region"));
            result.put("country", location.get("country"));
            result.put("local_time", location.get("localtime"));
            result.put("temperature_c", current.get("temp_c"));
            result.put("feels_like_c", current.get("feelslike_c"));
            result.put("condition", condition.get("text"));
            result.put("humidity_percent", current.get("humidity"));
            result.put("wind_kph", current.get("wind_kph"));
            return result;

        } catch (Exception e) {
            return Map.of("error", "Could not get weather for " + city + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> asMap(Object value) {
        return value instanceof Map ? (Map<String, Object>) value : Map.of();
    }
}