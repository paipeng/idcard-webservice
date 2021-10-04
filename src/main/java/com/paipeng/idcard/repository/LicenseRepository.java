package com.paipeng.idcard.repository;

import com.paipeng.idcard.entity.License;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LicenseRepository extends JpaRepository<License, Long> {
    License findByOwner(String owner);
}
