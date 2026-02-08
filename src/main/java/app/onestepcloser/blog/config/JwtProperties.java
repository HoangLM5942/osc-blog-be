package app.onestepcloser.blog.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class JwtProperties {

    @Value("${app.auth.login}")
    private String loginPath;

    @Value("${app.auth.token.secret}")
    private String secret;

    @Value("${app.auth.token.expiration}")
    private long expiration;

}