package com.example.gender_healthcare_service.config;


import com.example.gender_healthcare_service.Filter.JwtTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private JwtAuthEntryPoint jwtAuthEntryPoint;
    @Autowired
    private JwtTokenFilter jwtTokenFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.cors(Customizer.withDefaults()).authorizeHttpRequests(auth->
                auth
                // ========== PUBLIC APIs - Không cần đăng nhập ==========
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/validate-otp").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/refresh-token").permitAll()
                .requestMatchers("/api/auth/login-by-google").permitAll()
                .requestMatchers("/oauth2/**").permitAll()
                
                // Swagger Documentation - MUST BE FIRST
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                .requestMatchers("/v3/api-docs/**").permitAll()
                .requestMatchers("/v3/api-docs").permitAll()
                .requestMatchers("/swagger-resources/**").permitAll()
                .requestMatchers("/webjars/**").permitAll()
                .requestMatchers("/favicon.ico").permitAll()
                .requestMatchers("/ws/**").permitAll() // WebSocket endpoints
                
                // Chat APIs - Require authentication
                .requestMatchers("/api/chat/**").authenticated()
                
                // Homepage & Blog - Public content
                .requestMatchers(HttpMethod.GET, "/api/homepage/featured-doctors").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/homepage/latest-blog-posts").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/homepage/consultants").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/homepage/details").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/homepage/blog/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blog/posts/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/blog/categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/qa/faq").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/services/testing-services").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/services/testing-services/*").permitAll()
                .requestMatchers("/api/test/**").permitAll()
                
                // Cloudinary APIs - Public for testing
                .requestMatchers(HttpMethod.POST, "/api/cloudinary/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cloudinary/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/cloudinary/**").permitAll()
                
                // Default Images APIs
                .requestMatchers(HttpMethod.GET, "/api/default-images/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/default-images/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                .requestMatchers(HttpMethod.PUT, "/api/default-images/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/api/default-images/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                
                // Avatar APIs - Require authentication
                .requestMatchers(HttpMethod.POST, "/api/user/avatar/upload").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/user/avatar/delete").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/user/avatar/check").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                
                // ========== CUSTOMER/PATIENT APIs ==========
                .requestMatchers(HttpMethod.POST, "/api/booking").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/booking/my-bookings").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/user/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/user/profile").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers("/api/menstrual-cycle/**").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/qa/questions").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/qa/user/questions").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers("/api/consultation/book").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers("/api/consultation/user-bookings").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers("/api/feedback/consultation").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                
                // ========== DASHBOARD APIs ==========
                .requestMatchers(HttpMethod.GET, "/api/homepage/stats").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/homepage/notifications").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/homepage/upcoming-appointments").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/homepage/notifications/*/mark-read").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/homepage/notifications/mark-all-read").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                
                // ========== NOTIFICATION APIs ==========
                .requestMatchers(HttpMethod.GET, "/api/notifications").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/notifications/type/*").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/notifications/unread-count").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/notifications/*/read").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/notifications/mark-all-read").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/notifications/create-sample").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                
                // ========== CONSULTANT APIs ==========
                .requestMatchers("/api/consultant/**").hasAnyAuthority("ROLE_CONSULTANT", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers("/api/consultation/consultant-bookings").hasAnyAuthority("ROLE_CONSULTANT", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/qa/questions/*/answers").hasAnyAuthority("ROLE_CONSULTANT", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/qa/answers/*").hasAnyAuthority("ROLE_CONSULTANT", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/qa/consultant/questions").hasAnyAuthority("ROLE_CONSULTANT", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/blog/posts").hasAnyAuthority("ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/blog/posts/*").hasAnyAuthority("ROLE_CONSULTANT", "ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/feedback/consultant/*").hasAnyAuthority("ROLE_CONSULTANT", "ROLE_MANAGER", "ROLE_ADMIN")
                
                // ========== STAFF APIs ==========
                .requestMatchers(HttpMethod.PATCH, "/api/booking/*/status").hasAnyAuthority("ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/booking/*/admin").hasAnyAuthority("ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers("/api/services/testing-services/bookings/*/results").hasAnyAuthority("ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/staff/dashboard/stats").hasAnyAuthority("ROLE_STAFF", "ROLE_MANAGER", "ROLE_ADMIN")
                
                // ========== MANAGER APIs ==========
                .requestMatchers(HttpMethod.GET, "/api/admin/consultants/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/admin/consultants").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/consultants/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers("/api/admin/testing-services/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers("/api/admin/reports/**").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/blog/categories").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/blog/categories/*").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/blog/categories/*").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                
                // ========== ADMIN ONLY APIs ==========
                .requestMatchers("/api/admin/users/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/admin/consultants/*").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/admin/patient/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/admin/setUserToConsultant/*").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/blog/posts/*").hasAnyAuthority("ROLE_MANAGER", "ROLE_ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/qa/questions/*/public").hasAuthority("ROLE_ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/qa/questions/*").hasAnyAuthority("ROLE_CUSTOMER", "ROLE_ADMIN")
                .requestMatchers("/api/admin/feedback/**").hasAuthority("ROLE_ADMIN")
                
                // All other requests need authentication
                .anyRequest().authenticated()
                ).csrf(csrf -> csrf.disable())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthEntryPoint))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .failureUrl("/login?error=true"))
                .build();
    }
}
