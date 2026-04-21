package com.af.tourism.service.impl.app;

import com.af.tourism.common.ErrorCode;
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
import com.af.tourism.service.cache.CacheCounterSupport;
import com.af.tourism.service.cache.CacheKeySupport;
import com.af.tourism.service.helper.AttractionCheckService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final CacheKeySupport cacheKeySupport;
    private final CacheCounterSupport cacheCounterSupport;

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
        String cacheKey = cacheKeySupport.buildAttractionListKey(queryDTO);

        // 2.优先查询缓存，命中后补齐 Redis 中的实时浏览量
        try {
            PageResponse<AttractionCardVO> cachedResponse = cacheClient.get(cacheKey, ATTRACTION_LIST_PAGE_TYPE);
            if (cachedResponse != null) {
                cacheCounterSupport.fillAttractionCardViewCounts(cachedResponse.getList());
                return cachedResponse;
            }
        } catch (Exception ex) {
            log.warn("读取景点列表缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 3.缓存未命中时回源数据库
        PageHelper.startPage(queryDTO.getPageNum(), queryDTO.getPageSize());
        List<AttractionCardVO> list = attractionMapper.selectAttractions(queryDTO);
        PageInfo<AttractionCardVO> pageInfo = new PageInfo<>(list);

        // 4.封装返回结果
        PageResponse<AttractionCardVO> response = new PageResponse<>();
        response.setList(list);
        response.setPageNum(pageInfo.getPageNum());
        response.setPageSize(pageInfo.getPageSize());
        response.setTotal(pageInfo.getTotal());

        // 5.写入列表缓存，浏览量展示仍以 Redis 总量为准
        try {
            cacheClient.set(cacheKey, response, RedisTtlConstants.ATTRACTION_LIST);
        } catch (Exception ex) {
            log.warn("写入景点列表缓存失败，cacheKey={}", cacheKey, ex);
        }

        cacheCounterSupport.fillAttractionCardViewCounts(response.getList());
        return response;
    }

    /**
     * 景点详情
     * @param attractionId 景点 id
     * @return 景点详情信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public AttractionDetailVO getAttractionDetail(Long attractionId) {
        // 1.构建 Redis 中景点详情的 key
        String cacheKey = cacheKeySupport.buildAttractionDetailKey(attractionId);

        // 2.优先读取详情缓存，命中后同步总量 key 并做浏览量双写
        try {
            AttractionDetailVO cachedDetail = cacheClient.get(cacheKey, AttractionDetailVO.class);
            if (cachedDetail != null) {
                // 2.1.将浏览量总量写入缓存
                cacheCounterSupport.syncAttractionViewCount(attractionId, cachedDetail.getViewCount());
                // 2.2.将浏览量增量写入缓存
                cacheCounterSupport.incrementAttractionViewCount(attractionId, 1);
                // 2.3.填充浏览量数据
                cacheCounterSupport.fillAttractionViewCount(cachedDetail, attractionId);
                // 2.4.将景点详情基础信息写入缓存
                cacheClient.set(cacheKey, cachedDetail, RedisTtlConstants.DEFAULT);
                return cachedDetail;
            }
        } catch (Exception ex) {
            log.warn("读取景点详情缓存失败，回源数据库，cacheKey={}", cacheKey, ex);
        }

        // 3.缓存未命中时查询数据库
        AttractionDetailVO detailVO = attractionMapper.selectAttractionDetail(attractionId);
        if (detailVO == null) {
            log.warn("景点不存在，attractionId={}", attractionId);
            throw new BusinessException(ErrorCode.NOT_FOUND, "景点不存在");
        }

        // 4.清洗联系方式字段
        if (detailVO.getTelephone() != null) {
            detailVO.setTelephoneList(Arrays.asList(detailVO.getTelephone().split(",")));
        }

        // 5.初始化 Redis 总量并追加本次浏览，再回写详情缓存
        try {
            cacheCounterSupport.syncAttractionViewCount(attractionId, detailVO.getViewCount());
            cacheCounterSupport.incrementAttractionViewCount(attractionId, 1);
            cacheCounterSupport.fillAttractionViewCount(detailVO, attractionId);
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

        // 2.构建 Redis 中天气缓存 key
        String cacheKey = cacheKeySupport.buildAttractionWeatherKey(attractionId);

        // 3.优先读取天气缓存
        try {
            AttractionWeatherVO cachedWeather = cacheClient.get(cacheKey, ATTRACTION_WEATHER_TYPE);
            if (cachedWeather != null) {
                return cachedWeather;
            }
        } catch (Exception ex) {
            log.warn("读取景点天气缓存失败，回源第三方接口，cacheKey={}", cacheKey, ex);
        }

        // 4.获取并校验调用外部 API 的参数
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

        // 5.调用外部 API 并转换为业务对象
        try {
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

        // 7.至少有一部分天气数据成功时才返回成功结果
        boolean hasWeatherData =
                currentVO != null
                        || (forecastVOList != null && !forecastVOList.isEmpty())
                        || (alertVOList != null && !alertVOList.isEmpty());

        if (!hasWeatherData) {
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "获取景点天气失败");
        }

        // 8.封装结果
        weatherVO.setAvailable(true);
        weatherVO.setSourceUpdateTime(sourceUpdateTime != null ? sourceUpdateTime : LocalDateTime.now());
        weatherVO.setCurrent(currentVO);
        weatherVO.setForecast(forecastVOList);
        weatherVO.setAlerts(alertVOList);

        // 9.写入天气缓存
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
     * 构建旅行建议。
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
     * 构建日期标签。
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
}
