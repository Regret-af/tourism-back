package com.af.tourism.converter;

import com.af.tourism.pojo.dto.OperationLogRecordDTO;
import com.af.tourism.pojo.entity.OperationLog;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OperationLogConverter {

    OperationLog toOperationLog(OperationLogRecordDTO operationLogRecordDTO);
}
