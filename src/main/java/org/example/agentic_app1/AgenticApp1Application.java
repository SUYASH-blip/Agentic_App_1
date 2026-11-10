package org.example.agentic_app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AgenticApp1Application {

    public static void main(String[] args) {
        SpringApplication.run(AgenticApp1Application.class, args);
    }

}
