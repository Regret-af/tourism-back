package com.af.tourism.controller.app;

import com.af.tourism.common.ApiResponse;
import com.af.tourism.pojo.dto.app.NotificationQueryDTO;
import com.af.tourism.pojo.vo.app.NotificationReadVO;
import com.af.tourism.pojo.vo.app.NotificationUnreadCountVO;
import com.af.tourism.pojo.vo.app.NotificationVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.app.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.Min;

/**
 * 通知接口。
 */
@Validated
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 查询用户通知列表
     * @param queryDTO 请求参数
     * @return 通知列表
     */
    @GetMapping
    public ApiResponse<PageResponse<NotificationVO>> listNotifications(@Valid NotificationQueryDTO queryDTO) {
        // 获取用户id，若 id 为空，直接抛出异常
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(notificationService.listNotifications(userId, queryDTO));
    }

    /**
     * 未读数量统计
     * @return 未读数量
     */
    @GetMapping("/unread-count")
    public ApiResponse<NotificationUnreadCountVO> getUnreadCount() {
        // 获取用户id，若 id 为空，直接抛出异常
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(notificationService.getUnreadCount(userId));
    }

    /**
     * 单条通知已读
     * @param notificationId 已读通知 id
     * @return 已读信息
     */
    @PatchMapping("/{notificationId}/read")
    public ApiResponse<NotificationReadVO> markAsRead(@PathVariable("notificationId") @Min(value = 1, message = "notificationId不能小于1") Long notificationId) {
        // 获取用户id，若 id 为空，直接抛出异常
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(notificationService.markAsRead(userId, notificationId));
    }
}
