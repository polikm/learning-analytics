package com.example.learning.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务状态码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    // ========== 成功 ==========
    SUCCESS(200, "操作成功"),

    // ========== 客户端错误 4xx ==========
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未认证，请先登录"),
    FORBIDDEN(403, "无权限访问"),
    NOT_FOUND(404, "资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),

    // ========== 服务端错误 5xx ==========
    FAILED(500, "操作失败"),
    INTERNAL_SERVER_ERROR(500, "服务器内部错误"),

    // ========== 业务错误 1xxx ==========
    TOKEN_INVALID(1001, "Token无效"),
    TOKEN_EXPIRED(1002, "Token已过期"),
    USER_NOT_FOUND(1003, "用户不存在"),
    USER_PASSWORD_ERROR(1004, "用户名或密码错误"),
    USER_DISABLED(1005, "用户已被禁用"),
    USER_ALREADY_EXISTS(1006, "用户名已存在"),

    TENANT_NOT_FOUND(1101, "租户不存在"),
    TENANT_DISABLED(1102, "租户已被禁用"),

    // ========== 数据相关 2xxx ==========
    DATA_NOT_FOUND(2001, "数据不存在"),
    DATA_ALREADY_EXISTS(2002, "数据已存在"),
    DATA_SAVE_FAILED(2003, "数据保存失败"),
    DATA_DELETE_FAILED(2004, "数据删除失败"),
    DATA_IMPORT_FAILED(2005, "数据导入失败"),
    DATA_EXPORT_FAILED(2006, "数据导出失败"),

    // ========== 文件相关 3xxx ==========
    FILE_UPLOAD_FAILED(3001, "文件上传失败"),
    FILE_DOWNLOAD_FAILED(3002, "文件下载失败"),
    FILE_NOT_FOUND(3003, "文件不存在"),
    FILE_TYPE_NOT_ALLOWED(3004, "文件类型不允许"),
    FILE_SIZE_EXCEEDED(3005, "文件大小超限");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 提示信息
     */
    private final String message;

}
