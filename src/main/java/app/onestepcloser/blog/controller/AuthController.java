package app.onestepcloser.blog.controller;

import app.onestepcloser.blog.config.JwtProperties;
import app.onestepcloser.blog.domain.model.request.LoginRequest;
import app.onestepcloser.blog.domain.model.response.BaseResponse;
import app.onestepcloser.blog.domain.model.response.LoginResponse;
import app.onestepcloser.blog.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;

    @RequestMapping(method = RequestMethod.POST, value = "/login",  produces = {"application/json"})
    public ResponseEntity<BaseResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            return customReturn(() -> {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
                String username = authentication.getName();
                String token = jwtUtil.generateToken(username);
                long expiresInMs = jwtProperties.getExpiration();
                return LoginResponse.of(token, username, expiresInMs);
            });
        } catch (BadCredentialsException e) {
            BaseResponse<LoginResponse> response = new BaseResponse<>(HttpStatus.UNAUTHORIZED);
            response.setErrorList(java.util.List.of("Invalid username or password"));
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
}
