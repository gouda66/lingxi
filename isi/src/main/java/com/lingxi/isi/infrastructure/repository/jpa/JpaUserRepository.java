package com.lingxi.isi.infrastructure.repository.jpa;

import com.lingxi.isi.domain.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    List<User> findByRole(String role);

    List<User> findByCompanyId(String companyId);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);
    
    /**
     * 根据用户 ID 查询菜单权限标识
     */
    @org.springframework.data.jpa.repository.Query("SELECT m.perms FROM Menu m WHERE m.id IN " +
           "(SELECT rm.menuId FROM RoleMenu rm WHERE rm.roleId IN " +
           "(SELECT ur.roleId FROM UserRole ur WHERE ur.userId = ?1)) " +
           "AND m.hidden = 0 AND m.perms IS NOT NULL")
    java.util.List<String> findPermissionsByUserId(Long userId);
}
