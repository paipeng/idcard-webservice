package com.paipeng.idcard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "licenses")
public class License extends BaseEntity {
    @Column(nullable = false, length = 64, unique = true)
    private String owner;

    @Column(name = "app", nullable = false, length = 64)
    private String app;

    @Column(name = "expire", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp expire;

    @Column(name = "nanogrid", columnDefinition = "bit default 0 ", nullable = false)
    private boolean nanogrid;


    @Column(name = "file_path", nullable = true, length = 128)
    private String filePath;


    @Column(name = "uuid", nullable = false, length = 36, unique = true)
    private String uuid;


    @Column(name = "signature", length = 512)
    private String signature;


    @ManyToOne(cascade = {CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @LazyToOne(value = LazyToOneOption.FALSE)
    private User user;

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public Timestamp getExpire() {
        return expire;
    }

    public void setExpire(Timestamp expire) {
        this.expire = expire;
    }

    public boolean isNanogrid() {
        return nanogrid;
    }

    public void setNanogrid(boolean nanogrid) {
        this.nanogrid = nanogrid;
    }

    @JsonIgnore
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonIgnore
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    @JsonIgnore
    public List<String> getFeatureStrings() {
        List<String> featureStrings = new ArrayList<>();
        featureStrings.add("owner:STRING=" + getOwner());
        featureStrings.add("app:STRING=" + getApp());
        featureStrings.add("uuid:STRING=" + getUuid());
        featureStrings.add("expire:DATE=" + getExpire());
        featureStrings.add("nanogrid:INT=" + (isNanogrid()?"1":"0"));
        return featureStrings;
    }
}
