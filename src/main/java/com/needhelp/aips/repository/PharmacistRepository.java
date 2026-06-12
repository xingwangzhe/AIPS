package com.needhelp.aips.repository;

import com.needhelp.aips.entity.Pharmacist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PharmacistRepository extends JpaRepository<Pharmacist, Long> {
    List<Pharmacist> findByStatus(Integer status);
}
