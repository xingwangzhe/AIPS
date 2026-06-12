package com.needhelp.aips.dto.drug;

import java.util.List;

public class CategoryResponse {
    private Long id;
    private String name;
    private String iconUrl;
    private List<CategoryResponse> children;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getIconUrl() { return iconUrl; }
    public void setIconUrl(String iconUrl) { this.iconUrl = iconUrl; }
    public List<CategoryResponse> getChildren() { return children; }
    public void setChildren(List<CategoryResponse> children) { this.children = children; }
}
