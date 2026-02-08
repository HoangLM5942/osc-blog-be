package app.onestepcloser.blog.security;

import app.onestepcloser.blog.exception.ApiException;
import app.onestepcloser.blog.exception.ErrorMessages;
import app.onestepcloser.blog.utility.Constants;
import app.onestepcloser.blog.utility.JsonHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import net.minidev.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

public class CustomAuthorizationFilter extends BasicAuthenticationFilter {

    private static final Logger logger = LogManager.getLogger(CustomAuthorizationFilter.class);

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public CustomAuthorizationFilter(AuthenticationManager authenticationManager,
                                     JwtUtil jwtUtil,
                                     UserDetailsService userDetailsService) {
        super(authenticationManager);
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        try {
            logger.info(String.format("[REQUEST]\n\tIP : %s\n\tAPI: %s %s\n\tParams: %s\n\tHeader: %s",
                    getIPRequest(request),
                    request.getMethod(),
                    request.getServletPath(),
                    getParams(request),
                    getHeader(request)
            ));

            String header = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (header == null || !header.startsWith(Constants.TOKEN_PREFIX)) {
                throw new ApiException(HttpStatus.FORBIDDEN, ErrorMessages.ERROR.AUTH_E000.getMessage());
            }

            String token = header.substring(Constants.TOKEN_PREFIX.length() + 1);

            if (jwtUtil.validateToken(token)) {
                Map<String, Object> userInfo = JsonHelper.getMap(jwtUtil.extractUsername(token));
                String username = String.valueOf(userInfo.getOrDefault("username", Constants.EMPTY_STRING));
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new ApiException(HttpStatus.UNAUTHORIZED, ErrorMessages.ERROR.AUTH_E001.getMessage());
            }

            filterChain.doFilter(request, response);

        } catch (ApiException e) {
            JSONObject rsp = new JSONObject();
            rsp.put("code", e.getCode());
            rsp.put("message", e.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(e.getCode());
            response.getWriter().println(rsp);
            logger.error("ERROR: ", e);
        } catch (Exception e) {
            JSONObject rsp = new JSONObject();
            rsp.put("code", HttpStatus.UNAUTHORIZED.value());
            rsp.put("message", ErrorMessages.ERROR.AUTH_E000.getMessage());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.getWriter().println(rsp);
            logger.error("ERROR: ", e);
        } finally {
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.info("-------------------- Finished -------------------- Time: {} ms ", elapsedTime);
        }
    }

    private String getIPRequest(HttpServletRequest request) {
        if (request == null) return null;
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null || ipAddress.equals(Constants.EMPTY_STRING)) {
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }

    private String getParams(HttpServletRequest request) {
        if (request == null) return null;
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap == null || parameterMap.isEmpty()) return "No Params";
        StringBuilder params = new StringBuilder();
        for (String key : parameterMap.keySet()) {
            params.append(key).append("=").append(parameterMap.get(key)[0]).append(Constants.COMMA);
        }
        return params.toString();
    }

    private String getHeader(HttpServletRequest request) {
        if (request == null) return null;
        StringBuilder headers = new StringBuilder();
        Enumeration<String> elements = request.getHeaderNames();
        while (elements.hasMoreElements()) {
            String key = elements.nextElement();
            headers.append(key).append("=").append(request.getHeader(key)).append(Constants.COMMA);
        }
        return headers.toString();
    }
}
