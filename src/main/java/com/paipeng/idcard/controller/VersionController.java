package com.paipeng.idcard.controller;

import com.paipeng.idcard.entity.User;
import com.paipeng.idcard.repository.UserRepository;
import com.paipeng.idcard.security.JWTAuthorizationFilter;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class VersionController {
    private Logger log;


    public VersionController() {
        this.log = LogManager.getLogger(this.getClass().getName());
    }

    @GetMapping("/version")
    public String version() {
        log.trace("version");
        //String token = getJWTToken("test@gmail.com");
        return "Hello IdCard CRM";
    }


}
