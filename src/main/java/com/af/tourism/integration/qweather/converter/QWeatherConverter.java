package com.af.tourism.integration.qweather.converter;

import com.af.tourism.integration.qweather.dto.QWeatherDailyResponse;
import com.af.tourism.integration.qweather.dto.QWeatherNowResponse;
import com.af.tourism.integration.qweather.dto.QWeatherWarningResponse;
import com.af.tourism.pojo.vo.app.WeatherAlertVO;
import com.af.tourism.pojo.vo.app.WeatherCurrentVO;
import com.af.tourism.pojo.vo.app.WeatherForecastVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * 和风天气实体转业务实体转换器
 */
@Mapper(componentModel = "spring")
public interface QWeatherConverter {

    @Mapping(source = "now.text", target = "weatherText")
    @Mapping(source = "now.temp", target = "temperature")
    @Mapping(source = "now.feelsLike", target = "feelsLike")
    @Mapping(source = "now.humidity", target = "humidity")
    @Mapping(source = "now.windDir", target = "windDirection")
    @Mapping(source = "now.windScale", target = "windLevel", qualifiedByName = "addUnit")
    WeatherCurrentVO toWeatherCurrentVO(QWeatherNowResponse qWeatherNowResponse);

    List<WeatherForecastVO> toWeatherForecastVOList(List<QWeatherDailyResponse.Daily> dailies);

    @Mapping(source = "fxDate", target = "date")
    @Mapping(source = "textDay", target = "weatherTextDay")
    @Mapping(source = "textNight", target = "weatherTextNight")
    @Mapping(source = "tempMin", target = "tempMin")
    @Mapping(source = "tempMax", target = "tempMax")
    WeatherForecastVO toWeatherForecastVO(QWeatherDailyResponse.Daily daily);

    List<WeatherAlertVO> toWeatherAlertVOList(List<QWeatherWarningResponse.Alert> alerts);

    @Mapping(source = "color.code", target = "level")
    @Mapping(source = "headline", target = "title")
    @Mapping(source = "description", target = "description")
    WeatherAlertVO toWeatherAlertVO(QWeatherWarningResponse.Alert alert);


    @Named("addUnit")
    default String appendLevelSuffix(String level) {
        return level == null ? "" : level + "级";
    }
}
