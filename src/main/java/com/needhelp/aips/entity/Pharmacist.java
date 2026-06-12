package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

/**
 * 药师表（pharmacists）。
 * status: 1在职 2离线 3停用
 */
@Entity
@Table(name = "pharmacists")
public class Pharmacist extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "license_no", nullable = false, unique = true, length = 50)
    private String licenseNo;

    @Column(nullable = false, length = 20)
    private String phone;

    @Column(nullable = false)
    private Integer status = 1; // 1在职 2离线 3停用

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getLicenseNo() { return licenseNo; }
    public void setLicenseNo(String licenseNo) { this.licenseNo = licenseNo; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
