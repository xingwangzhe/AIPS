package com.needhelp.aips.service.drug;

import com.needhelp.aips.common.dto.PageResult;
import com.needhelp.aips.common.exception.BusinessException;
import com.needhelp.aips.dto.drug.CategoryResponse;
import com.needhelp.aips.dto.drug.DrugDetailResponse;
import com.needhelp.aips.dto.drug.DrugSearchResponse;
import com.needhelp.aips.entity.Category;
import com.needhelp.aips.entity.Medicine;
import com.needhelp.aips.entity.MedicineReview;
import com.needhelp.aips.repository.CategoryRepository;
import com.needhelp.aips.repository.MedicineRepository;
import com.needhelp.aips.repository.MedicineReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 药品服务。
 * 处理药品分类浏览、搜索、详情查看。
 */
@Service
public class DrugService {

    private final CategoryRepository categoryRepository;
    private final MedicineRepository medicineRepository;
    private final MedicineReviewRepository reviewRepository;

    public DrugService(CategoryRepository categoryRepository,
                       MedicineRepository medicineRepository,
                       MedicineReviewRepository reviewRepository) {
        this.categoryRepository = categoryRepository;
        this.medicineRepository = medicineRepository;
        this.reviewRepository = reviewRepository;
    }

    /**
     * 获取药品分类树。
     */
    public List<CategoryResponse> getCategories(Long parentId) {
        List<Category> categories;
        if (parentId == null) {
            categories = categoryRepository.findByParentIdAndStatus(0L, 1);
        } else {
            categories = categoryRepository.findByParentIdAndStatus(parentId, 1);
        }
        return categories.stream().map(cat -> {
            CategoryResponse resp = new CategoryResponse();
            resp.setId(cat.getId());
            resp.setName(cat.getName());
            resp.setIconUrl(cat.getIconUrl());
            // 加载子分类
            List<Category> children = categoryRepository.findByParentIdAndStatus(cat.getId(), 1);
            if (!children.isEmpty()) {
                resp.setChildren(children.stream().map(child -> {
                    CategoryResponse childResp = new CategoryResponse();
                    childResp.setId(child.getId());
                    childResp.setName(child.getName());
                    childResp.setIconUrl(child.getIconUrl());
                    return childResp;
                }).collect(Collectors.toList()));
            }
            return resp;
        }).collect(Collectors.toList());
    }

    /**
     * 搜索药品。
     */
    public PageResult<DrugSearchResponse> searchDrugs(String keyword, Long categoryId, Integer isRx,
                                                       String sort, int page, int pageSize) {
        Sort sortObj = parseSort(sort);
        Pageable pageable = PageRequest.of(page - 1, pageSize, sortObj);

        Page<Medicine> result;
        if (categoryId != null && keyword != null && !keyword.isEmpty()) {
            result = medicineRepository.searchByCategoryAndKeyword(categoryId, keyword, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            result = medicineRepository.searchByKeyword(keyword, pageable);
        } else if (categoryId != null && isRx != null) {
            result = medicineRepository.findByCategoryAndRx(categoryId, isRx, pageable);
        } else if (categoryId != null) {
            result = medicineRepository.findByCategoryIdAndStatus(categoryId, 1, pageable);
        } else if (isRx != null) {
            result = medicineRepository.findByIsPrescriptionAndStatus(isRx, 1, pageable);
        } else {
            result = medicineRepository.searchByKeyword("", pageable);
        }

        List<DrugSearchResponse> list = result.getContent().stream().map(m -> {
            DrugSearchResponse dto = new DrugSearchResponse();
            dto.setId(m.getId());
            dto.setName(m.getName());
            dto.setSpecification(m.getSpecification());
            dto.setPrice(m.getPrice());
            dto.setOriginalPrice(m.getOriginalPrice());
            dto.setMainImageUrl(m.getMainImageUrl());
            dto.setIsPrescription(m.getIsPrescription());
            dto.setStock(m.getStock());
            return dto;
        }).collect(Collectors.toList());

        return new PageResult<>(list, result.getTotalElements(), page, pageSize);
    }

    /**
     * 获取药品详情。
     */
    public DrugDetailResponse getDetail(Long medicineId) {
        Medicine m = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new BusinessException(404, "药品不存在"));

        if (m.getStatus() != 1) {
            throw new BusinessException(404, "药品已下架");
        }

        DrugDetailResponse dto = new DrugDetailResponse();
        dto.setId(m.getId());
        dto.setName(m.getName());
        dto.setGenericName(m.getGenericName());
        dto.setSpecification(m.getSpecification());
        dto.setManufacturer(m.getManufacturer());
        dto.setIsPrescription(m.getIsPrescription());
        dto.setPrice(m.getPrice());
        dto.setOriginalPrice(m.getOriginalPrice());
        dto.setStock(m.getStock());
        dto.setMainImageUrl(m.getMainImageUrl());
        dto.setIndications(m.getIndications());
        dto.setDosage(m.getDosage());
        dto.setContraindications(m.getContraindications());

        // 加载评价
        Page<MedicineReview> reviews = reviewRepository
                .findByMedicineIdAndStatus(medicineId, 1, PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createTime")));
        List<DrugDetailResponse.ReviewItem> reviewList = reviews.getContent().stream().map(r -> {
            DrugDetailResponse.ReviewItem item = new DrugDetailResponse.ReviewItem();
            item.setUserName(r.getIsAnonymous() == 1 ? "匿名用户" : (r.getUser().getNickname() != null ? maskName(r.getUser().getNickname()) : "用户"));
            item.setRating(r.getRating());
            item.setContent(r.getContent());
            return item;
        }).collect(Collectors.toList());
        dto.setReviews(reviewList);

        return dto;
    }

    private Sort parseSort(String sort) {
        if (sort == null) return Sort.unsorted();
        return switch (sort) {
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "sales" -> Sort.unsorted(); // 需要另一个排序字段
            case "rating" -> Sort.unsorted();
            default -> Sort.unsorted();
        };
    }

    private String maskName(String name) {
        if (name == null || name.length() <= 1) return name;
        return name.charAt(0) + "**";
    }
}
