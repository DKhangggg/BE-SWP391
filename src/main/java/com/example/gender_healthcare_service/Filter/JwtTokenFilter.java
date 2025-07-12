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
    
    /**
     * Danh sách API public - không cần authentication
     * Phù hợp với hệ thống healthcare
     */
    private final List<String> PUBLIC_APIS = List.of(
            // ========== Authentication APIs ==========
            "/api/auth/login",
            "/api/auth/register", 
            "/api/auth/forgot-password",
            "/api/auth/reset-password",
            "/api/auth/validate-otp",
            "/api/auth/refresh-token",
            "/api/auth/login-by-google",
            "/oauth2/**",
            
            // ========== Documentation & Development - HIGH PRIORITY ==========
            "/swagger-ui/**",
            "/swagger-ui.html", 
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/swagger-resources/**",
            "/webjars/**", 
            "/favicon.ico",
            "/actuator/**",
            
            // ========== Public Website Content ==========
            "/",
            "/home",
            "/about",
            "/contact",
            "/services",
            "/doctors",
            "/blog",
            
            // ========== Public API Content ==========
            "/api/homepage/**",
            "/api/blog/posts/**",        // Xem blog posts
            "/api/blog/categories/**",   // Xem danh mục blog
            "/api/qa/faq",              // FAQ công khai
            "/api/services/testing-services",     // Xem danh sách dịch vụ
            "/api/services/testing-services/*",   // Xem chi tiết dịch vụ
            
            // ========== WebSocket ==========
            "/ws/**",
            "/topic/**",
            "/app/**"
    );

    /**
     * Kiểm tra URI có phải là public API không
     */
    public boolean isPublicAPI(String uri) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return PUBLIC_APIS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            
            // Kiểm tra nếu là public API
            if (isPublicAPI(requestURI)) {
                filterChain.doFilter(request, response);
                return;
            }

            // Xử lý Authorization header cho private APIs
            String authHeader = request.getHeader("Authorization");
            System.out.println("Authorization header present: " + (authHeader != null));

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                System.out.println("Processing JWT token...");

                // Validate token
                if (!tokenService.validateToken(token)) {
                    System.out.println("Invalid JWT token");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Invalid or expired JWT token\"}");
                    return;
                }

                // Extract user and set authentication
                String username = tokenService.getUserNameFromJWT(token);
                UserDetails userDetails = authenticationService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                System.out.println("Authentication successful for user: " + username + 
                                 " with authorities: " + userDetails.getAuthorities());
                
                filterChain.doFilter(request, response);
            } else {
                // No valid auth header for private API
                System.out.println("Missing or invalid Authorization header for protected endpoint");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Authorization header is required\"}");
            }
        } catch (Exception e) {
            System.err.println("JWT Filter error: " + e.getMessage());
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Authentication failed: " + e.getMessage() + "\"}");
        }
    }
}
