package com.needhelp.aips.repository;

import com.needhelp.aips.entity.Prescription;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    Optional<Prescription> findByPrescriptionNo(String prescriptionNo);
    Page<Prescription> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    Page<Prescription> findByPharmacistStatus(Integer status, Pageable pageable);
}
