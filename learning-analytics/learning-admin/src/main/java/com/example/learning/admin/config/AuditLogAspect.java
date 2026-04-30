package com.example.learning.admin.config;

import cn.dev33.satoken.stp.StpUtil;
import com.example.learning.admin.annotation.AuditLog;
import com.example.learning.model.entity.SysAuditLog;
import com.example.learning.service.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志AOP切面
 * 通过 @AuditLog 注解自动记录操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(auditLog)")
    public Object around(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        SysAuditLog sysAuditLog = new SysAuditLog();
        sysAuditLog.setOperation(auditLog.value());
        sysAuditLog.setMethod(joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());

        if (request != null) {
            sysAuditLog.setRequestUrl(request.getRequestURI());
            sysAuditLog.setRequestMethod(request.getMethod());
            sysAuditLog.setIpAddress(getIpAddress(request));
            sysAuditLog.setUserAgent(request.getHeader("User-Agent"));
        }

        // 获取当前登录用户信息
        try {
            sysAuditLog.setUserId(StpUtil.getLoginIdAsLong());
            sysAuditLog.setUsername(StpUtil.getLoginIdAsString());
        } catch (Exception e) {
            // 未登录用户
        }

        // 记录请求参数
        try {
            Object[] args = joinPoint.getArgs();
            Map<String, Object> params = new HashMap<>();
            String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            if (paramNames != null) {
                for (int i = 0; i < paramNames.length; i++) {
                    Object arg = args[i];
                    if (arg instanceof MultipartFile) {
                        params.put(paramNames[i], "file:" + ((MultipartFile) arg).getOriginalFilename());
                    } else if (arg instanceof HttpServletRequest) {
                        // 跳过request对象
                    } else {
                        params.put(paramNames[i], arg);
                    }
                }
            }
            String paramsJson = objectMapper.writeValueAsString(params);
            // 限制参数长度，避免过大
            if (paramsJson.length() > 2000) {
                paramsJson = paramsJson.substring(0, 2000) + "...(truncated)";
            }
            sysAuditLog.setRequestParams(paramsJson);
        } catch (Exception e) {
            log.warn("记录审计日志请求参数失败: {}", e.getMessage());
        }

        Object result;
        try {
            // 执行目标方法
            result = joinPoint.proceed();
            sysAuditLog.setStatus(1); // 成功
        } catch (Throwable e) {
            sysAuditLog.setStatus(0); // 失败
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.length() > 2000) {
                errorMsg = errorMsg.substring(0, 2000) + "...(truncated)";
            }
            sysAuditLog.setErrorMsg(errorMsg);
            throw e;
        } finally {
            // 记录执行时长
            long duration = System.currentTimeMillis() - startTime;
            sysAuditLog.setDuration((int) duration);

            // 异步记录审计日志
            try {
                auditLogService.record(sysAuditLog);
            } catch (Exception e) {
                log.error("记录审计日志失败: {}", e.getMessage());
            }
        }

        return result;
    }

    /**
     * 获取客户端真实IP地址
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
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多次反向代理后取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
