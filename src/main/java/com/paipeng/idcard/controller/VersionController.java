package com.paipeng.idcard.controller;

import com.paipeng.idcard.config.ApplicationConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VersionController {
    private Logger log;

    @Autowired
    private ApplicationConfig applicationConfig;

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
