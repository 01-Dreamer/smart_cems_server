package zxylearn.smart_cems_server.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import zxylearn.smart_cems_server.common.JwtUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String JWT_BLACKLIST_KEY = "auth:jwt:blacklist:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            // 检查黑名单
            if (redisTemplate.hasKey(JWT_BLACKLIST_KEY + jwt)) {
                chain.doFilter(request, response);
                return;
            }

            try {
                username = jwtUtils.extractUsername(jwt);
            } catch (Exception e) {
                // Token 无效或已过期
            }
        }


        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtils.validateToken(jwt)) {
                String role = jwtUtils.extractRole(jwt);
                if (role != null) {
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority(role));
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities);
                    usernamePasswordAuthenticationToken
                            .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        }
        chain.doFilter(request, response);
    }
}
