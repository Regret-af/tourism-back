package com.af.tourism.service.impl.admin;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.converter.AttractionConverter;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.integration.baidu.map.BaiduMapClient;
import com.af.tourism.integration.baidu.map.converter.BaiduMapConverter;
import com.af.tourism.integration.baidu.map.dto.BaiduMapDetailResponse;
import com.af.tourism.integration.baidu.map.dto.BaiduMapSuggestionResponse;
import com.af.tourism.mapper.AttractionCategoryMapper;
import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.pojo.dto.admin.AdminAttractionCreateDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionQueryDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionStatusUpdateDTO;
import com.af.tourism.pojo.dto.admin.AdminAttractionUpdateDTO;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.entity.AttractionCategory;
import com.af.tourism.pojo.vo.admin.AttractionDetailForAdminVO;
import com.af.tourism.pojo.vo.admin.AttractionForAdminVO;
import com.af.tourism.pojo.vo.admin.MapDetailVO;
import com.af.tourism.pojo.vo.admin.MapSuggestionVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.admin.AdminAttractionService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 管理端景点服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminAttractionServiceImpl implements AdminAttractionService {

    private final AttractionMapper attractionMapper;
    private final AttractionCategoryMapper attractionCategoryMapper;

    private final AttractionConverter attractionConverter;
    private final BaiduMapConverter baiduMapConverter;

    private final BaiduMapClient baiduMapClient;

    /**
     * 管理端景点列表
     * @param queryDTO 查询参数
     * @return 景点分页列表
     */
    @Override
    public PageResponse<AttractionForAdminVO> listAttractions(AdminAttractionQueryDTO queryDTO) {
        // 1.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 2.进行景点列表查询
        List<AttractionForAdminVO> list = attractionMapper.selectAdminAttractions(queryDTO);
        PageInfo<AttractionForAdminVO> pageInfo = new PageInfo<>(list);

        // 3.封装返回值
        PageResponse<AttractionForAdminVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        return response;
    }

    /**
     * 管理端景点详情
     * @param id 景点 id
     * @return 景点详情信息
     */
    @Override
    public AttractionDetailForAdminVO getAttractionDetail(Long id) {
        // 1.查询景点详情
        AttractionDetailForAdminVO detail = attractionMapper.selectAdminAttractionDetail(id);
        if (detail == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }

        return detail;
    }

    /**
     * 管理端新增景点
     * @param request 请求信息
     * @return 新增后的景点详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttractionDetailForAdminVO createAttraction(AdminAttractionCreateDTO request) {
        // 1.校验参数
        // 1.1.查询景点分类，判断是否存在该分类
        AttractionCategory category = attractionCategoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点分类不存在");
        }

        // 1.2.校验非空参数
        if (!StringUtils.hasText(request.getName()) || !StringUtils.hasText(request.getAddressDetail())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "景点名称或详细地址不能为空");
        }

        // 1.3.校验景点状态的合法性
        if (request.getStatus() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "景点状态不合法");
        }

        // 2.插入数据
        Attraction entity = attractionConverter.toAttraction(request);
        attractionMapper.insert(entity);

        return getAttractionDetail(entity.getId());
    }

    /**
     * 编辑景点
     * @param id 景点 id
     * @param request 编辑请求
     * @return 编辑后的景点详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttractionDetailForAdminVO updateAttraction(Long id, AdminAttractionUpdateDTO request) {
        // 1.校验参数
        // 1.1.获取景点信息，判断是否存在
        Attraction entity = attractionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }

        // 1.2.获取分类信息，判断是否存在该分类
        AttractionCategory category = attractionCategoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点分类不存在");
        }

        // 1.3.校验非空字段
        if (!StringUtils.hasText(request.getName()) || !StringUtils.hasText(request.getAddressDetail())) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "景点名称或详细地址不能为空");
        }

        // 1.4.校验景点状态的合法性
        if (request.getStatus() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "景点状态不合法");
        }

        // 2.更新数据
        Attraction updateEntity = attractionConverter.toAttraction(request);
        updateEntity.setId(id);
        attractionMapper.updateById(updateEntity);

        return getAttractionDetail(id);
    }

    /**
     * 修改景点状态
     * @param id 景点 id
     * @param request 状态修改请求
     * @return 修改后的景点详情
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAttractionStatus(Long id, AdminAttractionStatusUpdateDTO request) {
        // 1.校验参数
        // 1.1.校验景点是否存在并获取景点实体
        Attraction entity = attractionMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }

        // 1.2.校验景点状态的合法性
        if (request.getStatus() == null) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "景点状态不合法");
        }

        // 1.3.校验幂等性
        Integer status = request.getStatus().getValue();
        if (Objects.equals(entity.getStatus(), status)) {
            return;
        }

        // 2.修改景点状态
        entity.setStatus(status);
        attractionMapper.updateById(entity);
    }

    /**
     * 百度地点搜索
     * @param keyword 关键词
     * @return 地点信息列表
     */
    @Override
    public List<MapSuggestionVO> getMapSuggestion(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "关键词不能为空！");
        }

        BaiduMapSuggestionResponse response = baiduMapClient.getSuggestion(keyword);

        return baiduMapConverter.toMapSuggestionVOList(response.getResults());
    }

    /**
     * 百度地点详情回填
     * @param uid 百度UID
     * @return 地点详细信息
     */
    @Override
    public MapDetailVO getMapDetail(String uid) {
        if (!StringUtils.hasText(uid)) {
            throw new BusinessException(ErrorCode.PARAM_INVALID, "UID不能为空！");
        }

        BaiduMapDetailResponse response = baiduMapClient.getDetail(uid);
        List<MapDetailVO> voList = baiduMapConverter.toMapDetailVOList(response.getResults());

        return voList != null && !voList.isEmpty() ? voList.get(0) : null;
    }
}
