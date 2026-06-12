package com.needhelp.aips.repository;

import com.needhelp.aips.entity.MedicineReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicineReminderRepository extends JpaRepository<MedicineReminder, Long> {
    List<MedicineReminder> findByUserIdAndIsEnabled(Long userId, Integer isEnabled);
}
