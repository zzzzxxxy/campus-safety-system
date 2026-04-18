package com.campus.safety.module.visitor.controller;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.common.result.R;
import com.campus.safety.common.utils.PageResult;
import com.campus.safety.module.visitor.dto.VisitorAuditDTO;
import com.campus.safety.module.visitor.dto.VisitorDTO;
import com.campus.safety.module.visitor.dto.VisitorQueryDTO;
import com.campus.safety.module.visitor.entity.VisitorRecord;
import com.campus.safety.module.visitor.service.VisitorRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * 访客记录控制器
 */
@RestController
@RequestMapping("/visitor")
@RequiredArgsConstructor
public class VisitorRecordController {

    private final VisitorRecordService visitorRecordService;

    /**
     * 分页查询访客记录
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('visitor:record:list')")
    @Log(module = "访客管理", description = "分页查询访客记录")
    public R<PageResult<VisitorRecord>> page(VisitorQueryDTO queryDTO) {
        return R.ok(visitorRecordService.queryPage(queryDTO));
    }

    /**
     * 获取访客记录详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('visitor:record:list')")
    @Log(module = "访客管理", description = "查询访客详情")
    public R<VisitorRecord> getById(@PathVariable Long id) {
        return R.ok(visitorRecordService.getById(id));
    }

    /**
     * 新增访客记录
     */
    @PostMapping
    @PreAuthorize("hasAuthority('visitor:record:add')")
    @Log(module = "访客管理", description = "新增访客记录")
    public R<Void> add(@RequestBody @Valid VisitorDTO dto) {
        visitorRecordService.add(dto);
        return R.ok();
    }

    /**
     * 编辑访客记录
     */
    @PutMapping
    @PreAuthorize("hasAuthority('visitor:record:edit')")
    @Log(module = "访客管理", description = "编辑访客记录")
    public R<Void> update(@RequestBody @Valid VisitorDTO dto) {
        visitorRecordService.update(dto);
        return R.ok();
    }

    /**
     * 删除访客记录
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('visitor:record:delete')")
    @Log(module = "访客管理", description = "删除访客记录")
    public R<Void> delete(@PathVariable Long id) {
        visitorRecordService.delete(id);
        return R.ok();
    }

    /**
     * 审核访客
     */
    @PutMapping("/audit")
    @PreAuthorize("hasAuthority('visitor:record:audit')")
    @Log(module = "访客管理", description = "审核访客记录")
    public R<Void> audit(@RequestBody @Valid VisitorAuditDTO dto) {
        visitorRecordService.audit(dto);
        return R.ok();
    }

    /**
     * 入校签到
     */
    @PutMapping("/check-in/{id}")
    @PreAuthorize("hasAuthority('visitor:record:audit')")
    @Log(module = "访客管理", description = "访客入校签到")
    public R<Void> checkIn(@PathVariable Long id) {
        visitorRecordService.checkIn(id);
        return R.ok();
    }

    /**
     * 离校签退
     */
    @PutMapping("/check-out/{id}")
    @PreAuthorize("hasAuthority('visitor:record:audit')")
    @Log(module = "访客管理", description = "访客离校签退")
    public R<Void> checkOut(@PathVariable Long id) {
        visitorRecordService.checkOut(id);
        return R.ok();
    }

    /**
     * 今日访客统计
     */
    @GetMapping("/today-stats")
    @PreAuthorize("hasAuthority('visitor:record:list')")
    @Log(module = "访客管理", description = "查看今日访客统计")
    public R<Map<String, Object>> todayStats() {
        return R.ok(visitorRecordService.todayStats());
    }

    /**
     * 导出访客记录（CSV）
     */
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('visitor:record:list')")
    @Log(module = "访客管理", description = "导出访客记录")
    public void export(VisitorQueryDTO queryDTO, HttpServletResponse response) throws IOException {
        List<VisitorRecord> list = visitorRecordService.exportList(queryDTO);

        String filename = "visitor_records.csv";
        String encoded = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + encoded);

        // Write UTF-8 BOM for Excel compatibility
        PrintWriter writer = response.getWriter();
        writer.write('\ufeff');

        // header
        writer.println("ID,访客姓名,手机号,身份证,事由,被访人,部门,预计到访,预计离开,实际到访,实际离开,状态,备注");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (VisitorRecord r : list) {
            writer.print(csv(r.getId()));
            writer.print(',');
            writer.print(csv(r.getVisitorName()));
            writer.print(',');
            writer.print(csv(r.getVisitorPhone()));
            writer.print(',');
            writer.print(csv(r.getIdCard()));
            writer.print(',');
            writer.print(csv(r.getReason()));
            writer.print(',');
            writer.print(csv(r.getVisitee()));
            writer.print(',');
            writer.print(csv(r.getDepartment()));
            writer.print(',');
            writer.print(csv(formatDt(r.getVisitTime(), dtf)));
            writer.print(',');
            writer.print(csv(formatDt(r.getLeaveTime(), dtf)));
            writer.print(',');
            writer.print(csv(formatDt(r.getActualVisitTime(), dtf)));
            writer.print(',');
            writer.print(csv(formatDt(r.getActualLeaveTime(), dtf)));
            writer.print(',');
            writer.print(csv(statusLabel(r.getStatus())));
            writer.print(',');
            writer.print(csv(r.getRemark()));
            writer.println();
        }
        writer.flush();
    }

    private static String formatDt(java.time.LocalDateTime dt, DateTimeFormatter dtf) {
        return dt == null ? "" : dt.format(dtf);
    }

    private static String statusLabel(Integer status) {
        if (status == null) return "";
        if (status == 0) return "待审批";
        if (status == 1) return "已通过";
        if (status == 2) return "已拒绝";
        return String.valueOf(status);
    }

    /**
     * Minimal CSV escaping: wrap with quotes if contains comma/quote/newline.
     */
    private static String csv(Object v) {
        if (v == null) return "";
        String s = String.valueOf(v);
        boolean needQuote = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        if (needQuote) {
            s = s.replace("\"", "\"\"");
            return "\"" + s + "\"";
        }
        return s;
    }
}

