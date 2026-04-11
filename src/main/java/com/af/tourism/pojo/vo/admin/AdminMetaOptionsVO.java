package com.af.tourism.pojo.vo.admin;

import com.af.tourism.pojo.vo.common.OptionVO;
import lombok.Data;

import java.util.List;

/**
 * 管理端通用字典选项
 */
@Data
public class AdminMetaOptionsVO {

    private List<OptionVO<String>> operationLogModules;

    private List<OptionVO<String>> operationLogActions;

    private List<OptionVO<String>> operationLogSources;
}
