package com.lingxi.isi.application.service;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.RandomUtil;
import com.lingxi.isi.common.exception.CustomException;
import com.lingxi.isi.domain.model.entity.InterviewRoom;
import com.lingxi.isi.domain.model.entity.Resume;
import com.lingxi.isi.domain.repository.InterviewRoomRepository;
import com.lingxi.isi.domain.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewApplicationService {

    private final InterviewRoomRepository interviewRoomRepository;
    private final ResumeRepository resumeRepository;

    @Autowired
    private SnowflakeGenerator snowflakeGenerator;

    /**
     * 创建面试间
     */
    @Transactional
    public InterviewRoom createInterviewRoom(Long userId, Long resumeId, String title) {
        // 验证简历是否存在
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new CustomException("简历不存在"));

        // 检查是否已有进行中的面试
        List<InterviewRoom> ongoingRooms = interviewRoomRepository.findByResumeIdAndStatus(resumeId, 1);
        if (!ongoingRooms.isEmpty()) {
            throw new CustomException("该简历已有进行中的面试");
        }

        // 生成唯一房间号
        String roomId = generateRoomId();

        InterviewRoom room = new InterviewRoom();
        room.setId(snowflakeGenerator.next());
        room.setRoomId(roomId);
        room.setResumeId(resumeId);
        room.setUserId(userId);
        room.setTitle(title != null ? title : "面试-" + roomId);
        room.setStatus(0); // 待开始
        room.setAiEnabled(true);

        return interviewRoomRepository.save(room);
    }

    /**
     * 开始面试
     */
    @Transactional
    public InterviewRoom startInterview(Long roomId) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException("面试间不存在"));

        room.start();
        return interviewRoomRepository.save(room);
    }

    /**
     * 结束面试
     */
    @Transactional
    public InterviewRoom endInterview(Long roomId) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException("面试间不存在"));

        room.end();
        return interviewRoomRepository.save(room);
    }

    /**
     * HR 加入面试间
     */
    @Transactional
    public InterviewRoom hrJoinRoom(Long roomId, Long hrUserId) {
        InterviewRoom room = interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException("面试间不存在"));

        if (!room.canJoin()) {
            throw new CustomException("面试间已关闭或已结束");
        }

        room.setHrUserId(hrUserId);
        
        return interviewRoomRepository.save(room);
    }

    /**
     * 获取用户的面试列表
     */
    public List<InterviewRoom> getUserInterviews(Long userId) {
        return interviewRoomRepository.findByUserId(userId);
    }

    /**
     * 获取 HR 的面试列表
     */
    public List<InterviewRoom> getHrInterviews(Long hrUserId) {
        return interviewRoomRepository.findByHrUserId(hrUserId);
    }

    /**
     * 获取面试详情
     */
    public InterviewRoom getInterviewDetail(Long roomId) {
        return interviewRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException("面试间不存在"));
    }

    /**
     * 通过房间号获取面试间
     */
    public InterviewRoom getByRoomId(String roomId) {
        return interviewRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new CustomException("面试间不存在"));
    }

    /**
     * 生成唯一房间号
     */
    private String generateRoomId() {
        // 格式：ROOM-年月日时分秒 -6 位随机数
        LocalDateTime now = LocalDateTime.now();
        String timestamp = String.format("%d%02d%02d%02d%02d%02d",
                now.getYear(),
                now.getMonthValue(),
                now.getDayOfMonth(),
                now.getHour(),
                now.getMinute(),
                now.getSecond());
        
        String random = RandomUtil.randomNumbers(6);
        return "ROOM-" + timestamp + "-" + random;
    }
}
