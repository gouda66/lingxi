package com.lingxi.isi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.mapper.InterviewQuestionMapper;
import com.lingxi.isi.models.entity.InterviewQuestion;
import com.lingxi.isi.service.IInterviewQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterviewQuestionServiceImpl extends ServiceImpl<InterviewQuestionMapper, InterviewQuestion> implements IInterviewQuestionService {

    @Override
    public R<List<InterviewQuestion>> listQuestions(Long sessionId) {
        LambdaQueryWrapper<InterviewQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InterviewQuestion::getSessionId, sessionId)
               .orderByAsc(InterviewQuestion::getSequenceNo);
        List<InterviewQuestion> questions = this.list(wrapper);
        return R.success(questions);
    }

    @Override
    public R<Void> updateQuestion(InterviewQuestion question) {
        this.updateById(question);
        return R.success(null);
    }
}
