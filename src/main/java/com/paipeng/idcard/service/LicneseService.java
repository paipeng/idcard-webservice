package com.paipeng.idcard.service;

import com.paipeng.idcard.entity.License;
import com.paipeng.idcard.repository.LicenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LicneseService {
    @Autowired
    private LicenseRepository licenseRepository;

    public List<License> getLicenses() {
        return licenseRepository.findAll();
    }
}
