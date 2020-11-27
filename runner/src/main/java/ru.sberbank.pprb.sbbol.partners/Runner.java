package ru.sberbank.pprb.sbbol.partners;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Runner {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Runner.class, args);
        System.out.println("Context is loaded");
    }
}
