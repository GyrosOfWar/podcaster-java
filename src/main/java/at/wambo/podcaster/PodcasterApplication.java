package at.wambo.podcaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration;
import org.springframework.boot.orm.jpa.EntityScan;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EntityScan(basePackageClasses = {at.wambo.podcaster.model.User.class, at.wambo.podcaster.controller.RssFeedController.class,
        at.wambo.podcaster.repository.UserRepository.class})
public class PodcasterApplication {
//    @Configuration
//    @Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
//    protected static class SecuirtyConfiguration extends WebSecurityConfigurerAdapter {
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http.httpBasic()
//                    .and()
//                    .authorizeRequests()
//                    .anyRequest().authenticated();
//        }
//    }

    public static void main(String[] args) {
        SpringApplication.run(PodcasterApplication.class, args);
    }
}
