package com.paipeng.idcard.repository;

import com.paipeng.idcard.entity.License;
import com.paipeng.idcard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface LicenseRepository extends JpaRepository<License, Long> {
    License findByOwner(String owner);
    @Query("SELECT l FROM License l WHERE l.user = ?1")
    List<License> findAllByUser(User user);
}
