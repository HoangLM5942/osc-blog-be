package app.onestepcloser.blog.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class SecurityAppProperties {
    @Value("${app.security.default-username}")
    private String defaultUsername = "admin";
    @Value("${app.security.default-username}")
    private String defaultPassword = "admin";
}
