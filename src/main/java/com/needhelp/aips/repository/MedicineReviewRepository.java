package com.needhelp.aips.repository;

import com.needhelp.aips.entity.MedicineReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineReviewRepository extends JpaRepository<MedicineReview, Long> {
    Page<MedicineReview> findByMedicineIdAndStatus(Long medicineId, Integer status, Pageable pageable);
}
