package com.lingxi.isi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.models.entity.InterviewQuestion;

import java.util.List;

public interface IInterviewQuestionService extends IService<InterviewQuestion> {

    /**
     * 获取会话的问题列表
     */
    R<List<InterviewQuestion>> listQuestions(Long sessionId);

    /**
     * 更新题目
     */
    R<Void> updateQuestion(InterviewQuestion question);
}
