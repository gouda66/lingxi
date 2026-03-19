package com.lingxi.isi.interfaces.rest;

import com.lingxi.isi.application.service.InterviewApplicationService;
import com.lingxi.isi.common.result.R;
import com.lingxi.isi.domain.model.entity.InterviewRoom;
import com.lingxi.isi.infrastructure.filter.LoginCheckFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 面试管理接口
 */
@RestController
@RequestMapping("/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewApplicationService interviewApplicationService;

    /**
     * 创建面试间
     */
    @PostMapping("/room/create")
    public R<InterviewRoom> createInterviewRoom(@RequestParam Long resumeId, 
                                                 @RequestParam(required = false) String title) {
        Long userId = LoginCheckFilter.getCurrentUserId();
        InterviewRoom room = interviewApplicationService.createInterviewRoom(userId, resumeId, title);
        return R.success(room);
    }

    /**
     * 开始面试
     */
    @PostMapping("/room/{roomId}/start")
    public R<InterviewRoom> startInterview(@PathVariable Long roomId) {
        InterviewRoom room = interviewApplicationService.startInterview(roomId);
        return R.success(room);
    }

    /**
     * 结束面试
     */
    @PostMapping("/room/{roomId}/end")
    public R<InterviewRoom> endInterview(@PathVariable Long roomId) {
        InterviewRoom room = interviewApplicationService.endInterview(roomId);
        return R.success(room);
    }

    /**
     * HR 加入面试间
     */
    @PostMapping("/room/{roomId}/join")
    public R<InterviewRoom> hrJoinRoom(@PathVariable Long roomId) {
        Long hrUserId = LoginCheckFilter.getCurrentUserId();
        InterviewRoom room = interviewApplicationService.hrJoinRoom(roomId, hrUserId);
        return R.success(room);
    }

    /**
     * 获取我的面试列表
     */
    @GetMapping("/my/list")
    public R<List<InterviewRoom>> getMyInterviews() {
        Long userId = LoginCheckFilter.getCurrentUserId();
        List<InterviewRoom> rooms = interviewApplicationService.getUserInterviews(userId);
        return R.success(rooms);
    }

    /**
     * 获取面试详情
     */
    @GetMapping("/room/{roomId}")
    public R<InterviewRoom> getInterviewDetail(@PathVariable Long roomId) {
        InterviewRoom room = interviewApplicationService.getInterviewDetail(roomId);
        return R.success(room);
    }

    /**
     * 通过房间号加入面试（前端可以通过房间号直接访问）
     */
    @GetMapping("/room/code/{roomCode}")
    public R<InterviewRoom> getByRoomCode(@PathVariable String roomCode) {
        InterviewRoom room = interviewApplicationService.getByRoomId(roomCode);
        return R.success(room);
    }
}
