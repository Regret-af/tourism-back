package com.af.tourism.integration.qweather;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.integration.common.exception.ThirdPartyApiException;
import com.af.tourism.integration.common.helper.RestClientHelper;
import com.af.tourism.integration.qweather.config.QWeatherProperties;
import com.af.tourism.integration.qweather.dto.QWeatherDailyResponse;
import com.af.tourism.integration.qweather.dto.QWeatherNowResponse;
import com.af.tourism.integration.qweather.dto.QWeatherWarningResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 和风天气客户端。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class QWeatherClient {

    private static final String WEATHER_NOW_PATH = "/v7/weather/now";
    private static final String WEATHER_DAILY_PATH = "/v7/weather/3d";
    private static final String WEATHER_WARNING_PATH = "/weatheralert/v1/current/";

    private final RestClientHelper restClientHelper;
    private final QWeatherProperties properties;

    /**
     * 查询现在的天气信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return 现在的天气信息
     */
    public QWeatherNowResponse getWeatherNow(BigDecimal longitude, BigDecimal latitude) {

        Map<String, String> params = buildLocation(longitude, latitude);

        QWeatherNowResponse response = get(WEATHER_NOW_PATH, params, QWeatherNowResponse.class);

        if (!"200".equals(response.getCode())) {
            log.error("和风天气业务异常 location={} response={}", params.get("location"), response);
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "天气服务返回异常");
        }

        return response;
    }

    /**
     * 查询未来几天的天气信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return 未来几天的天气信息
     */
    public QWeatherDailyResponse getWeatherDaily(BigDecimal longitude, BigDecimal latitude) {
        Map<String, String> params = buildLocation(longitude, latitude);

        QWeatherDailyResponse response = get(WEATHER_DAILY_PATH, params, QWeatherDailyResponse.class);

        if (!"200".equals(response.getCode())) {
            log.error("和风天气业务异常 location={} response={}", params.get("location"), response);
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "天气服务返回异常");
        }

        return response;
    }

    /**
     * 查询天气预警信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return 天气预警信息
     */
    public QWeatherWarningResponse getWeatherWarning(BigDecimal longitude, BigDecimal latitude) {

        String location = latitude.stripTrailingZeros().toPlainString()
                + "/"
                + longitude.stripTrailingZeros().toPlainString();

        QWeatherWarningResponse response = get(WEATHER_WARNING_PATH + location,
                null,
                QWeatherWarningResponse.class
        );

        if (response.getMetadata() == null || response.getMetadata().getZeroResult() == null) {
            log.error("天气预警响应结构异常 location={} response={}", location, response);
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "天气预警服务返回异常");
        }

        return response;
    }

    /**
     * 发送 Get 请求
     * @param path 请求路径
     * @param params 请求参数
     * @param responseType 返回类型
     * @return 返回结果
     * @param <T> 返回类型
     */
    private <T> T get(String path, Map<String, String> params, Class<T> responseType) {
        if (!StringUtils.hasText(properties.getToken())) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "和风天气未配置token");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-QW-Api-Key", properties.getToken());

        T body = restClientHelper.doGet(
                properties.getBaseUrl(),
                path,
                params,
                headers,
                responseType
        );

        if (body == null) {
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "天气服务返回为空");
        }

        return body;
    }

    /**
     * 构建 location 参数
     * @param longitude 经度
     * @param latitude 纬度
     * @return location 参数
     */
    private Map<String, String> buildLocation(BigDecimal longitude, BigDecimal latitude) {

        Map<String, String> params = new HashMap<>();
        params.put("location", longitude.stripTrailingZeros().toPlainString()
                + ","
                + latitude.stripTrailingZeros().toPlainString());

        return params;
    }

}
