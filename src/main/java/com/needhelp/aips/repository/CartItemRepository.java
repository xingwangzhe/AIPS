package com.needhelp.aips.repository;

import com.needhelp.aips.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUserIdOrderByCreateTimeDesc(Long userId);
    Optional<CartItem> findByUserIdAndMedicineId(Long userId, Long medicineId);
    List<CartItem> findByUserIdAndIsSelected(Long userId, Integer isSelected);
    long countByUserId(Long userId);
    void deleteByUserIdAndIsSelected(Long userId, Integer isSelected);
}
