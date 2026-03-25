package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.mapper.EmailRecordMapper;
import com.lingxi.isi.models.entity.EmailRecord;
import com.lingxi.isi.service.IEmailRecordService;
import org.springframework.stereotype.Service;

@Service
public class EmailRecordServiceImpl extends ServiceImpl<EmailRecordMapper, EmailRecord> implements IEmailRecordService {

}
