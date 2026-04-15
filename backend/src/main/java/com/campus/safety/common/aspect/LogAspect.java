package com.campus.safety.common.aspect;

import com.campus.safety.common.annotation.Log;
import com.campus.safety.module.system.entity.SysOperLog;
import com.campus.safety.module.system.mapper.SysOperLogMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class LogAspect {

    private final SysOperLogMapper sysOperLogMapper;
    private final ObjectMapper objectMapper;

    @Around("@annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint point, Log logAnnotation) throws Throwable {
        // 1. 记录开始时间
        long startTime = System.currentTimeMillis();

        // 2. 构建SysOperLog对象
        SysOperLog operLog = new SysOperLog();
        operLog.setModule(logAnnotation.module());
        operLog.setDescription(logAnnotation.description());

        // 3. method = className.methodName
        String className = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        operLog.setMethod(className + "." + methodName);

        // 4-6. 从HttpServletRequest获取信息
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operLog.setRequestMethod(request.getMethod());
                operLog.setOperUrl(request.getRequestURI());
                operLog.setOperIp(getIpAddress(request));
            }
        } catch (Exception e) {
            log.warn("获取请求信息失败: {}", e.getMessage());
        }

        // 7. operParam: JSON序列化参数
        try {
            Object[] args = point.getArgs();
            if (args != null && args.length > 0) {
                String params = objectMapper.writeValueAsString(args);
                // 截断过长的参数
                if (params.length() > 2000) {
                    params = params.substring(0, 2000) + "...";
                }
                operLog.setOperParam(params);
            }
        } catch (Exception e) {
            log.warn("序列化参数失败: {}", e.getMessage());
        }

        Object result = null;
        try {
            // 8. 执行目标方法
            result = point.proceed();

            // 9. 成功
            operLog.setStatus(0);
            try {
                String jsonResult = objectMapper.writeValueAsString(result);
                if (jsonResult.length() > 2000) {
                    jsonResult = jsonResult.substring(0, 2000) + "...";
                }
                operLog.setJsonResult(jsonResult);
            } catch (Exception e) {
                log.warn("序列化结果失败: {}", e.getMessage());
            }

            return result;
        } catch (Throwable e) {
            // 10. 异常
            operLog.setStatus(1);
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 2000) {
                errorMsg = errorMsg.substring(0, 2000);
            }
            operLog.setErrorMsg(errorMsg);
            throw e;
        } finally {
            // 11. 设置操作时间和操作用户，插入数据库
            operLog.setOperTime(LocalDateTime.now());

            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getName() != null) {
                    operLog.setOperUser(authentication.getName());
                }
            } catch (Exception e) {
                log.warn("获取当前用户失败: {}", e.getMessage());
            }

            try {
                sysOperLogMapper.insert(operLog);
            } catch (Exception e) {
                log.error("保存操作日志失败: {}", e.getMessage());
            }
        }
    }

    /**
     * 获取IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理时取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
