package com.campus.safety.module.visitor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.safety.module.visitor.entity.VisitorRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 访客记录Mapper
 */
@Mapper
public interface VisitorRecordMapper extends BaseMapper<VisitorRecord> {
}
