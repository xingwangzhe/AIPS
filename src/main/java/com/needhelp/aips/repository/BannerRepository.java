package com.needhelp.aips.repository;

import com.needhelp.aips.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {
    List<Banner> findByStatusOrderBySortOrderAsc(Integer status);
}
