package com.campus.safety.module.visitor.service;

import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.visitor.dto.VisitorAuditDTO;
import com.campus.safety.module.visitor.dto.VisitorDTO;
import com.campus.safety.module.visitor.dto.VisitorQueryDTO;
import com.campus.safety.module.visitor.entity.VisitorRecord;

import java.util.Map;

/**
 * 访客记录服务接口
 */
public interface VisitorRecordService {

    /**
     * 分页查询访客记录
     */
    PageResult<VisitorRecord> queryPage(VisitorQueryDTO queryDTO);

    /**
     * 根据ID查询访客记录
     */
    VisitorRecord getById(Long id);

    /**
     * 新增访客记录
     */
    void add(VisitorDTO dto);

    /**
     * 编辑访客记录
     */
    void update(VisitorDTO dto);

    /**
     * 删除访客记录
     */
    void delete(Long id);

    /**
     * 审核访客
     */
    void audit(VisitorAuditDTO dto);

    /**
     * 入校签到
     */
    void checkIn(Long id);

    /**
     * 离校签退
     */
    void checkOut(Long id);

    /**
     * 今日访客统计
     */
    Map<String, Object> todayStats();
}
