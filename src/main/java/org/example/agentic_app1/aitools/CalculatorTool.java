package org.example.agentic_app1.aitools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CalculatorTool {

    @Tool(description = """
            Performs arithmetic calculations.
            Supported operations: add, subtract, multiply, divide, mod, power.
            """)
    public Map<String, Object> calculate(
            @ToolParam(description = "Operation: add, subtract, multiply, divide, mod, power")
            String operation,

            @ToolParam(description = "First number")
            double a,

            @ToolParam(description = "Second number")
            double b) {

        System.out.println("Calculator tool called: " + a + " " + operation + " " + b);

        String op = operation == null ? "" : operation.trim().toLowerCase();
        double result;

        switch (op) {
            case "add" -> result = a + b;
            case "subtract" -> result = a - b;
            case "multiply" -> result = a * b;
            case "divide" -> {
                if (b == 0) return Map.of("error", "Cannot divide by 0");
                result = a / b;
            }
            case "mod" -> {
                if (b == 0) return Map.of("error", "Cannot calculate mod by 0");
                result = a % b;   // the old version never returned this value
            }
            case "power" -> result = Math.pow(a, b);
            default -> {
                return Map.of("error", "Unsupported operation: " + operation);
            }
        }

        return Map.of("result", result);
    }
}