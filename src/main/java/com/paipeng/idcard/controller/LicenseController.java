package com.paipeng.idcard.controller;

import com.paipeng.idcard.entity.License;
import com.paipeng.idcard.service.LicneseService;
import com.sun.istack.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
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


    @GetMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
    public License getLicenseById(@NotNull @PathVariable("id") Long id, HttpServletResponse httpServletResponse) {
        logger.info("getLicenseById: " + id);
        License license = licneseService.getLicenseById(id);
        if (license == null) {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return license;
    }
}
