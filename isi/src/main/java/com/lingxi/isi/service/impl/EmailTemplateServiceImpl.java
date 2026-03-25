package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.mapper.EmailTemplateMapper;
import com.lingxi.isi.models.entity.EmailTemplate;
import com.lingxi.isi.service.IEmailTemplateService;
import org.springframework.stereotype.Service;

@Service
public class EmailTemplateServiceImpl extends ServiceImpl<EmailTemplateMapper, EmailTemplate> implements IEmailTemplateService {

}
