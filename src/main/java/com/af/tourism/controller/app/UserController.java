package com.af.tourism.controller.app;

import com.af.tourism.annotation.OperationLogRecord;
import com.af.tourism.common.ApiResponse;
import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.pojo.dto.DiaryQueryDTO;
import com.af.tourism.pojo.dto.FavoriteDiaryQueryDTO;
import com.af.tourism.pojo.dto.UserPasswordUpdateDTO;
import com.af.tourism.pojo.dto.UserProfileUpdateDTO;
import com.af.tourism.pojo.vo.*;
import com.af.tourism.securitylite.AuthContext;
import com.af.tourism.service.DiaryFavoriteService;
import com.af.tourism.service.DiaryService;
import com.af.tourism.service.UserService;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<UserVO> me() {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(userService.getCurrentUserProfile(userId));
    }

    /**
     * 更新当前用户资料
     * @param profileUpdateDTO 用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/me/profile")
    @OperationLogRecord(module = OperationLogModule.USER, action = OperationLogAction.UPDATE_PROFILE, description = "用户修改个人信息", userIdField = "data.id")
    public ApiResponse<UserPublicVO> updateProfile(@Valid @RequestBody UserProfileUpdateDTO profileUpdateDTO) {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(userService.updateUserProfile(userId, profileUpdateDTO));
    }

    /**
     * 修改当前用户密码
     * @param passwordUpdateDTO 新旧密码
     * @return 操作结果
     */
    @PutMapping("/me/password")
    @OperationLogRecord(module = OperationLogModule.USER, action = OperationLogAction.UPDATE_PASSWORD, description = "用户修改密码")
    public ApiResponse<Boolean> updatePassword(@Valid @RequestBody UserPasswordUpdateDTO passwordUpdateDTO) {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(userService.updatePassword(userId, passwordUpdateDTO));
    }

    /**
     * 获取我的日记
     * @param queryDTO 排序分页参数
     * @return 分页后的当前用户日记列表
     */
    @GetMapping("me/travel-diaries")
    public ApiResponse<PageResponse<MyDiaryProfileCardVO>> getTravelDiaries(@Valid DiaryQueryDTO queryDTO) {
        Long userId = AuthContext.requireCurrentUserId();
        return ApiResponse.ok(diaryService.listMyDiaries(userId, queryDTO));
    }

    /**
     * 获取我的收藏
     * @param queryDTO 分页参数
     * @return 分页后的当前用户收藏日记列表
     */
    @GetMapping("me/favorite-diaries")
    public ApiResponse<PageResponse<FavoriteDiaryCardVO>> getFavoriteDiaries(@Valid FavoriteDiaryQueryDTO queryDTO) {
        Long userId = AuthContext.requireCurrentUserId();
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
