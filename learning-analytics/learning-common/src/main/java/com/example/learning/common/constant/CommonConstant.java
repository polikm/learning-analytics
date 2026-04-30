package com.example.learning.common.constant;

/**
 * 通用常量
 */
public class CommonConstant {

    private CommonConstant() {
    }

    // ========== 系统相关 ==========

    /**
     * UTF-8 编码
     */
    public static final String UTF8 = "UTF-8";

    /**
     * 成功标记
     */
    public static final Integer SUCCESS = 0;

    /**
     * 失败标记
     */
    public static final Integer FAIL = 1;

    // ========== 租户相关 ==========

    /**
     * 默认租户ID
     */
    public static final Long DEFAULT_TENANT_ID = 1L;

    /**
     * 超级管理员租户ID
     */
    public static final Long SUPER_TENANT_ID = 0L;

    // ========== 用户相关 ==========

    /**
     * 超级管理员ID
     */
    public static final Long SUPER_ADMIN_ID = 1L;

    /**
     * 用户类型 - 系统管理员
     */
    public static final String USER_TYPE_ADMIN = "admin";

    /**
     * 用户类型 - 教师
     */
    public static final String USER_TYPE_TEACHER = "teacher";

    /**
     * 用户类型 - 学生
     */
    public static final String USER_TYPE_STUDENT = "student";

    /**
     * 用户类型 - 家长
     */
    public static final String USER_TYPE_PARENT = "parent";

    // ========== 状态相关 ==========

    /**
     * 状态 - 启用
     */
    public static final Integer STATUS_ENABLED = 1;

    /**
     * 状态 - 禁用
     */
    public static final Integer STATUS_DISABLED = 0;

    /**
     * 删除标记 - 已删除
     */
    public static final Integer DELETED = 1;

    /**
     * 删除标记 - 未删除
     */
    public static final Integer NOT_DELETED = 0;

    // ========== 请求头 ==========

    /**
     * 请求头 - 租户ID
     */
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";

    /**
     * 请求头 - Token
     */
    public static final String HEADER_TOKEN = "Authorization";

    // ========== 缓存相关 ==========

    /**
     * 缓存前缀 - 用户Token
     */
    public static final String CACHE_PREFIX_TOKEN = "learning:token:";

    /**
     * 缓存前缀 - 用户信息
     */
    public static final String CACHE_PREFIX_USER = "learning:user:";

    /**
     * 缓存前缀 - 字典
     */
    public static final String CACHE_PREFIX_DICT = "learning:dict:";

    // ========== SQL相关 ==========

    /**
     * 多租户字段名
     */
    public static final String TENANT_COLUMN = "tenant_id";

    /**
     * 逻辑删除字段名
     */
    public static final String DELETED_COLUMN = "deleted";

}
