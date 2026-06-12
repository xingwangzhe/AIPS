package com.needhelp.aips.repository;

import com.needhelp.aips.entity.ConsultationMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultationMessageRepository extends JpaRepository<ConsultationMessage, Long> {
    List<ConsultationMessage> findByConsultationIdOrderByCreateTimeAsc(Long consultationId);
}
