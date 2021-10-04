package com.paipeng.idcard.controller;

import com.paipeng.idcard.entity.User;
import com.paipeng.idcard.service.UserService;
import com.sun.istack.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class LoginController {
    private final static Logger logger = LogManager.getLogger(LoginController.class.getSimpleName());
    @Autowired
    private UserService userService;

    @PostMapping(value = "/login", produces = {"application/json;charset=UTF-8"})
    public String login(@NotNull @RequestBody User user) {
        User loginUser = userService.login(user);
        if (loginUser != null) {
            return "jwt: " + "Bearer " + loginUser.getToken();
        } else {
            return "login failed";
        }
    }


    @ResponseStatus(HttpStatus.NO_CONTENT)
    @GetMapping("/logout2")
    public void logout() {
        logger.info("logout2");
        userService.logout();
        //response.setStatus(HttpStatus.NO_CONTENT.value());
        //return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
