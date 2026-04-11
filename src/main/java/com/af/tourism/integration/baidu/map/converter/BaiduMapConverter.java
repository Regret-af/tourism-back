package com.af.tourism.integration.baidu.map.converter;

import com.af.tourism.integration.baidu.map.dto.BaiduMapDetailResponse;
import com.af.tourism.integration.baidu.map.dto.BaiduMapSuggestionResponse;
import com.af.tourism.pojo.vo.admin.MapDetailVO;
import com.af.tourism.pojo.vo.admin.MapSuggestionVO;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring")
public interface BaiduMapConverter {

    List<MapSuggestionVO> toMapSuggestionVOList(List<BaiduMapSuggestionResponse.SuggestionResult> suggestionResultList);

    MapSuggestionVO toMapSuggestionVO(BaiduMapSuggestionResponse.SuggestionResult suggestionResult);

    List<MapDetailVO> toMapDetailVOList(List<BaiduMapDetailResponse.DetailResult> detailResultList);

    @Mapping(source = "uid", target = "baiduUid")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "location.lat", target = "latitude")
    @Mapping(source = "location.lng", target = "longitude")
    @Mapping(source = "address", target = "addressDetail")
    @Mapping(source = "telephone", target = "telephone")
    @Mapping(source = "detail_info.shop_hours", target = "openingHours")
    MapDetailVO toMapDetailVO(BaiduMapDetailResponse.DetailResult detailResult);

    @AfterMapping
    default void enrichMapDetailVO(
            @MappingTarget MapDetailVO mapDetailVO,
            BaiduMapDetailResponse.DetailResult detailResult
    ) {
        String province = detailResult.getProvince();
        String city = detailResult.getCity();
        mapDetailVO.setLocationText(
                (province != null ? province : "") +
                        (city != null ? city : "")
        );
        mapDetailVO.setSourceSyncedAt(LocalDateTime.now());
    }
}
