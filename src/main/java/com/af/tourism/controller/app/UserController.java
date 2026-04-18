package com.af.tourism.controller.app;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.app.DiaryQueryDTO;
import com.af.tourism.pojo.dto.app.FavoriteDiaryQueryDTO;
import com.af.tourism.pojo.dto.app.UserPasswordUpdateDTO;
import com.af.tourism.pojo.dto.app.UserProfileUpdateDTO;
import com.af.tourism.pojo.vo.app.DiaryProfileCardVO;
import com.af.tourism.pojo.vo.app.FavoriteDiaryCardVO;
import com.af.tourism.pojo.vo.app.MyDiaryDetailVO;
import com.af.tourism.pojo.vo.app.MyDiaryProfileCardVO;
import com.af.tourism.pojo.vo.app.UserPublicVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.pojo.vo.common.UserVO;
import com.af.tourism.security.SecurityUser;
import com.af.tourism.security.SecurityUtils;
import com.af.tourism.service.app.DiaryFavoriteService;
import com.af.tourism.service.app.DiaryService;
import com.af.tourism.service.app.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户相关接口。
 */
@RestController
@Validated
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final DiaryService diaryService;
    private final DiaryFavoriteService diaryFavoriteService;

    /**
     * 获取当前用户信息
     * @return 当前用户信息
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<UserVO> me(@AuthenticationPrincipal SecurityUser currentUser) {
        return ApiResponse.ok(userService.getCurrentUserProfile(currentUser.getUserId()));
    }

    /**
     * 更新当前用户资料
     * @param profileUpdateDTO 用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/me/profile")
    @PreAuthorize("isAuthenticated()")
    @OperationLogRecord(module = OperationLogModule.USER, action = OperationLogAction.UPDATE_PROFILE, description = "用户修改个人信息", userIdField = "data.id")
    public ApiResponse<UserPublicVO> updateProfile(@Valid @RequestBody UserProfileUpdateDTO profileUpdateDTO) {
        Long userId = SecurityUtils.requireCurrentUserId();
        return ApiResponse.ok(userService.updateUserProfile(userId, profileUpdateDTO));
    }

    /**
     * 修改当前用户密码
     * @param passwordUpdateDTO 新旧密码
     * @return 操作结果
     */
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    @OperationLogRecord(module = OperationLogModule.USER, action = OperationLogAction.UPDATE_PASSWORD, description = "用户修改密码")
    public ApiResponse<Boolean> updatePassword(@Valid @RequestBody UserPasswordUpdateDTO passwordUpdateDTO) {
        Long userId = SecurityUtils.requireCurrentUserId();
        return ApiResponse.ok(userService.updatePassword(userId, passwordUpdateDTO));
    }

    /**
     * 获取我的日记
     * @param queryDTO 排序分页参数
     * @return 分页后的当前用户日记列表
     */
    @GetMapping("me/travel-diaries")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PageResponse<MyDiaryProfileCardVO>> getTravelDiaries(@Valid DiaryQueryDTO queryDTO) {
        Long userId = SecurityUtils.requireCurrentUserId();
        return ApiResponse.ok(diaryService.listMyDiaries(userId, queryDTO));
    }

    /**
     * 查询我的单篇日记详情
     * @param diaryId 日记 id
     * @return 我的日记详情
     */
    @GetMapping("me/travel-diaries/{diaryId}")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<MyDiaryDetailVO> getMyDiaryDetail(@PathVariable("diaryId") Long diaryId) {
        Long userId = SecurityUtils.requireCurrentUserId();
        return ApiResponse.ok(diaryService.getMyDiaryDetail(diaryId, userId));
    }

    /**
     * 获取我的收藏
     * @param queryDTO 分页参数
     * @return 分页后的当前用户收藏日记列表
     */
    @GetMapping("me/favorite-diaries")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PageResponse<FavoriteDiaryCardVO>> getFavoriteDiaries(@Valid FavoriteDiaryQueryDTO queryDTO) {
        Long userId = SecurityUtils.requireCurrentUserId();
        return ApiResponse.ok(diaryFavoriteService.listFavoriteDiaries(userId, queryDTO));
    }

    /**
     * 获取他人主页日记
     * @param userId 用户 id
     * @param queryDTO 排序分页参数
     * @return 分页后的用户日记列表
     */
    @GetMapping("{userId}/travel-diaries")
    public ApiResponse<PageResponse<DiaryProfileCardVO>> getUserPublicDiaries(@PathVariable Long userId, @Valid DiaryQueryDTO queryDTO) {
        return ApiResponse.ok(diaryService.listUserPublicDiaries(userId, queryDTO));
    }
}
