package app.onestepcloser.blog.security;

import app.onestepcloser.blog.config.SecurityAppProperties;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final SecurityAppProperties securityAppProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        if (!securityAppProperties.getDefaultUsername().equals(username)) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        return User.builder()
                .username(securityAppProperties.getDefaultUsername())
                .password(passwordEncoder.encode(securityAppProperties.getDefaultPassword()))
                .roles("USER")
                .build();
    }

    public boolean matchesRawPassword(String rawPassword, UserDetails user) {
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }
}
