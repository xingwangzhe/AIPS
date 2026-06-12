package com.needhelp.aips.repository;

import com.needhelp.aips.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIdAndStatus(Long parentId, Integer status);
    List<Category> findByParentId(Long parentId);
}
