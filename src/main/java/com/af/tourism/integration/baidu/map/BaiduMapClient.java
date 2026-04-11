package com.af.tourism.integration.baidu.map;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.exception.BusinessException;
import com.af.tourism.integration.baidu.map.config.BaiduMapProperties;
import com.af.tourism.integration.baidu.map.dto.BaiduMapDetailResponse;
import com.af.tourism.integration.baidu.map.dto.BaiduMapSuggestionResponse;
import com.af.tourism.integration.common.exception.ThirdPartyApiException;
import com.af.tourism.integration.common.helper.RestClientHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 百度地图客户端
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BaiduMapClient {

    private static final String MAP_SUGGESTION = "place/v3/suggestion";
    private static final String MAP_DETAIL = "place/v3/detail";

    private final RestClientHelper restClientHelper;
    private final BaiduMapProperties baiduMapProperties;

    /**
     * 地点输入提示
     * @param query 搜索关键字
     * @return 地点提示列表
     */
    public BaiduMapSuggestionResponse getSuggestion(String query) {

        // 1.拼接请求参数
        Map<String, String> params = new HashMap<>();
        params.put("query", query);
        params.put("region", "北京");
        params.put("region_limit", "false");

        // 2.进行请求
        BaiduMapSuggestionResponse response = get(MAP_SUGGESTION,
                params,
                BaiduMapSuggestionResponse.class);

        // 3.查看请求结果
        if (response.getStatus() != 0) {
            log.warn("百度地图服务请求失败, 错误信息如下:\n" + response.getMessage());
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "百度地图服务请求失败, 错误信息如下:\n" + response.getMessage());
        }

        return response;
    }

    /**
     *  地点详情检索
     * @param uid 百度poi的 id
     * @return 地点详情
     */
    public BaiduMapDetailResponse getDetail(String uid) {
        // 1.构建参数
        Map<String, String> params = new HashMap<>();
        params.put("uid", uid);
        params.put("scope", String.valueOf(2));

        // 2.进行请求
        BaiduMapDetailResponse response = get(MAP_DETAIL,
                params,
                BaiduMapDetailResponse.class);

        // 3.查看请求结果
        if (response.getStatus() != 0) {
            log.warn("百度地图服务请求失败, 错误信息如下:\n" + response.getMessage());
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "百度地图服务请求失败, 错误信息如下:\n" + response.getMessage());
        }

        return response;
    }

    /**
     * 发送 Get 请求
     * @param path 请求路径
     * @param params 请求参数
     * @param responseType 返回类型
     * @return 返回信息
     * @param <T> 返回类型
     */
    private <T> T get(String path, Map<String, String> params, Class<T> responseType) {
        if (!StringUtils.hasText(baiduMapProperties.getAk())) {
            log.error("百度地图未配置ak！");
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "百度地图未配置ak");
        }

        if (params == null || params.isEmpty()) {
            params = new HashMap<>();
        }

        params.put("ak", baiduMapProperties.getAk());

        T response = restClientHelper.doGet(
                baiduMapProperties.getBaseUrl(),
                path,
                params,
                HttpHeaders.EMPTY,
                responseType
        );

        if (response == null) {
            log.warn("百度地图服务返回未空,请求路径为:{}, 参数:{}", baiduMapProperties.getBaseUrl() + path, params);
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "百度地图服务返回为空");
        }

        return response;
    }
}
