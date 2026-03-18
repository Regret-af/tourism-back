package com.af.tourism.utils;

import cn.hutool.crypto.digest.DigestUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ListCacheUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true); // 强制key排序
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);   // 忽略null值
        MAPPER.registerModule(new JavaTimeModule());
    }

    public static String buildHashKey(String prefix, Object params) {
        // 1. 判断参数是否为空
        if (params == null) {
            return prefix + ":empty";
        }

        // 2. 序列化为有序、无空值的 JSON 字符串
        String json = toJSON(params);

        // 3. 进行 MD5 哈希
        String hash = DigestUtil.md5Hex(json);

        // 4. 拼接前缀返回
        return prefix + ":" + hash;
    }

    public static String toJSON(Object object) {
        try {
            return MAPPER.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象序列化失败", e);
        }
    }

    public static <T> T toBean(String json, TypeReference<T> typeReference) {
        try {
            return MAPPER.readValue(json, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("对象反序列化失败", e);
        }
    }
}
