package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.mapper.SysRoleMapper;
import com.lingxi.isi.models.entity.SysRole;
import com.lingxi.isi.models.request.ChangeRoleStatusRequest;
import com.lingxi.isi.models.request.RoleListRequest;
import com.lingxi.isi.models.request.RoleRequest;
import com.lingxi.isi.service.ISysRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
    private final SysRoleMapper sysRoleMapper;
    
    public SysRoleServiceImpl(SysRoleMapper sysRoleMapper) {
        this.sysRoleMapper = sysRoleMapper;
    }
    
    @Override
    public List<Map<String, Object>> getRoleOptions() {
        // 查询所有正常状态的角色
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getStatus, "0");
        List<SysRole> roleList = sysRoleMapper.selectList(queryWrapper);
        
        // 转换为 Map 列表
        List<Map<String, Object>> result = new ArrayList<>();
        for (SysRole role : roleList) {
            Map<String, Object> map = new HashMap<>();
            map.put("roleId", role.getRoleId());
            map.put("roleName", role.getRoleName());
            map.put("roleKey", role.getRoleKey());
            map.put("status", role.getStatus());
            result.add(map);
        }
        
        return result;
    }

    @Override
    public R<Map<String, Object>> listRoles(RoleListRequest request) {
        // 构建查询条件
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(request.getRoleName())) {
            queryWrapper.like(SysRole::getRoleName, request.getRoleName());
        }
        if (StringUtils.hasText(request.getRoleKey())) {
            queryWrapper.like(SysRole::getRoleKey, request.getRoleKey());
        }
        if (StringUtils.hasText(request.getStatus())) {
            queryWrapper.eq(SysRole::getStatus, request.getStatus());
        }
        
        // 添加时间范围查询
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (StringUtils.hasText(request.getStartTime())) {
            LocalDate beginDate = LocalDate.parse(request.getStartTime(), formatter);
            LocalDateTime beginDateTime = beginDate.atStartOfDay();
            queryWrapper.ge(SysRole::getCreatedAt, beginDateTime);
        }
        if (StringUtils.hasText(request.getEndTime())) {
            LocalDate endDate = LocalDate.parse(request.getEndTime(), formatter);
            LocalDateTime endDateTime = endDate.atTime(23, 59, 59);
            queryWrapper.le(SysRole::getCreatedAt, endDateTime);
        }
        
        // 分页查询
        Page<SysRole> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<SysRole> resultPage = this.page(page, queryWrapper);
        
        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("rows", resultPage.getRecords());
        result.put("total", resultPage.getTotal());
        
        return R.success(result);
    }

    @Override
    public R<SysRole> getRoleById(Long roleId) {
        SysRole role = this.getById(roleId);
        if (role == null) {
            return R.error("角色不存在");
        }
        return R.success(role);
    }

    @Override
    public R<Void> addRole(RoleRequest request) {
        // 校验角色名称是否已存在
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleName, request.getRoleName());
        SysRole existRole = this.getOne(queryWrapper);
        if (existRole != null) {
            return R.error("角色名称已存在");
        }
        
        // 校验权限字符是否已存在
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleKey, request.getRoleKey());
        existRole = this.getOne(queryWrapper);
        if (existRole != null) {
            return R.error("权限字符已存在");
        }
        
        // 创建角色实体
        SysRole role = new SysRole();
        BeanUtils.copyProperties(request, role);
        
        // 设置默认值
        if (role.getStatus() == null || role.getStatus().isEmpty()) {
            role.setStatus("0");
        }
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        
        // 保存角色
        boolean saved = this.save(role);
        return saved ? R.success(null) : R.error("新增失败");
    }

    @Override
    public R<Void> updateRole(RoleRequest request) {
        // 校验角色是否存在
        SysRole existRole = this.getById(request.getRoleId());
        if (existRole == null) {
            return R.error("角色不存在");
        }
        
        // 校验角色名称是否已被其他角色使用
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleName, request.getRoleName());
        queryWrapper.ne(SysRole::getRoleId, request.getRoleId());
        SysRole duplicateRole = this.getOne(queryWrapper);
        if (duplicateRole != null) {
            return R.error("角色名称已存在");
        }
        
        // 校验权限字符是否已被其他角色使用
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleKey, request.getRoleKey());
        queryWrapper.ne(SysRole::getRoleId, request.getRoleId());
        duplicateRole = this.getOne(queryWrapper);
        if (duplicateRole != null) {
            return R.error("权限字符已存在");
        }
        
        // 更新角色实体
        BeanUtils.copyProperties(request, existRole);
        existRole.setUpdatedAt(LocalDateTime.now());
        
        // 更新角色
        boolean updated = this.updateById(existRole);
        return updated ? R.success(null) : R.error("修改失败");
    }

    @Override
    public R<Void> deleteRoles(String roleIds) {
        if (roleIds == null || roleIds.trim().isEmpty()) {
            return R.error("角色 ID 不能为空");
        }
        
        // 解析角色 ID 列表
        String[] idArray = roleIds.split(",");
        
        for (String idStr : idArray) {
            try {
                Long roleId = Long.parseLong(idStr.trim());
                
                // 检查角色是否存在
                SysRole role = this.getById(roleId);
                if (role == null) {
                    return R.error("角色不存在：" + roleId);
                }
                
                // 删除角色
                boolean deleted = this.removeById(roleId);
                if (!deleted) {
                    return R.error("删除失败：" + roleId);
                }
            } catch (NumberFormatException e) {
                return R.error("无效的角色 ID 格式：" + idStr);
            }
        }
        
        return R.success(null);
    }

    @Override
    public R<Void> changeStatus(ChangeRoleStatusRequest request) {
        // 校验角色是否存在
        SysRole existRole = this.getById(request.getRoleId());
        if (existRole == null) {
            return R.error("角色不存在");
        }
        
        // 更新状态
        existRole.setStatus(request.getStatus());
        existRole.setUpdatedAt(LocalDateTime.now());
        
        boolean updated = this.updateById(existRole);
        return updated ? R.success(null) : R.error("修改失败");
    }
}
