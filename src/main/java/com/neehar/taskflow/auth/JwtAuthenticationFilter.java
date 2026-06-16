package com.neehar.taskflow.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. Get the Authorization header
        final String authHeader = request.getHeader("Authorization");

        // 2. No token? Just continue down the chain.
        //    The AuthorizationFilter further down will reject the request if the endpoint requires auth.
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Strip the "Bearer " prefix to get the raw token
        final String token = authHeader.substring(7);

        // 4. Quick validity check (signature + expiry)
        if (!jwtService.isTokenValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Extract the email from the token's subject claim
        final String email = jwtService.extractEmail(token);

        // 6. Only authenticate if nobody has been authenticated already this request
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 7. Load the user from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 8. Build the Authentication object (Spring's "currently logged-in user" representation)
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,                          // no credentials — JWT already proved identity
                            userDetails.getAuthorities()
                    );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 9. Put it into the SecurityContext for the rest of the request
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        // 10. Continue down the chain
        filterChain.doFilter(request, response);
    }
}