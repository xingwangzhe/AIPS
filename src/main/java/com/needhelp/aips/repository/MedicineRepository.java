package com.needhelp.aips.repository;

import com.needhelp.aips.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    /** 关键字搜索：匹配名称、通用名、适应症、成分 */
    @Query("SELECT m FROM Medicine m WHERE m.status = 1 AND (" +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.indications) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.ingredients) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Medicine> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    /** 按分类 + 关键字搜索 */
    @Query("SELECT m FROM Medicine m WHERE m.status = 1 AND m.category.id = :categoryId AND (" +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(m.indications) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Medicine> searchByCategoryAndKeyword(@Param("categoryId") Long categoryId,
                                               @Param("keyword") String keyword,
                                               Pageable pageable);

    /** 按分类查找 */
    Page<Medicine> findByCategoryIdAndStatus(Long categoryId, Integer status, Pageable pageable);

    /** 按处方药类型筛选 */
    Page<Medicine> findByIsPrescriptionAndStatus(Integer isPrescription, Integer status, Pageable pageable);

    /** 按分类 + 处方药筛选 */
    @Query("SELECT m FROM Medicine m WHERE m.status = 1 AND m.category.id = :categoryId " +
           "AND (:isRx IS NULL OR m.isPrescription = :isRx)")
    Page<Medicine> findByCategoryAndRx(@Param("categoryId") Long categoryId,
                                        @Param("isRx") Integer isRx,
                                        Pageable pageable);

    /** 热门搜索：返回销量最高的 N 个药品名称 */
    @Query(value = "SELECT DISTINCT m.name FROM medicines m WHERE m.status = 1 ORDER BY " +
           "(SELECT COALESCE(SUM(oi.quantity), 0) FROM order_items oi WHERE oi.medicine_id = m.id) DESC LIMIT :limit",
           nativeQuery = true)
    List<String> findHotSearchTerms(@Param("limit") int limit);
}
