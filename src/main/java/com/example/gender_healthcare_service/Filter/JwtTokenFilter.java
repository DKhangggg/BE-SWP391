package com.example.gender_healthcare_service.Filter;

import com.example.gender_healthcare_service.service.AuthenticationService;
import com.example.gender_healthcare_service.service.impl.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtServiceImpl tokenService;
    @Autowired
    private AuthenticationService authenticationService;
    private final List<String> AUTH_PERMISSION = List.of(
            "/api/login",
            "/api/register",
            "/",
            "/home",
            "/services",
            "/doctors",
            "/blog",
            "/contact",
            "/api//forgot-password",
            "/api/reset-password",
            "/api/auth/login-by-google",
            "/api/auth/register",
            "/api/auth/forgot-password",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/favicon.ico",
            "/swagger-ui.html"
    );

    public boolean checkIsPublicAPI(String uri) {
        AntPathMatcher pathMatch = new AntPathMatcher();
        return AUTH_PERMISSION.stream().anyMatch(pattern -> pathMatch.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            boolean isPublicAPI = checkIsPublicAPI(request.getRequestURI());
            System.out.println("Is public API: " + isPublicAPI);

            if (isPublicAPI) {
                filterChain.doFilter(request, response);
                return;
            }

            // Xử lý Authorization header
            String authHeader = request.getHeader("Authorization");
            System.out.println("Authorization header: " + authHeader);

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                System.out.println("Extracted token: " + (token != null ? token.substring(0, Math.min(10, token.length())) + "..." : "null"));

                if (!tokenService.validateToken(token)) {
                    System.out.println("Token validation failed");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
                    return;
                }

                String username = tokenService.getUserNameFromJWT(token);
                UserDetails userDetails = authenticationService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                filterChain.doFilter(request, response);
            } else {
                System.out.println("No JWT token found in request");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authorization required");
            }
        } catch (Exception e) {
            System.out.println("Exception in JwtTokenFilter: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Error processing JWT token: " + e.getMessage());
        }
    }
}
