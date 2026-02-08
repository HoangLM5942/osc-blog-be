package app.onestepcloser.blog.security;

import app.onestepcloser.blog.utility.Constants;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
@Builder
public class UserDetailsCustom implements UserDetails {
    private Long id;
    private String status;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String image;
    private String token;
    private long expiresIn;
    private List<String> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (CollectionUtils.isEmpty(this.roles)) {
            return Collections.emptyList();
        }
        return this.roles.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return !Objects.equals(this.status, Constants.STATUS.DELETED.name());
    }

    @Override
    public boolean isAccountNonLocked() {
        return !Objects.equals(this.status, Constants.STATUS.INACTIVATED.name());
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
