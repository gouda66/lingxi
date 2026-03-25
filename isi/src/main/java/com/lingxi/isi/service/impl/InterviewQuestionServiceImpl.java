package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.mapper.InterviewQuestionMapper;
import com.lingxi.isi.models.entity.InterviewQuestion;
import com.lingxi.isi.service.IInterviewQuestionService;
import org.springframework.stereotype.Service;

@Service
public class InterviewQuestionServiceImpl extends ServiceImpl<InterviewQuestionMapper, InterviewQuestion> implements IInterviewQuestionService {

}
