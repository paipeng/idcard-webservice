package com.paipeng.idcard.controller;

import com.paipeng.idcard.entity.User;
import com.paipeng.idcard.service.UserService;
import com.sun.istack.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/logout2")
    public String logout() {
        logger.info("logout2");
        userService.logout();
        return "";
    }
}
