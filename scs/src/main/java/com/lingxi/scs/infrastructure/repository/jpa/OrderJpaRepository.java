package com.lingxi.scs.infrastructure.repository.jpa;

import com.lingxi.scs.domain.model.entity.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单JPA仓储接口
 *
 * @author system
 */
@Repository
public interface OrderJpaRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
    Optional<Orders> findByNumber(String number);
    List<Orders> findByUserId(Long userId);
    List<Orders> findByStatus(Integer status);
    
    Page<Orders> findAll(Pageable pageable);
    
    default Page<Orders> findAllWithFilters(String number, LocalDateTime beginTime, 
                                             LocalDateTime endTime, Pageable pageable) {
        return findAll((org.springframework.data.jpa.domain.Specification<Orders>) (root, query, criteriaBuilder) -> {
            var predicates = new java.util.ArrayList<jakarta.persistence.criteria.Predicate>();
            
            if (number != null && !number.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("number"), "%" + number + "%"));
            }
            
            if (beginTime != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("orderTime"), beginTime));
            }
            
            if (endTime != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("orderTime"), endTime));
            }
            
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        }, pageable);
    }
}
