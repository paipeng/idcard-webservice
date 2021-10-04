package com.paipeng.idcard.entity;

import org.hibernate.annotations.Generated;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 数据库Entity虚拟基础类，其他所有Entities都继承它。
 * Created by hs on 2017/2/4.
 * Modified by Pai Peng
 */
@MappedSuperclass
public abstract class BaseEntity {
    /**
     * 表格主键Id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 数据第一次创建时间，自动生成，无需传入
     */
    @Generated(org.hibernate.annotations.GenerationTime.ALWAYS)
    @Column(name = "create_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Timestamp createTime;
    /**
     * 每次更新此条数据时的记录时间（上一次更新时间），无需传入
     */
    @Generated(org.hibernate.annotations.GenerationTime.ALWAYS)
    @Column(name = "update_time", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP", insertable = false, updatable = false)
    private Timestamp updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}
