package ru.calendorny.securitystarter;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@Import(WebSecurityConfig.class)
public class SecurityAutoConfiguration {
}
