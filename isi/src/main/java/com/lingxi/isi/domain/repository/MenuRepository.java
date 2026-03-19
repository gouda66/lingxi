package com.lingxi.isi.domain.repository;

import com.lingxi.isi.domain.model.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 菜单仓库接口
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {

    /**
     * 根据用户 ID 查询菜单权限
     */
    @Query("SELECT m FROM Menu m WHERE m.id IN " +
           "(SELECT rm.menuId FROM RoleMenu rm WHERE rm.roleId IN " +
           "(SELECT ur.roleId FROM UserRole ur WHERE ur.userId = ?1)) " +
           "AND m.hidden = 0 ORDER BY m.orderNum")
    List<Menu> findMenusByUserId(Long userId);

    /**
     * 查询所有可见的菜单
     */
    List<Menu> findByHiddenOrderByOrderNum(Integer visible);
}
