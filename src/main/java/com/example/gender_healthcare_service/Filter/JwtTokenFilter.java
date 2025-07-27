package com.example.gender_healthcare_service.Filter;

import com.example.gender_healthcare_service.service.AuthenticationService;
import com.example.gender_healthcare_service.service.JwtService;
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
    private JwtService tokenService;
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
            "/api/homepage/featured-doctors",
            "/api/homepage/latest-blog-posts", 
            "/api/homepage/consultants",
            "/api/homepage/details",
            "/api/homepage/blog/**",
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
                filterChain.doFilter(request, response);
                return;
            }

            // Xử lý Authorization header cho private APIs
            String authHeader = request.getHeader("Authorization");

            if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                // Validate token
                try {
                    if (!tokenService.validateToken(token)) {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\": \"JWT token không hợp lệ hoặc đã hết hạn\"}");
                        return;
                    }
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Lỗi xác thực JWT: " + e.getMessage() + "\"}");
                    return;
                }

                // Extract user and set authentication
                try {
                    String username = tokenService.getUserNameFromJWT(token);
                    
                    UserDetails userDetails = authenticationService.loadUserByUsername(username);
                    
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Lỗi xác thực người dùng: " + e.getMessage() + "\"}");
                    return;
                }
                
                filterChain.doFilter(request, response);
            } else {
                // No valid auth header for private API
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Yêu cầu header Authorization\"}");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Xác thực thất bại: " + e.getMessage() + "\"}");
        }
    }
}
