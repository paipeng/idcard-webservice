package com.paipeng.idcard.controller;

import com.paipeng.idcard.entity.License;
import com.paipeng.idcard.service.LicneseService;
import com.sun.istack.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping(value = "", produces = {"application/json;charset=UTF-8"})
    public License save(@RequestBody License license, HttpServletResponse httpServletResponse) throws Exception {
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return licneseService.save(license);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@NotNull @PathVariable("id") Long id) throws Exception {
        licneseService.delete(id);
    }

    @PutMapping(value = "/{id}", produces = {"application/json;charset=UTF-8"})
    public License update(@NotNull @PathVariable("id") Long id, @RequestBody License license, HttpServletResponse httpServletResponse) throws Exception {
        logger.info("update: " + id);
        license = licneseService.update(id, license);
        if (license == null) {
            httpServletResponse.setStatus(HttpStatus.NOT_FOUND.value());
        }
        return license;
    }
}
