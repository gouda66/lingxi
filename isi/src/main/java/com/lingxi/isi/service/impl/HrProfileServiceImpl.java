package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.mapper.HrProfileMapper;
import com.lingxi.isi.models.entity.HrProfile;
import com.lingxi.isi.service.IHrProfileService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * HR 信息表 服务实现类
 * </p>
 */
@Service
public class HrProfileServiceImpl extends ServiceImpl<HrProfileMapper, HrProfile> implements IHrProfileService {

}
