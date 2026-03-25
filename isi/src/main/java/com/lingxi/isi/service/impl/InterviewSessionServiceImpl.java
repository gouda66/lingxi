package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.mapper.InterviewSessionMapper;
import com.lingxi.isi.models.entity.InterviewSession;
import com.lingxi.isi.service.IInterviewSessionService;
import org.springframework.stereotype.Service;

@Service
public class InterviewSessionServiceImpl extends ServiceImpl<InterviewSessionMapper, InterviewSession> implements IInterviewSessionService {

}
