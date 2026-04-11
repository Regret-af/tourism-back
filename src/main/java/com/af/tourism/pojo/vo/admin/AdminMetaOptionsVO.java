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

    private List<OptionVO<Integer>> attractionCategoryStatuses;

    private List<OptionVO<Integer>> attractionStatuses;

    private List<OptionVO<Integer>> diaryStatuses;

    private List<OptionVO<Integer>> diaryDeletedStatuses;

    private List<OptionVO<Integer>> diaryVisibilities;

    private List<OptionVO<Integer>> diaryTopStatuses;

    private List<OptionVO<Integer>> diaryCommentStatuses;
}
