package com.example.learning.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应体
 *
 * @param <T> 数据类型
 */
@Data
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 提示信息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 时间戳
     */
    private long timestamp;

    public R() {
        this.timestamp = System.currentTimeMillis();
    }

    public R(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public static <T> R<T> ok() {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    public static <T> R<T> ok(String message, T data) {
        return new R<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    public static <T> R<T> failed() {
        return new R<>(ResultCode.FAILED.getCode(), ResultCode.FAILED.getMessage(), null);
    }

    public static <T> R<T> failed(String message) {
        return new R<>(ResultCode.FAILED.getCode(), message, null);
    }

    public static <T> R<T> failed(int code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R<T> failed(ResultCode resultCode) {
        return new R<>(resultCode.getCode(), resultCode.getMessage(), null);
    }

    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }

}
