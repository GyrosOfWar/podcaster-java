package at.wambo.podcaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class PodcasterApplication {
    public static void main(String[] args) {
        SpringApplication.run(PodcasterApplication.class, args);
    }
}
