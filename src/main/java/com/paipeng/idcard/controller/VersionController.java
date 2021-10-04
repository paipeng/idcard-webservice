package com.paipeng.idcard.controller;

import com.paipeng.idcard.entity.User;
import com.paipeng.idcard.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@RestController
public class VersionController {
    private Logger log;
    @Autowired
    private UserRepository userRepository;

    public VersionController() {
        this.log = LogManager.getLogger(this.getClass().getName());
    }

    @GetMapping("/version")
    public String version() {
        log.trace("version");
        return "Hello world";
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> listUsers = userRepository.findAll();
        model.addAttribute("listUsers", listUsers);

        return "users";
    }

}
