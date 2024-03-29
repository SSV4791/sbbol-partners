package ru.sberbank.pprb.sbbol.partners;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class, scanBasePackages = {
    "ru.sberbank.pprb.sbbol.partners",
    "ru.sberbank.pprb.sbbol.migration"
})
public class Runner {
    public static void main(String[] args) {
        SpringApplication.run(Runner.class, args);
    }
}
