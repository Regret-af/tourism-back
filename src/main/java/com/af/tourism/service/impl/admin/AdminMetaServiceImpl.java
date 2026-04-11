package com.af.tourism.service.impl.admin;

import com.af.tourism.common.enums.OperationLogAction;
import com.af.tourism.common.enums.OperationLogModule;
import com.af.tourism.common.enums.OperationLogSource;
import com.af.tourism.common.enums.OptionProvider;
import com.af.tourism.pojo.vo.admin.AdminMetaOptionsVO;
import com.af.tourism.pojo.vo.common.OptionVO;
import com.af.tourism.service.admin.AdminMetaService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端通用元数据服务实现
 */
@Service
public class AdminMetaServiceImpl implements AdminMetaService {

    @Override
    public AdminMetaOptionsVO getOptions() {
        AdminMetaOptionsVO vo = new AdminMetaOptionsVO();
        vo.setOperationLogModules(toOptions(OperationLogModule.values()));
        vo.setOperationLogActions(toOptions(OperationLogAction.values()));
        vo.setOperationLogSources(toOptions(OperationLogSource.values()));
        return vo;
    }

    private <T, E extends OptionProvider<T>> List<OptionVO<T>> toOptions(E[] values) {
        return Arrays.stream(values)
                .map(item -> new OptionVO<>(item.getValue(), item.getLabel()))
                .collect(Collectors.toList());
    }
}
