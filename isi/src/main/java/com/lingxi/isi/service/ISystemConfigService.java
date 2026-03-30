package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.SystemConfig;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface ISystemConfigService extends IService<SystemConfig> {
    
    R getConfigKey(String configKey);

    R listUser(HttpServletRequest request);

    R getUser(Long userId);

    R addUser(Map<String, Object> data);

    R updateUser(Map<String, Object> data);

    R deleteUser(String userIds);

    R resetUserPwd(Map<String, Object> data);

    R changeUserStatus(Map<String, Object> data);

    R getDeptTree();
}
