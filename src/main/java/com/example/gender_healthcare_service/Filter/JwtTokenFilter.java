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
            "/swagger-ui/index.html",
            "/v3/api-docs/**",
            "/v3/api-docs",
            "/v3/api-docs.yaml",
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
            "/api/qa/faq",              // FAQ công khai
            "/api/services/testing-services",     // Xem danh sách dịch vụ
            "/api/services/testing-services/*",   // Xem chi tiết dịch vụ
            "/api/test/**",             // Test endpoints
            
            // ========== WebSocket ==========
            "/ws/**",
            "/topic/**",
            "/app/**",
            
            // ========== Cloudinary APIs ==========
            "/api/cloudinary/**"
    );

    /**
     * Kiểm tra URI có phải là public API không
     */
    public boolean isPublicAPI(String uri, String method) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        
        // Đặc biệt xử lý cho blog posts - chỉ GET là public
        if (pathMatcher.match("/api/blog/posts/**", uri)) {
            return "GET".equals(method);
        }
        
        // Đặc biệt xử lý cho blog categories - chỉ GET là public
        if (pathMatcher.match("/api/blog/categories/**", uri)) {
            return "GET".equals(method);
        }
        
        return PUBLIC_APIS.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        try {
            String requestURI = request.getRequestURI();
            String method = request.getMethod();
            
            // Kiểm tra nếu là public API
            if (isPublicAPI(requestURI, method)) {
                System.out.println("✅ Public API detected: " + requestURI + " (" + method + ") - Bypassing JWT filter");
                filterChain.doFilter(request, response);
                return;
            }

            System.out.println("🔒 Protected API: " + requestURI + " - Checking JWT token");

            // Xử lý Authorization header cho private APIs
            String authHeader = request.getHeader("Authorization");
            System.out.println("🔍 Authorization header present: " + (authHeader != null));
            if (authHeader != null) {
                System.out.println("🔍 Authorization header value: " + authHeader.substring(0, Math.min(50, authHeader.length())) + "...");
            }

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                System.out.println("🔍 Processing JWT token... Token length: " + token.length());
                System.out.println("🔍 Token preview: " + token.substring(0, Math.min(20, token.length())) + "...");

                // Validate token
                try {
                    if (!tokenService.validateToken(token)) {
                        System.out.println("❌ Invalid JWT token");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"Invalid or expired JWT token\"}");
                        return;
                    }
                    System.out.println("✅ JWT token is valid");
                } catch (Exception e) {
                    System.out.println("❌ JWT validation error: " + e.getMessage());
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"JWT validation error: " + e.getMessage() + "\"}");
                    return;
                }

                                // Extract user and set authentication
                try {
                    String username = tokenService.getUserNameFromJWT(token);
                    System.out.println("🔍 Extracted username from token: " + username);
                    
                    UserDetails userDetails = authenticationService.loadUserByUsername(username);
                    System.out.println("🔍 Loaded user details for: " + username);
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
             
                    System.out.println("✅ Authentication successful for user: " + username + 
                                     " with authorities: " + userDetails.getAuthorities());
                } catch (Exception e) {
                    System.out.println("❌ Error during user authentication: " + e.getMessage());
                    e.printStackTrace();
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"User authentication error: " + e.getMessage() + "\"}");
                    return;
                }
                
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
