package app.onestepcloser.blog.security;

import app.onestepcloser.blog.domain.model.response.LoginResponse;
import app.onestepcloser.blog.utility.Constants;
import app.onestepcloser.blog.utility.JsonHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Map;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private static final Logger logger = LogManager.getLogger(CustomAuthenticationFilter.class);

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        super();
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(@NonNull HttpServletRequest request,
                                                @NonNull HttpServletResponse response) throws AuthenticationException {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
        String username = Constants.EMPTY_STRING;
        String password = Constants.EMPTY_STRING;
        try {
            String contentType = request.getContentType();
            if (contentType.equalsIgnoreCase("application/json")) {
                Map<String, String> params = JsonHelper.getObject(request.getInputStream(), Map.class);
                if (params != null) {
                    username = params.getOrDefault("username", Constants.EMPTY_STRING);
                    password = params.getOrDefault("password", Constants.EMPTY_STRING);
                }
            }
        } catch (Exception e) {
            throw new AuthenticationServiceException("Logging in failed", e);
        }
        logger.info("[LOGIN] Username: {}", username);
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }

    @Override
    protected void successfulAuthentication(@NonNull HttpServletRequest request,
                                            @NonNull HttpServletResponse response,
                                            @NonNull FilterChain chain,
                                            Authentication authResult) throws IOException {
        UserDetailsCustom userDetails = (UserDetailsCustom) authResult.getPrincipal();

        LoginResponse loginResponse = new LoginResponse(userDetails);
        String token = jwtUtil.generateToken(loginResponse.toJson());
        loginResponse.setToken(token);
        loginResponse.setExpiresIn(jwtUtil.getExpiration());

        JSONObject rsp = new JSONObject();
        rsp.put("code", 200);
        rsp.put("message", "SUCCESS");
        rsp.put("data", loginResponse);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(200);
        response.getWriter().println(rsp);

        logger.debug("[LOGIN SUCCESSFUL] " + rsp);

        SecurityContextHolder.clearContext();
    }

    protected void unsuccessfulAuthentication(@NonNull HttpServletRequest request,
                                              @NonNull HttpServletResponse response,
                                              @NonNull AuthenticationException failed) throws IOException {
        JSONObject rsp = new JSONObject();
        rsp.put("code", HttpStatus.UNAUTHORIZED.value());
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        if (failed instanceof UsernameNotFoundException) {
            rsp.put("message", failed.getMessage());
        } else if (failed instanceof BadCredentialsException) {
            rsp.put("message", "Username or Password is incorrect!");
        } else if (failed instanceof LockedException) {
            rsp.put("message", "This account is inactivated!");
        } else if (failed instanceof DisabledException) {
            rsp.put("message", "This account has been deleted!");
        } else if (failed.getMessage().contains("Authentication method not supported")) {
            rsp.put("message", failed.getMessage());
            rsp.put("code", HttpStatus.METHOD_NOT_ALLOWED.value());
            response.setStatus(HttpStatus.METHOD_NOT_ALLOWED.value());
        } else {
            rsp.put("message", HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
            rsp.put("code", HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().println(rsp);

        logger.debug("[LOGIN FAILED] " + rsp);
        SecurityContextHolder.clearContext();
    }

}
