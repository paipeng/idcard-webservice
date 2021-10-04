package com.paipeng.idcard.controller;

import com.paipeng.idcard.entity.License;
import com.paipeng.idcard.service.LicneseService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/licenses")
public class LicenseController {
    private final static Logger logger = LogManager.getLogger(LicenseController.class.getSimpleName());

    @Autowired
    private LicneseService licneseService;

    @GetMapping(value = "", produces = {"application/json;charset=UTF-8"})
    public List<License> getAll() {
        logger.info("getAll");
        return licneseService.getLicenses();
    }
}
