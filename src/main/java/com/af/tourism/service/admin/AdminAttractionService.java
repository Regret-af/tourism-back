package com.af.tourism.service.admin;

import com.af.tourism.pojo.dto.admin.AdminAttractionCreateDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionQueryDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionStatusUpdateDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionUpdateDTO;
import com.af.tourism.pojo.vo.admin.AttractionDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.AttractionForAdminVO;
import com.af.tourism.pojo.vo.admin.MapDetailVO;
import com.af.tourism.pojo.vo.admin.MapSuggestionVO;
import com.af.tourism.pojo.vo.common.PageResponse;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 管理端景点服务
 */
public interface AdminAttractionService {

    /**
     * 管理端景点列表
     * @param queryDTO 查询参数
     * @return 景点分页列表
     */
    PageResponse<AttractionForAdminVO> listAttractions(@Valid AdminAttractionQueryDTO queryDTO);

    /**
     * 管理端景点详情
     * @param id 景点 id
     * @return 景点详情信息
     */
    AttractionDetailForAdminVO getAttractionDetail(Long id);

    /**
     * 管理端新增景点
     * @param request 请求信息
     * @return 新增后的景点详情
     */
    AttractionDetailForAdminVO createAttraction(@Valid AdminAttractionCreateDTO request);

    /**
     * 编辑景点
     * @param id 景点 id
     * @param request 编辑请求
     * @return 编辑后的景点详情
     */
    AttractionDetailForAdminVO updateAttraction(Long id, @Valid AdminAttractionUpdateDTO request);

    /**
     * 修改景点状态
     * @param id 景点 id
     * @param request 状态修改请求
     * @return 修改后的景点详情
     */
    void updateAttractionStatus(Long id, @Valid AdminAttractionStatusUpdateDTO request);

    /**
     * 百度地点搜索
     * @param keyword 关键词
     * @return 地点信息列表
     */
    List<MapSuggestionVO> getMapSuggestion(@Valid @NotBlank String keyword);

    /**
     * 百度地点详情回填
     * @param uid 百度UID
     * @return 地点详细信息
     */
    MapDetailVO getMapDetail(@Valid @NotBlank String uid);
}
