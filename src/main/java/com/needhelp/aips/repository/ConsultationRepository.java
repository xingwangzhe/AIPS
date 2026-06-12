package com.needhelp.aips.repository;

import com.needhelp.aips.entity.Consultation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Page<Consultation> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    Page<Consultation> findByStatus(Integer status, Pageable pageable);
}
