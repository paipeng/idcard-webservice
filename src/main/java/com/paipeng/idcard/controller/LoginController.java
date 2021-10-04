package com.paipeng.idcard.controller;

import com.paipeng.idcard.entity.User;
import com.paipeng.idcard.repository.UserRepository;
import com.paipeng.idcard.security.JWTAuthorizationFilter;
import com.sun.istack.NotNull;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class LoginController {
    private final static Logger logger = LogManager.getLogger(LoginController.class.getSimpleName());
    @Autowired
    private UserRepository userRepository;

    @PostMapping(value = "/login", produces = {"application/json;charset=UTF-8"})
    public String login(@NotNull @RequestBody User user) {
        logger.info("my login: " + user.getEmail());
        logger.info("my password: " + user.getPassword());

        if (user.getPassword() != null) {
            //BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            //String encodedPassword = passwordEncoder.encode(user.getPassword());
            //logger.trace("encodedPassword: " + encodedPassword);
            User foundUser = userRepository.findByEmail(user.getEmail());


            if (foundUser != null) {
                PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                if(passwordEncoder.matches(user.getPassword(),foundUser.getPassword())) {
                    String token = getJWTToken(user.getEmail());
                    return "jwt: " + token;
                } else {
                    return "password invalid";
                }
            } else {
                return "user not found";
            }
        } else {
            return "password invalid";
        }
    }

    private String getJWTToken(String username) {
        List<GrantedAuthority> grantedAuthorities = AuthorityUtils
                .commaSeparatedStringToAuthorityList("ROLE_USER");
        String token = Jwts
                .builder()
                .setId("softtekJWT")
                .setSubject(username)
                .claim("authorities",
                        grantedAuthorities.stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.toList()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 600000))
                .signWith(SignatureAlgorithm.HS512,
                        JWTAuthorizationFilter.SECRET.getBytes()).compact();
        return "Bearer " + token;
    }
}
