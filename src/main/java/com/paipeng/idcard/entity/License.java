package com.paipeng.idcard.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "licenses")
public class License extends BaseEntity {
    @Column(nullable = false, length = 64)
    private String owner;

    @Column(name = "app_name", nullable = false, length = 64)
    private String appName;

    @Column(name = "expire", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp expire;

    @Column(name = "nanogrid", columnDefinition = "bit default 0 ", nullable = false)
    private boolean nanogrid;

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

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
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
}
