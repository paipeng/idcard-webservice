package com.paipeng.idcard.controller;

import com.paipeng.idcard.entity.User;
import com.paipeng.idcard.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/users")
public class UserController {
    private final static Logger log = LogManager.getLogger(UserController.class.getSimpleName());

    @Autowired
    private UserRepository userRepository;


    @GetMapping(value = "", produces = {"application/json;charset=UTF-8"})
    public List<User> listUsers() {
        List<User> listUsers = userRepository.findAll();
        //model.addAttribute("listUsers", listUsers);
        return listUsers;
    }
}
