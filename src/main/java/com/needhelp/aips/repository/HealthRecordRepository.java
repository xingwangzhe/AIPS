package com.needhelp.aips.repository;

import com.needhelp.aips.entity.HealthRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthRecordRepository extends JpaRepository<HealthRecord, Long> {
    List<HealthRecord> findByUserIdAndRecordTypeOrderByRecordDateDesc(Long userId, Integer recordType);
    Optional<HealthRecord> findTopByUserIdAndRecordTypeOrderByRecordDateDesc(Long userId, Integer recordType);
    List<HealthRecord> findByUserIdOrderByRecordDateDesc(Long userId);
}
