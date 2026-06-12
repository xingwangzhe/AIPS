package com.needhelp.aips.dto.drug;

import java.math.BigDecimal;

public class DrugSearchResponse {
    private Long id;
    private String name;
    private String specification;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String mainImageUrl;
    private Integer isPrescription;
    private Integer stock;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }
    public Integer getIsPrescription() { return isPrescription; }
    public void setIsPrescription(Integer isPrescription) { this.isPrescription = isPrescription; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}
