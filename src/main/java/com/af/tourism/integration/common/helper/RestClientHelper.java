package com.af.tourism.integration.common.helper;

import com.af.tourism.common.ErrorCode;
import com.af.tourism.integration.common.exception.ThirdPartyApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RestClientHelper {

    private final RestTemplate restTemplate;

    public <T> T doGet(String baseUrl,
                       String path,
                       Map<String, String> queryParams,
                       HttpHeaders headers,
                       Class<T> responseType) {

        String url = buildUrl(baseUrl, path, queryParams);
        HttpHeaders finalHeaders = headers != null ? headers : defaultHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(finalHeaders);

        try {

            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    responseType
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "调用第三方 GET 接口失败: " + url);
        }
    }

    public <T, R> T doPost(String baseUrl,
                           String path,
                           Map<String, String> queryParams,
                           HttpHeaders headers,
                           R requestBody,
                           Class<T> responseType) {

        String url = buildUrl(baseUrl, path, queryParams);
        HttpHeaders finalHeaders = headers != null ? headers : defaultJsonHeaders();
        HttpEntity<R> entity = new HttpEntity<>(requestBody, finalHeaders);

        try {

            ResponseEntity<T> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    responseType
            );

            return response.getBody();
        } catch (RestClientException e) {
            throw new ThirdPartyApiException(ErrorCode.THIRD_PARTY_API_ERROR, "调用第三方 POST 接口失败: " + url);
        }
    }

    protected String buildUrl(String baseUrl, String path, Map<String, String> queryParams) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path(path);

        if (!CollectionUtils.isEmpty(queryParams)) {
            queryParams.forEach((k, v) -> {
                if (v != null) {
                    builder.queryParam(k, v);
                }
            });
        }

        return builder.build().toUriString();
    }

    protected HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(MediaType.parseMediaTypes(MediaType.APPLICATION_JSON_VALUE));
        return headers;
    }

    protected HttpHeaders defaultJsonHeaders() {
        HttpHeaders headers = defaultHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}