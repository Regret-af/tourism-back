package com.af.tourism.service.impl.app;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.common.constants.RedisKeyConstants;
import com.af.tourism.common.constants.RedisTtlConstants;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.integration.common.exception.ThirdPartyApiException;
import com.af.tourism.integration.qweather.QWeatherClient;
import com.af.tourism.integration.qweather.converter.QWeatherConverter;
import com.af.tourism.integration.qweather.dto.QWeatherDailyResponse;
import com.af.tourism.integration.qweather.dto.QWeatherNowResponse;
import com.af.tourism.integration.qweather.dto.QWeatherWarningResponse;
import com.af.tourism.mapper.AttractionMapper;
import com.af.tourism.pojo.dto.app.AttractionQueryDTO;
import com.af.tourism.pojo.entity.Attraction;
import com.af.tourism.pojo.vo.app.AttractionCardVO;
import com.af.tourism.pojo.vo.app.AttractionDetailVO;
import com.af.tourism.pojo.vo.app.AttractionWeatherVO;
import com.af.tourism.pojo.vo.app.WeatherAlertVO;
import com.af.tourism.pojo.vo.app.WeatherCurrentVO;
import com.af.tourism.pojo.vo.app.WeatherForecastVO;
import com.af.tourism.pojo.vo.common.PageResponse;
import com.af.tourism.service.app.AttractionService;
import com.af.tourism.service.cache.CacheClient;
import com.af.tourism.service.cache.CacheKeyBuilder;
import com.af.tourism.service.helper.AttractionCheckService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 景点服务实现。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AttractionServiceImpl implements AttractionService {

    private static final TypeReference<PageResponse<AttractionCardVO>> ATTRACTION_LIST_PAGE_TYPE =
            new TypeReference<PageResponse<AttractionCardVO>>() {
            };
    private static final TypeReference<AttractionWeatherVO> ATTRACTION_WEATHER_TYPE =
            new TypeReference<AttractionWeatherVO>() {
            };

    private final AttractionMapper attractionMapper;

    private final AttractionCheckService attractionCheckService;

    private final CacheClient cacheClient;
    private final CacheKeyBuilder cacheKeyBuilder;

    private final QWeatherClient qWeatherClient;

    private final QWeatherConverter qWeatherConverter;

    /**
     * 景点列表，覆盖列表、搜索、分类筛选。
     * @param queryDTO 列表、搜索、分类筛选参数
     * @return 景点列表
     */
    @Override
    public PageResponse<AttractionCardVO> listAttractions(AttractionQueryDTO queryDTO) {
        // 1.构建 Redis 中景点列表的 key
        String cacheKey = buildAttractionListCacheKey(queryDTO);

        // 2.在 Redis 中进行查询
        try {
            PageResponse<AttractionCardVO> cachedResponse = cacheClient.get(cacheKey, ATTRACTION_LIST_PAGE_TYPE);
            if (cachedResponse != null) {
                return cachedResponse;
            }
        } catch (Exception ex) {
            log.warn("读取景点列表缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 3.开启分页查询
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());

        // 4.在数据库中查询景点信息
        log.debug("查询景点列表，pageNum={}, pageSize={}, keyword={}, categoryId={}",
                queryDTO.getPageNum(),
                queryDTO.getPageSize(),
                queryDTO.getKeyword(),
                queryDTO.getCategoryId());
        List<AttractionCardVO> list = attractionMapper.selectAttractions(queryDTO);
        PageInfo<AttractionCardVO> pageInfo = new PageInfo<>(list);
        log.debug("查询景点信息完成，总记录数: {}", pageInfo.getTotal());

        // 5.封装返回信息
        PageResponse<AttractionCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        // 6.将返回值存入Redis
        try {
            cacheClient.set(cacheKey, response, RedisTtlConstants.ATTRACTION_LIST);
        } catch (Exception ex) {
            log.warn("写入景点列表缓存失败，cacheKey={}", cacheKey, ex);
        }

        return response;
    }

    /**
     * 景点详情
     * @param attractionId 景点id
     * @return 景点详情信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttractionDetailVO getAttractionDetail(Long attractionId) {
        // 1.构建 Redis 中景点分类的 key
        String cacheKey = cacheKeyBuilder.build(RedisKeyConstants.ATTRACTION_DETAIL, attractionId);

        // 2.增加景点浏览量
        attractionMapper.increaseViewCount(attractionId);

        // 3.查找 Redis 缓存，存在直接返回
        try {
            // 3.1.查找缓存
            AttractionDetailVO cachedDetail = cacheClient.get(cacheKey, AttractionDetailVO.class);
            if (cachedDetail != null) {
                // 3.2.浏览量+1，回写缓存
                cachedDetail.setViewCount((cachedDetail.getViewCount() == null ? 0 : cachedDetail.getViewCount()) + 1);
                cacheClient.set(cacheKey, cachedDetail, RedisTtlConstants.DEFAULT);
                return cachedDetail;
            }
        } catch (Exception ex) {
            log.warn("读取景点详情缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 4.在数据库中查询景点信息
        AttractionDetailVO detailVO = attractionMapper.selectAttractionDetail(attractionId);

        // 5.若为空，抛出异常
        if (detailVO == null) {
            log.warn("景点不存在，attractionId={}", attractionId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }

        // 6.封装telephoneList，对telephone字段进行数据清洗
        if (detailVO.getTelephone() != null) {
            detailVO.setTelephoneList(Arrays.asList(detailVO.getTelephone().split(",")));
        }

        // 7.将返回值存入Redis
        try {
            cacheClient.set(cacheKey, detailVO, RedisTtlConstants.DEFAULT);
        } catch (Exception ex) {
            log.warn("写入景点详情缓存失败，cacheKey={}", cacheKey, ex);
        }

        return detailVO;
    }

    /**
     * 查询景点天气信息、未来天气信息与预警信息
     * @param attractionId 景点 id
     * @return 景点天气信息
     */
    @Override
    public AttractionWeatherVO getAttractionWeather(Long attractionId) {
        // 1.校验景点状态
        Attraction attraction = attractionCheckService.requireAttraction(attractionId);
        AttractionWeatherVO weatherVO = new AttractionWeatherVO();

        // 2.构建 Redis 中景点分类的 key
        String cacheKey = buildAttractionWeatherCacheKey(attractionId);

        // 3.查找 Redis 缓存，存在直接返回
        try {
            AttractionWeatherVO cachedWeather = cacheClient.get(cacheKey, ATTRACTION_WEATHER_TYPE);
            if (cachedWeather != null) {
                return cachedWeather;
            }
        } catch (Exception ex) {
            log.warn("读取景点天气缓存失败，回源第三方接口，cacheKey={}", cacheKey, ex);
        }

        // 4.获取并校验调用外部API的参数
        BigDecimal longitude = attraction.getLongitude();
        BigDecimal latitude = attraction.getLatitude();
        if (longitude == null || latitude == null) {
            log.warn("查询景点天气失败，景点缺少经纬度，attractionId={}", attractionId);
            weatherVO.setAvailable(false);
            return weatherVO;
        }

        WeatherCurrentVO currentVO = null;
        List<WeatherForecastVO> forecastVOList = null;
        List<WeatherAlertVO> alertVOList = null;
        LocalDateTime sourceUpdateTime = null;

        // 5.调用外部API并处理各个字段
        // 5.1.获取实时天气
        try {
            // 5.1.1.请求API并将第三方返回实体改为业务实体
            QWeatherNowResponse weatherNow = qWeatherClient.getWeatherNow(longitude, latitude);
            currentVO = qWeatherConverter.toWeatherCurrentVO(weatherNow);
            sourceUpdateTime = weatherNow.getUpdateTime().toLocalDateTime();
        } catch (ThirdPartyApiException ex) {
            log.warn("获取实时天气失败，attractionId={}", attractionId, ex);
        }

        // 5.2.获取天气预报
        try {
            // 5.2.1.请求API并将第三方返回实体改为业务实体
            QWeatherDailyResponse weatherDaily = qWeatherClient.getWeatherDaily(longitude, latitude);
            forecastVOList = qWeatherConverter.toWeatherForecastVOList(weatherDaily.getDaily());

            // 5.2.2.补充 weekLabel 字段
            for (int i = 0; i < forecastVOList.size(); i++) {
                WeatherForecastVO forecast = forecastVOList.get(i);
                forecast.setWeekLabel(buildWeekLabel(i));
            }
        } catch (ThirdPartyApiException ex) {
            log.warn("获取天气预报失败，attractionId={}", attractionId, ex);
        }

        // 5.3.获取天气预警
        try {
            QWeatherWarningResponse weatherWarning = qWeatherClient.getWeatherWarning(longitude, latitude);

            // 5.3.1.查看是否有预警信息，有预警信息进行转换
            if (!weatherWarning.getMetadata().getZeroResult()) {
                alertVOList = qWeatherConverter.toWeatherAlertVOList(weatherWarning.getAlerts());
            }
        } catch (ThirdPartyApiException ex) {
            log.warn("获取天气预警失败，attractionId={}", attractionId, ex);
        }

        // 6.补充业务字段
        // 6.2.1.补充 isSuitable 字段
        if (currentVO != null) {
            currentVO.setIsSuitable(judgeSuitable(currentVO.getTemperature(), alertVOList));
            currentVO.setTravelTip(buildTravelTip(currentVO.getIsSuitable(), alertVOList));
        }

        if (forecastVOList != null) {
            List<WeatherAlertVO> finalAlertVOList = alertVOList;
            forecastVOList.forEach(forecastVO -> forecastVO.setIsSuitable(judgeSuitable(forecastVO.getTempMax(), finalAlertVOList)));
        }

        // 6.2.2.补充 travelTip 字段
        if (currentVO != null) {
            currentVO.setTravelTip(buildTravelTip(currentVO.getIsSuitable(), alertVOList));
        }

        // 7. 统一判断是否至少有一部分天气数据成功
        boolean hasWeatherData =
                currentVO != null
                        || (forecastVOList != null && !forecastVOList.isEmpty())
                        || (alertVOList != null && !alertVOList.isEmpty());

        if (!hasWeatherData) {
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "获取景点天气失败");
        }

        // 8.进行封装
        weatherVO.setAvailable(true);
        weatherVO.setSourceUpdateTime(sourceUpdateTime != null ? sourceUpdateTime : LocalDateTime.now());
        weatherVO.setCurrent(currentVO);
        weatherVO.setForecast(forecastVOList);
        weatherVO.setAlerts(alertVOList);

        // 9.将返回值存入Redis
        try {
            cacheClient.set(cacheKey, weatherVO, RedisTtlConstants.ATTRACTION_WEATHER);
        } catch (Exception ex) {
            log.warn("写入景点天气缓存失败，cacheKey={}", cacheKey, ex);
        }

        return weatherVO;
    }

    /**
     * 判断天气是否舒适
     * @param temp 温度
     * @param alerts 预警信息
     * @return 是否舒适
     */
    private Boolean judgeSuitable(Integer temp, List<WeatherAlertVO> alerts) {
        if (alerts != null && !alerts.isEmpty()) {
            return false;
        }

        if (temp >= 35) {
            return false;
        }

        return true;
    }

    /**
     * 构建旅行建议
     * @param isSuitable 是否舒适
     * @param alerts 预警信息
     * @return 旅行建议
     */
    private String buildTravelTip(Boolean isSuitable, List<WeatherAlertVO> alerts) {
        if (Boolean.TRUE.equals(isSuitable)) {
            return "当前天气较适合游览";
        }

        if (alerts != null && !alerts.isEmpty()) {
            return "当前天气存在预警，建议谨慎出行";
        }

        return "当前天气一般，建议合理安排行程";
    }

    /**
     * 构建日期标签
     * @param index 下标
     * @return 日期标签
     */
    private String buildWeekLabel(int index) {
        if (index == 0) {
            return "今天";
        }
        if (index == 1) {
            return "明天";
        }
        if (index == 2) {
            return "后天";
        }

        return null;
    }

    /**
     * 构建 Redis 中景点列表的 key
     * @param queryDTO 请求体
     * @return 构建好的 key
     */
    private String buildAttractionListCacheKey(AttractionQueryDTO queryDTO) {
        return cacheKeyBuilder.build(
                RedisKeyConstants.ATTRACTION_LIST,
                "pageNum", queryDTO.getPageNum(),
                "pageSize", queryDTO.getPageSize(),
                "keyword", normalizeKeyPart(queryDTO.getKeyword()),
                "categoryId", queryDTO.getCategoryId() == null ? "_" : queryDTO.getCategoryId(),
                "sort", normalizeKeyPart(queryDTO.getSortCode())
        );
    }

    /**
     * 构建景点天气缓存 key。
     * @param attractionId 景点 id
     * @return 缓存 key
     */
    private String buildAttractionWeatherCacheKey(Long attractionId) {
        return cacheKeyBuilder.build(RedisKeyConstants.ATTRACTION_WEATHER, attractionId);
    }

    /**
     * 查看 key 的组成部分，为空返回 _
     * @param value key的组成部分
     * @return 结果
     */
    private String normalizeKeyPart(String value) {
        return StringUtils.defaultIfBlank(value, "_").trim();
    }
}
