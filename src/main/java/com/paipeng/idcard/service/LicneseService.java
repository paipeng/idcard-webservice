package com.paipeng.idcard.service;

import com.paipeng.idcard.entity.BaseEntity;
import com.paipeng.idcard.entity.License;
import com.paipeng.idcard.entity.User;
import com.paipeng.idcard.repository.LicenseRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LicneseService extends BaseService {
    private final static Logger logger = LogManager.getLogger(LicneseService.class.getSimpleName());

    @Autowired
    private LicenseRepository licenseRepository;

    public List<License> getLicenses() {
        logger.info("getLicenses");
        return licenseRepository.findAll();
    }

    public License getLicenseById(Long id) {
        logger.info("getLicenseById");
        return licenseRepository.findById(id).orElse(null);
    }

    public License save(License license) throws Exception {
        logger.info("save: " + license);
        User user = getUserFromSecurity();
        if (user != null) {
            license.setUser(user);
            return licenseRepository.saveAndFlush(license);
        } else {
            throw new Exception("403");
        }

    }
}
