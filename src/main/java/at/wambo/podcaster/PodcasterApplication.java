package at.wambo.podcaster;

import at.wambo.podcaster.util.LogRequestsFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

import javax.servlet.Filter;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class PodcasterApplication {

    public static void main(String[] args) {
        SpringApplication.run(PodcasterApplication.class, args);
    }

    @Bean
    public Filter logFilter() {
        return new LogRequestsFilter();
    }
}
