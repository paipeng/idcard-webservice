package com.paipeng.idcard.security;


import com.paipeng.idcard.config.ApplicationConfig;
import com.paipeng.idcard.entity.User;
import com.paipeng.idcard.repository.UserRepository;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JWTAuthorizationFilter extends OncePerRequestFilter {
    private final static Logger logger = LoggerFactory.getLogger(JWTAuthorizationFilter.class);
    private final static String HEADER = "Authorization";
    private final static String PREFIX = "Bearer ";
    //public static final String SECRET = "";

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        logger.trace("doFilterInternal");
        try {
            if (checkJWTToken(request, response)) {
                logger.trace("jwt token found");
                String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
                logger.trace("jwtToken: " + jwtToken);

                User user = userRepository.findByToken(jwtToken);

                Claims claims = validateToken(jwtToken, user);
                if (claims != null && claims.get("authorities") != null) {
                    setUpSpringAuthentication(claims, user);
                } else {
                    logger.error("validateToken failed");
                    SecurityContextHolder.clearContext();
                }
            } else {
                logger.error("checkJWTToken failed");
                SecurityContextHolder.clearContext();
            }
            chain.doFilter(request, response);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException e) {
            logger.error("doFilterInternal exception: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            return;
        } catch (Exception e) {
            logger.error("doFilterInternal exception2: " + e.getMessage());
            if (e.getMessage().endsWith("java.lang.Exception: 400")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } else if (e.getMessage().endsWith("java.lang.Exception: 403")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            } else if (e.getMessage().endsWith("java.lang.Exception: 401")) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
            } else if (e.getMessage().endsWith("java.lang.Exception: 404")) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } else if (e.getMessage().endsWith("java.lang.Exception: 409")) {
                response.setStatus(HttpServletResponse.SC_CONFLICT);
                response.sendError(HttpServletResponse.SC_CONFLICT, e.getMessage());
            } else {
                logger.error("exception not handle");
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
    }

    private Claims validateToken(String jwtToken, User user) {
        logger.trace("validateToken");
        if (user != null) {
            logger.trace("local SECRET: " + user.getToken());
            Claims claims = Jwts.parser().setSigningKey(applicationConfig.getSecurityJwtSecret()).parseClaimsJws(jwtToken).getBody();
            if (claims != null) {
                return claims;
            }
        }
        return null;
    }

    /**
     * Authentication method in Spring flow
     *
     * @param claims claims
     * @param user   user
     */
    private void setUpSpringAuthentication(Claims claims, User user) {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List<String>) claims.get("authorities");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
        String authenticationHeader = request.getHeader(HEADER);
        if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
            return false;
        return true;
    }

}
