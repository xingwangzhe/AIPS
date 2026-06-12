package com.needhelp.aips.entity;

import com.needhelp.aips.common.base.BaseEntity;
import jakarta.persistence.*;

/**
 * 药品分类表（categories）。
 * 支持多级树形分类，children 由 Service 层填充。
 */
@Entity
@Table(name = "categories")
public class Category extends BaseEntity {

    @Column(name = "parent_id", nullable = false)
    private Long parentId = 0L; // 0 为一级分类

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "icon_url", length = 255)
    private String iconUrl;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(nullable = false)
    private Integer status = 1; // 1启用 0禁用

    // 非持久化字段，Service 层组装树形结构时使用
    @Transient
    private java.util.List<Category> children;

    public Long getParentId() { return parentId; }
    public void setParentId(Long parentId) { this.parentId = parentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public java.util.List<Category> getChildren() { return children; }
    public void setChildren(java.util.List<Category> children) { this.children = children; }
}
