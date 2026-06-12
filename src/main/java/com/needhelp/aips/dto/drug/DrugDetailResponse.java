package com.needhelp.aips.dto.drug;

import java.math.BigDecimal;
import java.util.List;

public class DrugDetailResponse {
    private Long id;
    private String name;
    private String genericName;
    private String specification;
    private String manufacturer;
    private Integer isPrescription;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer stock;
    private String mainImageUrl;
    private String indications;
    private String dosage;
    private String contraindications;
    private List<ReviewItem> reviews;

    public static class ReviewItem {
        private String userName;
        private Integer rating;
        private String content;
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
        public Integer getRating() { return rating; }
        public void setRating(Integer rating) { this.rating = rating; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGenericName() { return genericName; }
    public void setGenericName(String genericName) { this.genericName = genericName; }
    public String getSpecification() { return specification; }
    public void setSpecification(String specification) { this.specification = specification; }
    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
    public Integer getIsPrescription() { return isPrescription; }
    public void setIsPrescription(Integer isPrescription) { this.isPrescription = isPrescription; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(BigDecimal originalPrice) { this.originalPrice = originalPrice; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    public String getMainImageUrl() { return mainImageUrl; }
    public void setMainImageUrl(String mainImageUrl) { this.mainImageUrl = mainImageUrl; }
    public String getIndications() { return indications; }
    public void setIndications(String indications) { this.indications = indications; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public String getContraindications() { return contraindications; }
    public void setContraindications(String contraindications) { this.contraindications = contraindications; }
    public List<ReviewItem> getReviews() { return reviews; }
    public void setReviews(List<ReviewItem> reviews) { this.reviews = reviews; }
}
