package com.viettin.security;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.springframework.data.util.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthFilter extends OncePerRequestFilter {

    JwtUtils jwtUtils;
    CustomUserDetailsService customUserDetailsService;

    public String getTokenFromRequest(HttpServletRequest request){
       String token = request.getHeader("Authorization");
       if(StringUtils.hasText(token) && token.startsWith("Bearer ")){
           return token.substring(7);
       }
       return null;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (token != null) {
            try {
                String username = jwtUtils.getUsernameFromToken(token);

                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                if (jwtUtils.isValidateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                System.err.println("JWT validation error: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }


    public boolean isByPassToken(HttpServletRequest request){
        List<Pair<String, String>> token = List.of(
                Pair.of("http://localhost:8080/api/user/login", "POST"),
                Pair.of("http://localhost:8080/api/user/register", "POST"),
                Pair.of("http://localhost:8080/api/user/forgot-password", "POST"),
                Pair.of("http://localhost:8080/api/user/reset-password", "POST"),
                Pair.of("http://localhost:8080/api/user/verify", "POST")

                );
        for (Pair<String, String> pair : token) {
            if(request.getRequestURI().equals(pair.getFirst()) && request.getMethod().equals(pair.getSecond())) {
                return true;
            }
        }
        return false;
    }
}
