package com.needhelp.aips.controller;

import com.needhelp.aips.common.dto.ApiResponse;
import com.needhelp.aips.common.dto.PageResult;
import com.needhelp.aips.dto.drug.CategoryResponse;
import com.needhelp.aips.dto.drug.DrugDetailResponse;
import com.needhelp.aips.dto.drug.DrugSearchResponse;
import com.needhelp.aips.service.drug.DrugService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 药品接口。
 * GET /api/v1/drug/categories
 * GET /api/v1/drug/search
 * GET /api/v1/drug/{medicineId}
 */
@RestController
@RequestMapping("/api/v1/drug")
public class DrugController {

    private final DrugService drugService;

    public DrugController(DrugService drugService) {
        this.drugService = drugService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> getCategories(
            @RequestParam(required = false) Long parentId) {
        List<CategoryResponse> categories = drugService.getCategories(parentId);
        return ApiResponse.success(categories);
    }

    @GetMapping("/search")
    public ApiResponse<PageResult<DrugSearchResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer isRx,
            @RequestParam(required = false) String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        PageResult<DrugSearchResponse> result = drugService.searchDrugs(
                keyword, categoryId, isRx, sort, page, pageSize);
        return ApiResponse.success(result);
    }

    @GetMapping("/{medicineId}")
    public ApiResponse<DrugDetailResponse> getDetail(@PathVariable Long medicineId) {
        DrugDetailResponse detail = drugService.getDetail(medicineId);
        return ApiResponse.success(detail);
    }
}
