package app.onestepcloser.blog.domain.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginResponse {

    private String token;
    private String type;
    private Long expiresInMs;
    private String username;

    public static LoginResponse of(String token, String username, long expiresInMs) {
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .username(username)
                .expiresInMs(expiresInMs)
                .build();
    }
}
