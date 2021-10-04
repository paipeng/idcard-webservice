package com.paipeng.idcard.security;


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
    private final String HEADER = "Authorization";
    private final String PREFIX = "Bearer ";
    public static final String SECRET = "5161127a80ff47a1855176c345a1de39833b486ea3dd40629081ab0370a1632c87496492fb634c60a458182c69a7f0d0";


    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        logger.trace("doFilterInternal");
        try {
            if (checkJWTToken(request, response)) {
                logger.trace("jwt token found");
                Claims claims = validateToken(request);
                if (claims != null && claims.get("authorities") != null) {
                    setUpSpringAuthentication(claims);
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
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            ((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
            return;
        }
    }

    private Claims validateToken(HttpServletRequest request) {
        logger.trace("validateToken");
        String jwtToken = request.getHeader(HEADER).replace(PREFIX, "");
        logger.trace("jwtToken: " + jwtToken);

        User user = userRepository.findByToken(jwtToken);
        if (user != null) {
            logger.trace("local SECRET: " + user.getToken());
            Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(jwtToken).getBody();
            if (claims != null) {
                return claims;
            }
        }
        return null;
    }

    /**
     * Authentication method in Spring flow
     *
     * @param claims
     */
    private void setUpSpringAuthentication(Claims claims) {
        @SuppressWarnings("unchecked")
        List<String> authorities = (List) claims.get("authorities");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(claims.getSubject(), null,
                authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));
        SecurityContextHolder.getContext().setAuthentication(auth);

    }

    private boolean checkJWTToken(HttpServletRequest request, HttpServletResponse res) {
        String authenticationHeader = request.getHeader(HEADER);
        if (authenticationHeader == null || !authenticationHeader.startsWith(PREFIX))
            return false;
        return true;
    }

}
